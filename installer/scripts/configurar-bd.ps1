<#
.SYNOPSIS
    Configura la base de datos de Clinic VetSanus durante la instalacion.

.DESCRIPTION
    Lo ejecuta el instalador (Inno Setup) con permisos de administrador.

    Instalacion nueva (no existe <DirDatos>\config.properties):
      1. Elige un puerto libre (desde 3307) y escribe <DirDatos>\my.ini
      2. Inicializa el directorio de datos de MySQL (<DirDatos>\data)
      3. Registra e inicia el servicio de Windows
      4. Crea la base (01_esquema.sql) y, si se pidio, carga los datos de ejemplo
      5. Crea el usuario de la aplicacion (vetsanus_app) y el administrador del sistema
      6. Escribe <DirDatos>\config.properties, que lee la aplicacion
      Si algo falla, deshace lo anterior para poder reintentar desde cero.

    Reinstalacion o actualizacion (ya existen los datos y config.properties):
      conserva los datos; solo reescribe my.ini y vuelve a registrar el servicio.

    Los datos del administrador llegan por variables de entorno, no por linea de comandos,
    para que la contrasena no quede visible en la lista de procesos:
      VETSANUS_ADMIN_NOMBRE, VETSANUS_ADMIN_APELLIDO, VETSANUS_ADMIN_USUARIO, VETSANUS_ADMIN_CONTRASENA

.PARAMETER SinServicio
    Solo para pruebas: inicia mysqld como un proceso normal en lugar de registrar un servicio
    de Windows y no cambia permisos de carpetas (no requiere administrador).
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)] [string]$DirMySQL,
    [Parameter(Mandatory = $true)] [string]$DirDatos,
    [Parameter(Mandatory = $true)] [string]$DirSql,
    [string]$NombreServicio = 'ClinicVetSanusDB',
    [int]$PuertoInicial = 3307,
    [switch]$DatosEjemplo,
    [switch]$SinServicio
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version 3.0

$BaseDatos = 'DBVeterinaria2021222'
$UsuarioApp = 'vetsanus_app'

$DirData = Join-Path $DirDatos 'data'
$DirLogs = Join-Path $DirDatos 'logs'
$MyIni = Join-Path $DirDatos 'my.ini'
$Config = Join-Path $DirDatos 'config.properties'
$CredencialesRoot = Join-Path $DirDatos 'credenciales-root.txt'
$Log = Join-Path $DirLogs 'instalacion.log'
$Mysqld = Join-Path $DirMySQL 'bin\mysqld.exe'
$Mysql = Join-Path $DirMySQL 'bin\mysql.exe'
$Mysqladmin = Join-Path $DirMySQL 'bin\mysqladmin.exe'

# SID de grupos conocidos (no dependen del idioma de Windows)
$SidAdministradores = '*S-1-5-32-544'
$SidSistema = '*S-1-5-18'

# ---------------------------------------------------------------------------------------------
# Utilidades
# ---------------------------------------------------------------------------------------------

function Write-Log([string]$Mensaje) {
    $linea = '[{0:yyyy-MM-dd HH:mm:ss}] {1}' -f (Get-Date), $Mensaje
    Add-Content -Path $Log -Value $linea -Encoding UTF8
    Write-Host $linea
}

function ConvertTo-Argumento([string]$Valor) {
    if ($Valor -notmatch '[\s"]') { return $Valor }
    return '"' + (($Valor -replace '(\\*)"', '$1$1\"') -replace '(\\+)$', '$1$1') + '"'
}

# Ejecuta un programa, opcionalmente enviando un archivo por la entrada estandar (como "mysql < archivo.sql")
function Invoke-Proceso([string]$Exe, [string[]]$Argumentos, [string]$Entrada = '') {
    $info = New-Object System.Diagnostics.ProcessStartInfo
    $info.FileName = $Exe
    $info.Arguments = ($Argumentos | ForEach-Object { ConvertTo-Argumento $_ }) -join ' '
    $info.UseShellExecute = $false
    $info.CreateNoWindow = $true
    $info.RedirectStandardOutput = $true
    $info.RedirectStandardError = $true
    $info.RedirectStandardInput = ($Entrada -ne '')
    $proceso = [System.Diagnostics.Process]::Start($info)
    $salida = $proceso.StandardOutput.ReadToEndAsync()
    $errores = $proceso.StandardError.ReadToEndAsync()
    if ($Entrada -ne '') {
        $bytes = [IO.File]::ReadAllBytes($Entrada)
        $proceso.StandardInput.BaseStream.Write($bytes, 0, $bytes.Length)
        $proceso.StandardInput.Close()
    }
    $proceso.WaitForExit()
    return [pscustomobject]@{ Codigo = $proceso.ExitCode; Salida = $salida.Result; Error = $errores.Result }
}

