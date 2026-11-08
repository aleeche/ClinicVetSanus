<#
.SYNOPSIS
    Genera el instalador de Clinic VetSanus para Windows.

.DESCRIPTION
    1. Descarga una sola vez las dependencias externas en installer\vendor y verifica
       su SHA-256: MySQL 8.4 LTS (ZIP), JavaFX jmods, VC++ Redistributable e Inno Setup.
    2. Compila el codigo fuente y arma ClinicVetSanus.jar con sus librerias.
    3. Usa jpackage para crear ClinicVetSanus.exe con Java 21 y JavaFX incluidos.
    4. Separa DBVeterinaria2021222.sql en esquema y datos de ejemplo.
    5. Copia una version reducida de MySQL (solo lo necesario para el servidor).
    6. Compila installer\ClinicVetSanus.iss con Inno Setup.

    Resultado: installer\output\ClinicVetSanus-Setup-<version>.exe

.PARAMETER Version
    Version de la aplicacion (formato x.y.z).

.PARAMETER SinInstalador
    Prepara todo en installer\build pero no ejecuta Inno Setup (util para pruebas).

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File installer\build-installer.ps1 -Version 1.0.0
#>
[CmdletBinding()]
param(
    [ValidatePattern('^\d+\.\d+\.\d+$')]
    [string]$Version = '1.0.0',
    [switch]$SinInstalador
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version 3.0
$ProgressPreference = 'SilentlyContinue'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$DirInstaller = $PSScriptRoot
$Raiz = Split-Path -Parent $DirInstaller
$DirFuentes = Join-Path $Raiz 'ClinicVetSanus\src'
$DirLibrerias = Join-Path $Raiz 'APIJavaFXIN5AM'
$ScriptSql = Join-Path $Raiz 'DBVeterinaria2021222.sql'
$Logo = Join-Path $DirFuentes 'org\alejandroecheverria\image\LogoVetSanusBeta1.png'
$DirVendor = Join-Path $DirInstaller 'vendor'
$DirDescargas = Join-Path $DirVendor 'descargas'
$DirBuild = Join-Path $DirInstaller 'build'
$DirOutput = Join-Path $DirInstaller 'output'

$NombreApp = 'ClinicVetSanus'
$ClasePrincipal = 'org.alejandroecheverria.system.Principal'

# Modulos de Java y JavaFX que se incluyen en el runtime (obtenidos con jdeps sobre la app y sus librerias)
$Modulos = @(
    'java.base', 'java.desktop', 'java.sql', 'java.sql.rowset', 'java.naming', 'java.management',
    'java.management.rmi', 'java.prefs', 'java.scripting', 'java.xml', 'java.logging',
    'jdk.unsupported', 'jdk.charsets', 'jdk.localedata', 'jdk.crypto.ec',
    'javafx.controls', 'javafx.fxml'
)

# Opciones de la JVM que antes se configuraban en NetBeans (run.jvmargs).
# vetsanus.instalado hace que Conexion lea %ProgramData%\ClinicVetSanus\config.properties
$OpcionesJava = @(
    '-Dvetsanus.instalado=true',
    '--add-opens=java.base/java.lang=ALL-UNNAMED',
    '--add-opens=java.base/java.util=ALL-UNNAMED'
)

# Dependencias externas: version fija y hash SHA-256 verificado
$Dependencias = @{
    MySQL = @{
        Archivo = 'mysql-8.4.11-winx64.zip'
        Urls    = @(
            'https://cdn.mysql.com/Downloads/MySQL-8.4/mysql-8.4.11-winx64.zip',
            'https://downloads.mysql.com/archives/get/p/23/file/mysql-8.4.11-winx64.zip'
        )
        Sha256  = 'A492371D687D2BAB088B0062581144A0044B8964BAEFDF4FAA579292B423D25C'
    }
    JavaFX = @{
        Archivo = 'openjfx-23.0.2_windows-x64_bin-jmods.zip'
        Urls    = @('https://download2.gluonhq.com/openjfx/23.0.2/openjfx-23.0.2_windows-x64_bin-jmods.zip')
        Sha256  = 'B576F8614698CEEBB4F32D74378A15D4E9F3020BA7ADE31E2346E7F77EA814AD'
    }
    VCRedist = @{
        Archivo = 'vc_redist.x64.exe'
        Urls    = @('https://download.visualstudio.microsoft.com/download/pr/990c720a-46ce-4dc7-87b9-c2eb615d34b9/CC0FF0EB1DC3F5188AE6300FAEF32BF5BEEBA4BDD6E8E445A9184072096B713B/VC_redist.x64.exe')
        Sha256  = 'CC0FF0EB1DC3F5188AE6300FAEF32BF5BEEBA4BDD6E8E445A9184072096B713B'
    }
    InnoSetup = @{
        Archivo = 'innosetup-6.7.4.exe'
        Urls    = @('https://github.com/jrsoftware/issrc/releases/download/is-6_7_4/innosetup-6.7.4.exe')
        Sha256  = 'A549929319F9120716677C685535CA4671E4B937B5FDC87E2585F32D8EB30E8C'
    }
}
$CarpetaMySQL = 'mysql-8.4.11-winx64'
$CarpetaJmods = 'javafx-jmods-23.0.2'

# ---------------------------------------------------------------------------------------------
# Utilidades
# ---------------------------------------------------------------------------------------------

function Write-Paso([string]$Texto) {
    Write-Host ''
    Write-Host "== $Texto" -ForegroundColor Cyan
}

# Ejecuta un programa externo y falla si devuelve un codigo distinto de 0.
# (En PowerShell 5.1 la salida de error de un .exe se convierte en excepcion si ErrorActionPreference=Stop)
function Invoke-Herramienta {
    param([string]$Exe, [string[]]$Argumentos, [string]$Descripcion)
    Write-Host "   $Descripcion"
    $anterior = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $salida = & $Exe @Argumentos 2>&1 | ForEach-Object { "$_" }
        $codigo = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $anterior
    }
    if ($codigo -ne 0) {
        $salida | ForEach-Object { Write-Host "     $_" }
        throw "$Descripcion fallo (codigo de salida $codigo)."
    }
    return $salida
}

