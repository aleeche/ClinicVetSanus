; Instalador de Clinic VetSanus (Inno Setup 6)
;
; No se compila directamente: build-installer.ps1 prepara installer\build y luego ejecuta ISCC
; definiendo AppVersion y DirBuild.
;
; Qué hace el instalador:
;   - Copia la aplicación (ClinicVetSanus.exe con Java 21 y JavaFX incluidos) a Program Files.
;   - Copia un MySQL 8.4 reducido y lo registra como el servicio de Windows "ClinicVetSanusDB",
;     que solo escucha en 127.0.0.1 (scripts\configurar-bd.ps1).
;   - Los datos, my.ini y config.properties quedan en %ProgramData%\ClinicVetSanus y se conservan
;     al reinstalar o actualizar.

#ifndef AppVersion
  #define AppVersion "1.0.0"
#endif
#ifndef DirBuild
  #define DirBuild AddBackslash(SourcePath) + "build"
#endif

#define AppNombre "Clinic VetSanus"
#define AppExe "ClinicVetSanus.exe"
#define Servicio "ClinicVetSanusDB"

[Setup]
AppId={{8F3C2A71-5B4E-4D2A-9C1F-6E7B8A9D0C21}
AppName={#AppNombre}
AppVersion={#AppVersion}
AppVerName={#AppNombre} {#AppVersion}
AppPublisher=Alejandro Arturo Echeverría Garrido
VersionInfoVersion={#AppVersion}
DefaultDirName={autopf}\ClinicVetSanus
DefaultGroupName={#AppNombre}
DisableProgramGroupPage=yes
LicenseFile=..\LICENSE
PrivilegesRequired=admin
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
MinVersion=10.0
OutputBaseFilename=ClinicVetSanus-Setup-{#AppVersion}
SetupIconFile={#DirBuild}\ClinicVetSanus.ico
UninstallDisplayIcon={app}\{#AppExe}
UninstallDisplayName={#AppNombre}
Compression=lzma2/max
SolidCompression=yes
WizardStyle=modern
SetupLogging=yes

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"

[Tasks]
Name: "datosejemplo"; Description: "Cargar datos de ejemplo (clientes, mascotas, citas, etc.). Incluye el usuario de prueba aeche / 12345"; GroupDescription: "Base de datos:"; Flags: unchecked; Check: EsInstalacionNueva
Name: "escritorio"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"

[InstallDelete]
; Quita los archivos de una versión anterior. Los datos (en ProgramData) no se tocan.
Type: filesandordirs; Name: "{app}\app"
Type: filesandordirs; Name: "{app}\runtime"
Type: filesandordirs; Name: "{app}\mysql"
Type: filesandordirs; Name: "{app}\database"
Type: filesandordirs; Name: "{app}\scripts"

[Files]
Source: "{#DirBuild}\imagen\ClinicVetSanus\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "{#DirBuild}\mysql\*"; DestDir: "{app}\mysql"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "{#DirBuild}\database\*"; DestDir: "{app}\database"; Flags: ignoreversion
Source: "scripts\configurar-bd.ps1"; DestDir: "{app}\scripts"; Flags: ignoreversion
Source: "{#DirBuild}\redist\vc_redist.x64.exe"; DestDir: "{tmp}"; Flags: deleteafterinstall

[Icons]
Name: "{autoprograms}\{#AppNombre}"; Filename: "{app}\{#AppExe}"
Name: "{autodesktop}\{#AppNombre}"; Filename: "{app}\{#AppExe}"; Tasks: escritorio

[Run]
Filename: "{app}\{#AppExe}"; Description: "{cm:LaunchProgram,{#AppNombre}}"; Flags: nowait postinstall skipifsilent; Check: BaseDatosLista

[Code]
const
  NombreServicio = '{#Servicio}';
  ClaveVCRedist = 'SOFTWARE\Microsoft\VisualStudio\14.0\VC\Runtimes\x64';

var
  PaginaAdmin: TInputQueryWizardPage;
  BaseDatosConfigurada: Boolean;

{ Los datos del administrador se pasan a configurar-bd.ps1 por variables de entorno,
  para que la contraseña no aparezca en la línea de comandos del proceso. }
function SetEnvironmentVariable(lpName: String; lpValue: String): BOOL;
  external 'SetEnvironmentVariableW@kernel32.dll stdcall';

function DirDatos: String;
begin
  Result := ExpandConstant('{commonappdata}\ClinicVetSanus');
end;

{ config.properties lo escribe configurar-bd.ps1 al terminar; si existe, ya hay una base instalada }
function EsInstalacionNueva: Boolean;
begin
  Result := not FileExists(DirDatos + '\config.properties');
end;

function BaseDatosLista: Boolean;
begin
  Result := BaseDatosConfigurada;
end;

{ MySQL 8.4 requiere Visual C++ 2019 o posterior (14.2x en adelante) }
function VCRedistInstalado(Raiz: Integer): Boolean;
var
  Instalado, Mayor, Menor: Cardinal;
begin
  Result := RegQueryDWordValue(Raiz, ClaveVCRedist, 'Installed', Instalado) and (Instalado = 1) and
            RegQueryDWordValue(Raiz, ClaveVCRedist, 'Major', Mayor) and
            RegQueryDWordValue(Raiz, ClaveVCRedist, 'Minor', Menor) and
            ((Mayor > 14) or ((Mayor = 14) and (Menor >= 29)));
end;

function NecesitaVCRedist: Boolean;
begin
  Result := not (VCRedistInstalado(HKLM64) or VCRedistInstalado(HKLM32));
end;

procedure DetenerServicio;
var
  Codigo: Integer;
begin
  Exec(ExpandConstant('{sys}\net.exe'), 'stop ' + NombreServicio, '', SW_HIDE, ewWaitUntilTerminated, Codigo);
end;

procedure InitializeWizard;
begin
  PaginaAdmin := CreateInputQueryPage(wpSelectTasks,
    'Administrador del sistema',
    'Usuario con el que se iniciará sesión en Clinic VetSanus.',
    'Ingrese los datos del primer usuario. Después podrá registrar más usuarios desde el módulo Usuarios.');
  PaginaAdmin.Add('Nombre:', False);
  PaginaAdmin.Add('Apellido:', False);
  PaginaAdmin.Add('Usuario:', False);
  PaginaAdmin.Add('Contraseña:', True);
  PaginaAdmin.Add('Confirmar contraseña:', True);
  { Mismos límites que las columnas de la tabla Usuario }
  PaginaAdmin.Edits[0].MaxLength := 100;
  PaginaAdmin.Edits[1].MaxLength := 100;
  PaginaAdmin.Edits[2].MaxLength := 50;
  PaginaAdmin.Edits[3].MaxLength := 50;
  PaginaAdmin.Edits[4].MaxLength := 50;
end;

function ShouldSkipPage(PageID: Integer): Boolean;
begin
  { En una reinstalación o actualización la base ya tiene usuarios }
  Result := (PageID = PaginaAdmin.ID) and not EsInstalacionNueva;
end;

function NextButtonClick(CurPageID: Integer): Boolean;
var
  I: Integer;
begin
  Result := True;
  if CurPageID <> PaginaAdmin.ID then
    Exit;
  for I := 0 to 3 do
    if Trim(PaginaAdmin.Values[I]) = '' then begin
      MsgBox('Complete todos los campos.', mbError, MB_OK);
      Result := False;
      Exit;
    end;
  if Pos(' ', PaginaAdmin.Values[2]) > 0 then begin
    MsgBox('El usuario no puede contener espacios.', mbError, MB_OK);
    Result := False;
  end else if PaginaAdmin.Values[3] <> PaginaAdmin.Values[4] then begin
    MsgBox('Las contraseñas no coinciden.', mbError, MB_OK);
    Result := False;
  end;
end;

{ Antes de copiar archivos: detener MySQL para poder reemplazar sus archivos en una actualización }
function PrepareToInstall(var NeedsRestart: Boolean): String;
begin
  DetenerServicio;
  Result := '';
end;

procedure ConfigurarBaseDatos;
var
  Codigo: Integer;
  Parametros: String;
begin
  if NecesitaVCRedist then begin
    WizardForm.StatusLabel.Caption := 'Instalando Microsoft Visual C++ Redistributable...';
    { 0 = instalado, 1638 = ya hay una versión más nueva, 3010 = requiere reiniciar }
    Exec(ExpandConstant('{tmp}\vc_redist.x64.exe'), '/install /quiet /norestart', '', SW_HIDE, ewWaitUntilTerminated, Codigo);
    Log('vc_redist.x64.exe terminó con código ' + IntToStr(Codigo));
  end;

  WizardForm.StatusLabel.Caption := 'Configurando la base de datos (puede tardar un minuto)...';
  if EsInstalacionNueva then begin
    SetEnvironmentVariable('VETSANUS_ADMIN_NOMBRE', Trim(PaginaAdmin.Values[0]));
    SetEnvironmentVariable('VETSANUS_ADMIN_APELLIDO', Trim(PaginaAdmin.Values[1]));
    SetEnvironmentVariable('VETSANUS_ADMIN_USUARIO', Trim(PaginaAdmin.Values[2]));
    SetEnvironmentVariable('VETSANUS_ADMIN_CONTRASENA', PaginaAdmin.Values[3]);
  end;

  Parametros := '-NoProfile -NonInteractive -ExecutionPolicy Bypass' +
    ' -File "' + ExpandConstant('{app}\scripts\configurar-bd.ps1') + '"' +
    ' -DirMySQL "' + ExpandConstant('{app}\mysql') + '"' +
    ' -DirDatos "' + DirDatos + '"' +
    ' -DirSql "' + ExpandConstant('{app}\database') + '"' +
    ' -NombreServicio ' + NombreServicio;
  if WizardIsTaskSelected('datosejemplo') then
    Parametros := Parametros + ' -DatosEjemplo';

  if Exec(ExpandConstant('{sys}\WindowsPowerShell\v1.0\powershell.exe'), Parametros, '', SW_HIDE,
          ewWaitUntilTerminated, Codigo) and (Codigo = 0) then
    BaseDatosConfigurada := True
  else begin
    Log('configurar-bd.ps1 terminó con código ' + IntToStr(Codigo));
    MsgBox('La aplicación se instaló, pero no se pudo configurar la base de datos.' + #13#10#13#10 +
           'Revise el registro en:' + #13#10 + DirDatos + '\logs\instalacion.log' + #13#10#13#10 +
           'Puede volver a ejecutar este instalador para reintentarlo.', mbError, MB_OK);
  end;

  SetEnvironmentVariable('VETSANUS_ADMIN_NOMBRE', '');
  SetEnvironmentVariable('VETSANUS_ADMIN_APELLIDO', '');
  SetEnvironmentVariable('VETSANUS_ADMIN_USUARIO', '');
  SetEnvironmentVariable('VETSANUS_ADMIN_CONTRASENA', '');
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
    ConfigurarBaseDatos;
end;

procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
var
  Codigo: Integer;
begin
  if CurUninstallStep = usUninstall then begin
    { Detener y quitar el servicio antes de borrar los archivos de MySQL }
    DetenerServicio;
    Exec(ExpandConstant('{sys}\sc.exe'), 'delete ' + NombreServicio, '', SW_HIDE, ewWaitUntilTerminated, Codigo);
  end;
  if (CurUninstallStep = usPostUninstall) and DirExists(DirDatos) then begin
    { Por defecto (y en desinstalación silenciosa) los datos se conservan }
    if MsgBox('¿Desea eliminar también la base de datos de Clinic VetSanus?' + #13#10#13#10 +
              'Se borrarán todos los clientes, mascotas, citas, facturas y demás registros. ' +
              'Esta acción no se puede deshacer.' + #13#10#13#10 +
              'Elija "No" para conservarlos y recuperarlos si vuelve a instalar.',
              mbConfirmation, MB_YESNO or MB_DEFBUTTON2) = IDYES then
      DelTree(DirDatos, True, True, True);
  end;
end;