function Assert-Proceso($Resultado, [string]$Descripcion) {
    if ($Resultado.Codigo -ne 0) {
        throw "$Descripcion fallo (codigo $($Resultado.Codigo)): $($Resultado.Error.Trim()) $($Resultado.Salida.Trim())"
    }
}

function Write-Ascii([string]$Ruta, [string[]]$Lineas) {
    [IO.File]::WriteAllText($Ruta, (($Lineas -join "`r`n") + "`r`n"), [Text.Encoding]::ASCII)
}

function Write-Utf8([string]$Ruta, [string[]]$Lineas) {
    [IO.File]::WriteAllText($Ruta, (($Lineas -join "`r`n") + "`r`n"), (New-Object System.Text.UTF8Encoding $false))
}

function New-Contrasena([int]$Largo = 24) {
    $caracteres = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789'
    $bytes = New-Object byte[] $Largo
    $generador = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $generador.GetBytes($bytes)
    $generador.Dispose()
    return -join ($bytes | ForEach-Object { $caracteres[$_ % $caracteres.Length] })
}

# Texto seguro dentro de comillas simples de MySQL
function ConvertTo-TextoSql([string]$Valor) {
    return "'" + $Valor.Replace('\', '\\').Replace("'", "''") + "'"
}

function Test-PuertoLibre([int]$Puerto) {
    $enUso = [System.Net.NetworkInformation.IPGlobalProperties]::GetIPGlobalProperties().GetActiveTcpListeners() |
        Where-Object { $_.Port -eq $Puerto }
    if ($enUso) { return $false }
    $prueba = New-Object System.Net.Sockets.TcpListener ([System.Net.IPAddress]::Any), $Puerto
    try {
        $prueba.Start()
        return $true
    } catch {
        return $false
    } finally {
        $prueba.Stop()
    }
}

function Get-PuertoLibre {
    for ($puerto = $PuertoInicial; $puerto -lt $PuertoInicial + 100; $puerto++) {
        if (Test-PuertoLibre $puerto) { return $puerto }
    }
    throw "No hay puertos libres entre $PuertoInicial y $($PuertoInicial + 99)."
}

function Get-Propiedad([string]$Archivo, [string]$Patron) {
    if (-not (Test-Path $Archivo)) { return $null }
    foreach ($linea in [IO.File]::ReadAllLines($Archivo)) {
        if ($linea -match $Patron) { return $Matches[1] }
    }
    return $null
}

function Write-MyIni([int]$Puerto) {
    $base = $DirMySQL -replace '\\', '/'
    $datos = $DirData -replace '\\', '/'
    $errores = (Join-Path $DirLogs 'mysql.err') -replace '\\', '/'
    Write-Utf8 $MyIni @(
        '# Generado por el instalador de Clinic VetSanus',
        '[mysqld]',
        "basedir=`"$base`"",
        "datadir=`"$datos`"",
        "port=$Puerto",
        '# Solo acepta conexiones de este equipo',
        'bind-address=127.0.0.1',
        '# Sin X Protocol (puerto 33060) ni binlog: no se usan y evitan conflictos y espacio en disco',
        'mysqlx=OFF',
        'disable-log-bin',
        'character-set-server=utf8mb4',
        'collation-server=utf8mb4_0900_ai_ci',
        'max_connections=50',
        "log-error=`"$errores`""
    )
}

# Archivo temporal de opciones del cliente, para no pasar la contrasena por linea de comandos
function New-ArchivoCliente([int]$Puerto, [string]$Contrasena) {
    $archivo = [IO.Path]::GetTempFileName()
    $lineas = @('[client]', 'user=root', 'host=127.0.0.1', "port=$Puerto")
    if ($Contrasena) { $lineas += "password=$Contrasena" }
    Write-Ascii $archivo $lineas
    return $archivo
}

function Invoke-Sql([string]$ArchivoCliente, [string]$ArchivoSql, [string]$Descripcion) {
    Write-Log "Ejecutando $Descripcion"
    $resultado = Invoke-Proceso $Mysql @("--defaults-extra-file=$ArchivoCliente", '--default-character-set=utf8mb4',
        '--batch') $ArchivoSql
    Assert-Proceso $resultado $Descripcion
}

function Wait-MySQL([string]$ArchivoCliente, [int]$Segundos = 120) {
    $limite = (Get-Date).AddSeconds($Segundos)
    while ((Get-Date) -lt $limite) {
        # "mysqladmin ping" devuelve 0 si el servidor responde, aunque el usuario no tenga acceso
        $resultado = Invoke-Proceso $Mysqladmin @("--defaults-extra-file=$ArchivoCliente", '--connect-timeout=2', 'ping')
        if ($resultado.Codigo -eq 0) { return }
        Start-Sleep -Seconds 1
    }
    throw "MySQL no respondio en $Segundos segundos. Revise $(Join-Path $DirLogs 'mysql.err')"
}

function Remove-Servicio {
    $servicio = Get-Service -Name $NombreServicio -ErrorAction SilentlyContinue
    if (-not $servicio) { return }
    Write-Log "Quitando el servicio existente $NombreServicio"
    if ($servicio.Status -ne 'Stopped') { Stop-Service -Name $NombreServicio -Force }
    Assert-Proceso (Invoke-Proceso (Join-Path $env:SystemRoot 'System32\sc.exe') @('delete', $NombreServicio)) 'sc delete'
    for ($i = 0; $i -lt 30 -and (Get-Service -Name $NombreServicio -ErrorAction SilentlyContinue); $i++) {
        Start-Sleep -Seconds 1
    }
}

function Start-MySQL {
    if ($SinServicio) {
        Write-Log 'Iniciando mysqld como proceso (modo de prueba)'
        Start-Process -FilePath $Mysqld -ArgumentList "--defaults-file=`"$MyIni`"" -WindowStyle Hidden | Out-Null
        return
    }
    Remove-Servicio
    Write-Log "Registrando el servicio $NombreServicio"
    Assert-Proceso (Invoke-Proceso $Mysqld @('--install', $NombreServicio, "--defaults-file=$MyIni")) 'mysqld --install'
    Invoke-Proceso (Join-Path $env:SystemRoot 'System32\sc.exe') @('description', $NombreServicio,
        'Base de datos de Clinic VetSanus (MySQL 8.4)') | Out-Null
    Start-Service -Name $NombreServicio
}

function Stop-MySQL([string]$ArchivoCliente) {
    if ($SinServicio) {
        Invoke-Proceso $Mysqladmin @("--defaults-extra-file=$ArchivoCliente", 'shutdown') | Out-Null
    } else {
        Remove-Servicio
    }
}

# Solo administradores y el sistema (el servicio corre como LocalSystem)
function Set-PermisosRestringidos([string]$Ruta, [switch]$Carpeta) {
    if ($SinServicio) { return }
    $herencia = if ($Carpeta) { '(OI)(CI)F' } else { 'F' }
    Assert-Proceso (Invoke-Proceso (Join-Path $env:SystemRoot 'System32\icacls.exe') @(
        $Ruta, '/inheritance:r', '/grant:r', "${SidAdministradores}:$herencia", "${SidSistema}:$herencia")) "icacls $Ruta"
}

# ---------------------------------------------------------------------------------------------
# Instalacion nueva
# ---------------------------------------------------------------------------------------------

function Install-BaseDatos {
    $nombre = $env:VETSANUS_ADMIN_NOMBRE
    $apellido = $env:VETSANUS_ADMIN_APELLIDO
    $usuario = $env:VETSANUS_ADMIN_USUARIO
    $contrasena = $env:VETSANUS_ADMIN_CONTRASENA
    if (-not $nombre -or -not $apellido -or -not $usuario -or -not $contrasena) {
        throw 'Faltan los datos del administrador (variables VETSANUS_ADMIN_*).'
    }

    $puerto = Get-PuertoLibre
    Write-Log "Instalacion nueva. Puerto de MySQL: $puerto"
    if (Test-Path $DirData) { Remove-Item -Recurse -Force $DirData }
    New-Item -ItemType Directory -Force -Path $DirData | Out-Null
    Set-PermisosRestringidos $DirData -Carpeta
    Write-MyIni $puerto

    $contrasenaRoot = New-Contrasena
    $contrasenaApp = New-Contrasena
    $clienteSinClave = New-ArchivoCliente $puerto ''
    $clienteConClave = New-ArchivoCliente $puerto $contrasenaRoot
    $sqlFinal = [IO.Path]::GetTempFileName()
    $iniciado = $false
    $rootCambiado = $false
    try {
        Write-Log 'Inicializando el directorio de datos'
        Assert-Proceso (Invoke-Proceso $Mysqld @("--defaults-file=$MyIni", '--initialize-insecure', '--console')) 'mysqld --initialize'

        Start-MySQL
        $iniciado = $true
        Wait-MySQL $clienteSinClave

        Invoke-Sql $clienteSinClave (Join-Path $DirSql '01_esquema.sql') 'el esquema de la base de datos'
        if ($DatosEjemplo) {
            Invoke-Sql $clienteSinClave (Join-Path $DirSql '02_datos_ejemplo.sql') 'los datos de ejemplo'
        }

        # SHOW_ROUTINE: el conector de MySQL lee la definicion de los procedimientos para saber sus parametros
        Write-Utf8 $sqlFinal @(
            "CREATE USER '$UsuarioApp'@'localhost' IDENTIFIED BY '$contrasenaApp';",
            "GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON ``$BaseDatos``.* TO '$UsuarioApp'@'localhost';",
            "GRANT SHOW_ROUTINE ON *.* TO '$UsuarioApp'@'localhost';",
            "USE ``$BaseDatos``;",
            ('CALL sp_AgregarUsuario({0}, {1}, {2}, {3});' -f (ConvertTo-TextoSql $nombre),
                (ConvertTo-TextoSql $apellido), (ConvertTo-TextoSql $usuario), (ConvertTo-TextoSql $contrasena)),
            "ALTER USER 'root'@'localhost' IDENTIFIED BY '$contrasenaRoot';"
        )
        Invoke-Sql $clienteSinClave $sqlFinal 'la creacion de usuarios'
        $rootCambiado = $true

        Write-Ascii $CredencialesRoot @(
            'Credenciales del administrador de MySQL de Clinic VetSanus (para respaldos o soporte).',
            'Usuario: root',
            "Contrasena: $contrasenaRoot",
            'Host: 127.0.0.1',
            "Puerto: $puerto"
        )
        Set-PermisosRestringidos $CredencialesRoot

        # config.properties se escribe al final: su existencia indica que la instalacion termino bien
        Write-Ascii $Config @(
            '# Generado por el instalador de Clinic VetSanus. Contiene la contrasena de la base de datos.',
            'db.host=127.0.0.1',
            "db.puerto=$puerto",
            "db.nombre=$BaseDatos",
            "db.usuario=$UsuarioApp",
            "db.contrasena=$contrasenaApp"
        )
        Write-Log 'Base de datos configurada'
    } catch {
        Write-Log "Error en la instalacion nueva, deshaciendo cambios: $($_.Exception.Message)"
        try {
            if ($iniciado) {
                Stop-MySQL $(if ($rootCambiado) { $clienteConClave } else { $clienteSinClave })
                Start-Sleep -Seconds 3
            }
            foreach ($ruta in $DirData, $MyIni, $CredencialesRoot, $Config) {
                if (Test-Path $ruta) { Remove-Item -Recurse -Force $ruta }
            }
        } catch {
            Write-Log "No se pudo deshacer por completo: $($_.Exception.Message)"
        }
        throw
    } finally {
        foreach ($temporal in $clienteSinClave, $clienteConClave, $sqlFinal) {
            Remove-Item -Force $temporal -ErrorAction SilentlyContinue
        }
    }
}

# ---------------------------------------------------------------------------------------------
# Reinstalacion o actualizacion: los datos se conservan
# ---------------------------------------------------------------------------------------------

function Update-BaseDatos {
    $puerto = Get-Propiedad $Config '^\s*db\.puerto\s*=\s*(\d+)'
    if (-not $puerto) { $puerto = Get-Propiedad $MyIni '^\s*port\s*=\s*(\d+)' }
    if (-not $puerto) { throw "No se pudo leer el puerto de $Config" }
    Write-Log "Se conserva la base de datos existente (puerto $puerto)"
    Write-MyIni ([int]$puerto)
    $cliente = New-ArchivoCliente ([int]$puerto) ''
    try {
        Start-MySQL
        Wait-MySQL $cliente
    } finally {
        Remove-Item -Force $cliente -ErrorAction SilentlyContinue
    }
    # Aqui se aplicarian los scripts de migracion (03_*.sql, ...) cuando cambie la estructura de la base.
    Write-Log 'Servicio de base de datos actualizado'
}

# ---------------------------------------------------------------------------------------------

try {
    New-Item -ItemType Directory -Force -Path $DirDatos, $DirLogs | Out-Null
    Write-Log "=== Configuracion de la base de datos (MySQL en $DirMySQL, datos en $DirDatos) ==="
    $hayDatos = Test-Path (Join-Path $DirData 'mysql.ibd')
    $hayConfig = Test-Path $Config
    if ($hayDatos -and $hayConfig) {
        Update-BaseDatos
    } elseif ($hayDatos) {
        throw "Existe una base de datos en $DirData pero falta $Config. No se modifico nada para no perder datos."
    } else {
        Install-BaseDatos
    }
    exit 0
} catch {
    Write-Log "ERROR: $($_.Exception.Message)"
    exit 1
}