function Write-Utf8SinBom([string]$Ruta, [string[]]$Lineas) {
    $codificacion = New-Object System.Text.UTF8Encoding $false
    [IO.File]::WriteAllText($Ruta, (($Lineas -join "`r`n") + "`r`n"), $codificacion)
}

# Descarga (si hace falta) una dependencia y verifica su SHA-256
function Get-Dependencia([string]$Nombre) {
    $dep = $Dependencias[$Nombre]
    $dir = Join-Path $DirDescargas $Nombre.ToLower()
    $ruta = Join-Path $dir $dep.Archivo
    if (-not (Test-Path $ruta)) {
        New-Item -ItemType Directory -Force -Path $dir | Out-Null
        $temporal = "$ruta.part"
        $curl = Join-Path $env:SystemRoot 'System32\curl.exe'
        $descargado = $false
        foreach ($url in $dep.Urls) {
            Write-Host "   Descargando $url"
            try {
                if (Test-Path $curl) {
                    Invoke-Herramienta $curl @('-fsSL', '--retry', '3', '-o', $temporal, $url) "curl $($dep.Archivo)" | Out-Null
                } else {
                    Invoke-WebRequest -Uri $url -OutFile $temporal -UseBasicParsing
                }
                $descargado = $true
                break
            } catch {
                Write-Host "   No se pudo descargar desde $url ($($_.Exception.Message))" -ForegroundColor Yellow
            }
        }
        if (-not $descargado) { throw "No se pudo descargar $($dep.Archivo)." }
        Move-Item -Force $temporal $ruta
    }
    $hash = (Get-FileHash -Algorithm SHA256 -Path $ruta).Hash
    if ($hash -ne $dep.Sha256) {
        Remove-Item -Force $ruta
        throw "El SHA-256 de $($dep.Archivo) no coincide (esperado $($dep.Sha256), obtenido $hash). Se borro el archivo; vuelva a ejecutar el script."
    }
    return $ruta
}

function Expand-Zip([string]$Zip, [string]$Destino, [string[]]$Excluir = @()) {
    New-Item -ItemType Directory -Force -Path $Destino | Out-Null
    $tar = Join-Path $env:SystemRoot 'System32\tar.exe'
    if (Test-Path $tar) {
        $argumentos = @('-xf', $Zip, '-C', $Destino)
        foreach ($patron in $Excluir) { $argumentos += @('--exclude', $patron) }
        Invoke-Herramienta $tar $argumentos "Extrayendo $(Split-Path -Leaf $Zip)" | Out-Null
    } else {
        Write-Host "   Extrayendo $(Split-Path -Leaf $Zip)"
        Expand-Archive -Path $Zip -DestinationPath $Destino -Force
    }
}

# Busca un JDK 21+ con jpackage: primero JAVA_HOME, luego el java del PATH
function Get-JdkHome {
    $candidatos = @()
    if ($env:JAVA_HOME) { $candidatos += $env:JAVA_HOME }
    $java = Get-Command java -ErrorAction SilentlyContinue
    if ($java) {
        $anterior = $ErrorActionPreference
        $ErrorActionPreference = 'Continue'
        $propiedades = & $java.Source -XshowSettings:properties -version 2>&1 | ForEach-Object { "$_" }
        $ErrorActionPreference = $anterior
        $linea = $propiedades | Where-Object { $_ -match '^\s*java\.home\s*=' } | Select-Object -First 1
        if ($linea) { $candidatos += ($linea -split '=', 2)[1].Trim() }
    }
    foreach ($candidato in $candidatos) {
        $jpackage = Join-Path $candidato 'bin\jpackage.exe'
        if (-not (Test-Path $jpackage)) { continue }
        $versionJavac = (Invoke-Herramienta (Join-Path $candidato 'bin\javac.exe') @('-version') 'Version de javac') -join ' '
        if ($versionJavac -match 'javac (\d+)' -and [int]$Matches[1] -ge 21) { return $candidato }
    }
    throw 'No se encontro un JDK 21 o superior con jpackage. Instale el JDK 21 y defina JAVA_HOME.'
}

# Busca el compilador de Inno Setup; si no existe, instala la version portable en installer\vendor\innosetup
function Get-InnoSetup {
    if ($env:ISCC -and (Test-Path $env:ISCC)) { return $env:ISCC }
    $dirPortable = Join-Path $DirVendor 'innosetup'
    $candidatos = @(
        (Join-Path $dirPortable 'ISCC.exe'),
        (Join-Path ${env:ProgramFiles(x86)} 'Inno Setup 6\ISCC.exe'),
        (Join-Path $env:ProgramFiles 'Inno Setup 6\ISCC.exe'),
        (Join-Path $env:LOCALAPPDATA 'Programs\Inno Setup 6\ISCC.exe')
    )
    foreach ($candidato in $candidatos) { if (Test-Path $candidato) { return $candidato } }
    $instalador = Get-Dependencia 'InnoSetup'
    Write-Host "   Instalando Inno Setup (portable) en $dirPortable"
    $proceso = Start-Process -FilePath $instalador -Wait -PassThru -ArgumentList @(
        '/VERYSILENT', '/SUPPRESSMSGBOXES', '/NORESTART', '/CURRENTUSER', '/PORTABLE=1', "/DIR=`"$dirPortable`"")
    $iscc = Join-Path $dirPortable 'ISCC.exe'
    if ($proceso.ExitCode -ne 0 -or -not (Test-Path $iscc)) { throw 'No se pudo instalar Inno Setup.' }
    return $iscc
}

# Crea un .ico (con imagenes PNG de varios tamanos) a partir del logo de la aplicacion
function New-Icono([string]$Png, [string]$Ico) {
    Add-Type -AssemblyName System.Drawing
    $tamanos = @(256, 64, 48, 32, 16)
    $original = [System.Drawing.Image]::FromFile($Png)
    try {
        $imagenes = foreach ($tamano in $tamanos) {
            $bitmap = New-Object System.Drawing.Bitmap $tamano, $tamano
            $grafico = [System.Drawing.Graphics]::FromImage($bitmap)
            $grafico.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
            $grafico.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
            $grafico.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
            $grafico.Clear([System.Drawing.Color]::Transparent)
            $escala = [Math]::Min($tamano / $original.Width, $tamano / $original.Height)
            $ancho = [int][Math]::Round($original.Width * $escala)
            $alto = [int][Math]::Round($original.Height * $escala)
            $grafico.DrawImage($original, [int](($tamano - $ancho) / 2), [int](($tamano - $alto) / 2), $ancho, $alto)
            $grafico.Dispose()
            $memoria = New-Object System.IO.MemoryStream
            $bitmap.Save($memoria, [System.Drawing.Imaging.ImageFormat]::Png)
            $bitmap.Dispose()
            , $memoria.ToArray()
        }
    } finally {
        $original.Dispose()
    }
    $archivo = [IO.File]::Create($Ico)
    $escritor = New-Object System.IO.BinaryWriter $archivo
    try {
        $escritor.Write([UInt16]0)                # reservado
        $escritor.Write([UInt16]1)                # tipo: icono
        $escritor.Write([UInt16]$tamanos.Count)
        $desplazamiento = 6 + 16 * $tamanos.Count
        for ($i = 0; $i -lt $tamanos.Count; $i++) {
            $lado = if ($tamanos[$i] -ge 256) { 0 } else { $tamanos[$i] }   # 0 significa 256
            $escritor.Write([byte]$lado)
            $escritor.Write([byte]$lado)
            $escritor.Write([byte]0)              # sin paleta
            $escritor.Write([byte]0)
            $escritor.Write([UInt16]1)            # planos
            $escritor.Write([UInt16]32)           # bits por pixel
            $escritor.Write([UInt32]$imagenes[$i].Length)
            $escritor.Write([UInt32]$desplazamiento)
            $desplazamiento += $imagenes[$i].Length
        }
        foreach ($imagen in $imagenes) { $escritor.Write($imagen) }
    } finally {
        $escritor.Close()
    }
}

# Separa el script de desarrollo en:
#   01_esquema.sql        tablas y procedimientos (sin DROP DATABASE: nunca debe borrar datos al instalar)
#   02_datos_ejemplo.sql  las llamadas call sp_Agregar.../sp_Editar... que cargan datos de prueba
# Las llamadas sp_Listar/sp_Buscar/sp_Reporte solo consultan, por eso se omiten.
function Split-ScriptSql([string]$Origen, [string]$Destino) {
    $lineas = [IO.File]::ReadAllLines($Origen, [Text.Encoding]::UTF8)
    $esquema = New-Object System.Collections.Generic.List[string]
    $datos = New-Object System.Collections.Generic.List[string]
    $enProcedimiento = $false
    $nombreBase = $null
    $numero = 0
    foreach ($linea in $lineas) {
        $numero++
        $texto = $linea.Trim()
        if ($texto -match '^(?i)delimiter\s+(\S+)') {
            $enProcedimiento = ($Matches[1] -ne ';')
            $esquema.Add($linea)
            continue
        }
        if ($enProcedimiento) { $esquema.Add($linea); continue }
        if ($texto -match '^(?i)drop\s+database\b') { continue }
        if ($texto -match '^(?i)use\s+(\w+)\s*;') { $nombreBase = $Matches[1] }
        if ($texto -match '^(?i)(call\s+(\w+)|insert|update|delete|replace)\b') {
            if (-not $texto.EndsWith(';')) {
                throw "DBVeterinaria2021222.sql linea ${numero}: las sentencias de datos deben ocupar una sola linea."
            }
            if ($Matches[2] -and $Matches[2] -match '^(?i)sp_(Listar|Buscar|Reporte)') { continue }
            $datos.Add($linea)
            continue
        }
        $esquema.Add($linea)
    }
    if ($enProcedimiento) { throw 'DBVeterinaria2021222.sql: falta cerrar un bloque DELIMITER.' }
    if (-not $nombreBase) { throw 'DBVeterinaria2021222.sql: no se encontro la sentencia USE.' }
    $datos.Insert(0, "use $nombreBase;")
    New-Item -ItemType Directory -Force -Path $Destino | Out-Null
    Write-Utf8SinBom (Join-Path $Destino '01_esquema.sql') $esquema.ToArray()
    Write-Utf8SinBom (Join-Path $Destino '02_datos_ejemplo.sql') $datos.ToArray()
    Write-Host "   01_esquema.sql: $($esquema.Count) lineas; 02_datos_ejemplo.sql: $($datos.Count - 1) sentencias"
}

# ---------------------------------------------------------------------------------------------
# Proceso
# ---------------------------------------------------------------------------------------------

Write-Paso '1/7 Verificando herramientas'
$JdkHome = Get-JdkHome
$Javac = Join-Path $JdkHome 'bin\javac.exe'
$Jar = Join-Path $JdkHome 'bin\jar.exe'
$Jpackage = Join-Path $JdkHome 'bin\jpackage.exe'
Write-Host "   JDK: $JdkHome"
$Iscc = $null
if (-not $SinInstalador) {
    $Iscc = Get-InnoSetup
    Write-Host "   Inno Setup: $Iscc"
}

Write-Paso '2/7 Dependencias externas'
$DirJmods = Join-Path $DirVendor "javafx\$CarpetaJmods"
if (-not (Test-Path (Join-Path $DirJmods 'javafx.base.jmod'))) {
    Expand-Zip (Get-Dependencia 'JavaFX') (Join-Path $DirVendor 'javafx')
}
$DirMySQLCompleto = Join-Path $DirVendor "mysql\$CarpetaMySQL"
if (-not (Test-Path (Join-Path $DirMySQLCompleto 'bin\mysqld.exe'))) {
    # Se omiten simbolos de depuracion (.pdb, ~600 MB), librerias de desarrollo y el diccionario japones
    Expand-Zip (Get-Dependencia 'MySQL') (Join-Path $DirVendor 'mysql') @(
        '*.pdb', '*.lib', '*/include/*', '*/lib/mecab/*', '*/plugin/debug/*')
}
$VCRedist = Get-Dependencia 'VCRedist'
Write-Host '   OK'

Write-Paso '3/7 Compilando la aplicacion'
if (Test-Path $DirBuild) { Remove-Item -Recurse -Force $DirBuild }
$DirClases = Join-Path $DirBuild 'clases'
$DirApp = Join-Path $DirBuild 'app'
$DirAppLib = Join-Path $DirApp 'lib'
New-Item -ItemType Directory -Force -Path $DirClases, $DirAppLib | Out-Null

$LibreriasApp = @(Get-ChildItem (Join-Path $DirLibrerias 'APIReport\*.jar')) + @(
    Get-Item (Join-Path $DirLibrerias 'mysql-connector-java-5.1.47-bin.jar'),
    (Join-Path $DirLibrerias 'schudt-javafx-datepicker-0.0.2.jar'))
$LibreriasJavaFX = @(Get-ChildItem (Join-Path $DirLibrerias 'javafx-sdk-23.0.2\lib\javafx.*.jar'))
$ClasspathCompilacion = (@($LibreriasApp) + @($LibreriasJavaFX) | ForEach-Object { $_.FullName }) -join ';'

$Fuentes = Get-ChildItem -Recurse -File -Path $DirFuentes -Filter '*.java'
$ArchivoFuentes = Join-Path $DirBuild 'fuentes.txt'
Write-Utf8SinBom $ArchivoFuentes ($Fuentes | ForEach-Object { '"' + ($_.FullName -replace '\\', '/') + '"' })
Invoke-Herramienta $Javac @('--release', '21', '-encoding', 'UTF-8', '-nowarn', '-d', $DirClases,
    '-cp', $ClasspathCompilacion, "@$ArchivoFuentes") "javac ($($Fuentes.Count) archivos)" | Out-Null

# Recursos (FXML, CSS, imagenes, reportes): igual que NetBeans, todo menos .java y .form
$Recursos = Get-ChildItem -Recurse -File -Path $DirFuentes | Where-Object { $_.Extension -notin '.java', '.form' }
foreach ($recurso in $Recursos) {
    $relativo = $recurso.FullName.Substring($DirFuentes.Length).TrimStart('\')
    $destino = Join-Path $DirClases $relativo
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $destino) | Out-Null
    Copy-Item $recurso.FullName $destino
}
Write-Host "   $($Recursos.Count) recursos copiados"

Write-Paso '4/7 Empaquetando ClinicVetSanus.jar'
foreach ($libreria in $LibreriasApp) { Copy-Item $libreria.FullName $DirAppLib }
# Class-Path del manifiesto (lineas de maximo 72 bytes; las de continuacion empiezan con espacio)
$classPath = 'Class-Path: ' + (($LibreriasApp | ForEach-Object { "lib/$($_.Name)" }) -join ' ')
$lineasManifiesto = @("Main-Class: $ClasePrincipal")
$lineasManifiesto += $classPath.Substring(0, [Math]::Min(70, $classPath.Length))
for ($i = 70; $i -lt $classPath.Length; $i += 69) {
    $lineasManifiesto += ' ' + $classPath.Substring($i, [Math]::Min(69, $classPath.Length - $i))
}
$Manifiesto = Join-Path $DirBuild 'manifest.mf'
Write-Utf8SinBom $Manifiesto $lineasManifiesto
Invoke-Herramienta $Jar @('--create', '--file', (Join-Path $DirApp "$NombreApp.jar"), '--manifest', $Manifiesto,
    '-C', $DirClases, '.') 'jar' | Out-Null

Write-Paso '5/7 Creando ClinicVetSanus.exe con jpackage (Java + JavaFX incluidos)'
$Icono = Join-Path $DirBuild "$NombreApp.ico"
New-Icono $Logo $Icono
$DirImagen = Join-Path $DirBuild 'imagen'
$argumentos = @(
    '--type', 'app-image',
    '--dest', $DirImagen,
    '--name', $NombreApp,
    '--app-version', $Version,
    '--vendor', 'Alejandro Arturo Echeverria Garrido',
    '--description', 'Clinic VetSanus',
    '--icon', $Icono,
    '--input', $DirApp,
    '--main-jar', "$NombreApp.jar",
    '--main-class', $ClasePrincipal,
    '--module-path', "$DirJmods;$(Join-Path $JdkHome 'jmods')",
    '--add-modules', ($Modulos -join ','),
    # Sin --strip-native-commands: se conserva java.exe en el runtime para diagnosticar problemas
    '--jlink-options', '--strip-debug --no-man-pages --no-header-files --include-locales=en,es'
)
foreach ($opcion in $OpcionesJava) { $argumentos += @('--java-options', $opcion) }
Invoke-Herramienta $Jpackage $argumentos 'jpackage' | Out-Null
$DirAplicacion = Join-Path $DirImagen $NombreApp
Write-Host "   $DirAplicacion"

Write-Paso '6/7 Preparando base de datos y MySQL'
Split-ScriptSql $ScriptSql (Join-Path $DirBuild 'database')
$DirMySQL = Join-Path $DirBuild 'mysql'
New-Item -ItemType Directory -Force -Path (Join-Path $DirMySQL 'bin'), (Join-Path $DirMySQL 'lib') | Out-Null
foreach ($programa in 'mysqld.exe', 'mysql.exe', 'mysqladmin.exe', 'mysqldump.exe') {
    Copy-Item (Join-Path $DirMySQLCompleto "bin\$programa") (Join-Path $DirMySQL 'bin')
}
Get-ChildItem (Join-Path $DirMySQLCompleto 'bin\*.dll') | Where-Object { $_.Name -notlike '*-debug.dll' } |
    Copy-Item -Destination (Join-Path $DirMySQL 'bin')
Copy-Item -Recurse (Join-Path $DirMySQLCompleto 'share') $DirMySQL
Copy-Item -Recurse (Join-Path $DirMySQLCompleto 'lib\private') (Join-Path $DirMySQL 'lib')
Copy-Item (Join-Path $DirMySQLCompleto 'LICENSE'), (Join-Path $DirMySQLCompleto 'README') $DirMySQL
$tamanoMySQL = (Get-ChildItem -Recurse -File $DirMySQL | Measure-Object -Sum Length).Sum / 1MB
Write-Host ("   MySQL reducido: {0:N0} MB" -f $tamanoMySQL)
$DirRedist = Join-Path $DirBuild 'redist'
New-Item -ItemType Directory -Force -Path $DirRedist | Out-Null
Copy-Item $VCRedist $DirRedist

if ($SinInstalador) {
    Write-Paso 'Listo (sin instalador)'
    Write-Host "   Archivos preparados en $DirBuild"
    return
}

Write-Paso '7/7 Compilando el instalador con Inno Setup'
New-Item -ItemType Directory -Force -Path $DirOutput | Out-Null
Invoke-Herramienta $Iscc @('/Q', "/DAppVersion=$Version", "/DDirBuild=$DirBuild", "/O$DirOutput",
    (Join-Path $DirInstaller 'ClinicVetSanus.iss')) 'ISCC' | Out-Null
$Setup = Join-Path $DirOutput "$NombreApp-Setup-$Version.exe"
Write-Host ("   {0} ({1:N0} MB)" -f $Setup, ((Get-Item $Setup).Length / 1MB)) -ForegroundColor Green
