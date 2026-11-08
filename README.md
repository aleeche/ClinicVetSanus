# Clinic VetSanus

Sistema de escritorio para la **gestión integral de una clínica veterinaria**, desarrollado en
**Java + JavaFX** con base de datos **MySQL**. Permite administrar clientes, mascotas,
veterinarios, consultas, tratamientos, citas, vacunaciones, inventario de medicamentos,
recetas, facturación y compras, además de generar reportes en PDF.

> Proyecto académico — Código Técnico IN5AM · Carné 2021222

---

## Tabla de contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Instalador para Windows](#instalador-para-windows)
- [Requisitos previos](#requisitos-previos)
- [Librerías (APIJavaFXIN5AM)](#librerías-apijavafxin5am)
- [Configuración de la base de datos](#configuración-de-la-base-de-datos)
- [Configuración de la conexión](#configuración-de-la-conexión)
- [Compilación y ejecución](#compilación-y-ejecución)
- [Acceso al sistema](#acceso-al-sistema)
- [Reportes](#reportes)
- [Estructura de la base de datos](#estructura-de-la-base-de-datos)
- [Autor](#autor)
- [Licencia](#licencia)

---

## Características

La aplicación se organiza en módulos, cada uno con operaciones **CRUD** (crear, listar,
editar y eliminar) resueltas mediante **procedimientos almacenados** de MySQL:

| Módulo | Descripción |
|--------|-------------|
| **Clientes** | Registro de dueños con datos de contacto y fecha de registro. |
| **Mascotas** | Fichas de mascotas (especie, raza, sexo, peso, etc.) asociadas a un cliente. |
| **Veterinarios** | Personal médico con especialidad, estado y fecha de ingreso. |
| **Empleados** | Personal administrativo de la clínica. |
| **Proveedores** | Proveedores de medicamentos e insumos. |
| **Consultas** | Consultas médicas con motivo, diagnóstico y observaciones. |
| **Tratamientos** | Tratamientos derivados de una consulta. |
| **Citas** | Agenda de citas con estado (Pendiente / Completada / Cancelada). |
| **Vacunas** | Catálogo de vacunas con dosis y frecuencia. |
| **Vacunaciones** | Registro de vacunas aplicadas a cada mascota. |
| **Medicamentos** | Inventario con stock, precio y fecha de vencimiento. |
| **Recetas** | Recetas médicas con dosis, frecuencia y duración. |
| **Facturas** | Facturación a clientes con método de pago. |
| **Compras** | Compras realizadas a proveedores. |
| **Usuarios** | Gestión de usuarios del sistema. |

Otras funcionalidades:

- **Inicio de sesión** con validación de usuario contra la base de datos.
- **Menú principal** con acceso a todos los módulos.
- **Validaciones de datos** en la capa de la interfaz (campos obligatorios, formato de
  teléfono, longitud de campos y dominios de correo permitidos).
- **Generación de reportes en PDF** con JasperReports.
- **Selector de fechas** integrado en los formularios.

---

## Tecnologías

- **Java 21** (`javac.source` / `javac.target` = 21)
- **JavaFX 23.0.2** (`javafx.controls`, `javafx.fxml`) — interfaz de usuario con vistas FXML y estilos CSS
- **MySQL** con `mysql-connector-java 5.1.47`
- **JasperReports 5.6.0** (+ iText, Groovy, Apache POI y Commons) — generación y visualización de reportes
- **schudt-javafx-datepicker 0.0.2** — componente de selección de fechas
- **Apache Ant** (proyecto NetBeans) — sistema de compilación y empaquetado
- **Swing** (`JOptionPane`) — utilizado para diálogos de mensajes y confirmación

---

## Arquitectura del proyecto

El proyecto sigue una organización por capas dentro del paquete `org.alejandroecheverria`:

```
ClinicVetSanus/
├── src/org/alejandroecheverria/
│   ├── system/       # Principal.java — clase Application, punto de entrada y navegación entre vistas
│   ├── bean/         # POJOs / entidades (Cliente, Mascota, Veterinario, ...)
│   ├── controller/   # Controladores JavaFX (lógica de cada vista y llamadas a la BD)
│   ├── db/           # Conexion.java — conexión MySQL (patrón Singleton)
│   ├── report/       # GenerarReporte.java + plantillas JasperReports (.jrxml / .jasper)
│   ├── view/         # Vistas FXML
│   ├── resource/     # Hojas de estilo CSS
│   └── image/        # Iconos e imágenes de la interfaz
├── nbproject/        # Configuración del proyecto NetBeans / Ant
├── build.xml         # Script de compilación Ant
└── manifest.mf
APIJavaFXIN5AM/            # Librerías externas requeridas (JavaFX SDK, MySQL Connector, JasperReports, DatePicker)
DBVeterinaria2021222.sql   # Script completo de la base de datos (esquema, procedimientos y datos de ejemplo)
```

**Flujo general:**

1. `Principal` (extiende `javafx.application.Application`) arranca mostrando la ventana de
   **Login**.
2. Tras iniciar sesión correctamente se carga el **Menú principal**, desde donde se navega a
   cada módulo mediante el método `cambiarEscena(...)`.
3. Cada controlador obtiene la conexión mediante `Conexion.getInstance()` e invoca los
   **procedimientos almacenados** correspondientes para las operaciones CRUD.
4. Los reportes se generan con `GenerarReporte.mostrarReporte(...)`, que carga la plantilla
   `.jasper`, la rellena con la conexión activa y la muestra en un `JasperViewer`.

---

## Instalador para Windows

Para usar la aplicación **no hace falta** instalar Java, MySQL ni las librerías: el instalador
`ClinicVetSanus-Setup-<versión>.exe` incluye todo.

### Instalar la aplicación

1. Ejecuta `ClinicVetSanus-Setup-<versión>.exe` (pide permisos de administrador).
2. Ingresa los datos del **administrador del sistema** (el usuario con el que iniciarás sesión).
3. Opcional: marca **Cargar datos de ejemplo** (incluye además el usuario de prueba `aeche` / `12345`).
4. Al terminar, abre **Clinic VetSanus** desde el menú Inicio o el escritorio.

Qué instala:

| Qué | Dónde |
|-----|-------|
| Aplicación (con Java 21 y JavaFX incluidos) | `C:\Program Files\ClinicVetSanus\` |
| MySQL 8.4, como servicio de Windows **`ClinicVetSanusDB`** (inicio automático, solo acepta conexiones del propio equipo, puerto 3307 o el siguiente libre) | `C:\Program Files\ClinicVetSanus\mysql\` |
| Datos, `my.ini`, `config.properties` (conexión de la app) y registros | `C:\ProgramData\ClinicVetSanus\` |
| Contraseña del usuario `root` de MySQL (solo legible por administradores) | `C:\ProgramData\ClinicVetSanus\credenciales-root.txt` |

- **Reinstalar o actualizar** conserva la base de datos: solo se reemplazan los programas.
- **Desinstalar** pregunta si se borran también los datos (por defecto **no**).
- Si la base de datos no se pudo configurar, el detalle queda en
  `C:\ProgramData\ClinicVetSanus\logs\instalacion.log` (y el de MySQL en `mysql.err`).

### Generar el instalador

Requisitos: Windows 10/11 de 64 bits, **JDK 21** (con `jpackage`, incluido en el JDK) y conexión a
Internet la primera vez.

```powershell
powershell -ExecutionPolicy Bypass -File installer\build-installer.ps1 -Version 1.0.0
```

Resultado: `installer\output\ClinicVetSanus-Setup-1.0.0.exe` (~80 MB).

El script [`installer/build-installer.ps1`](installer/build-installer.ps1):

1. Descarga una sola vez en `installer\vendor\` (ignorado por git) MySQL 8.4 LTS, los JavaFX
   jmods 23.0.2, el Visual C++ Redistributable e Inno Setup 6 (portable), y verifica su SHA-256.
2. Compila el código y arma `ClinicVetSanus.jar` con sus librerías (no necesita NetBeans).
3. Crea `ClinicVetSanus.exe` con **jpackage**, incluyendo un Java 21 reducido con JavaFX.
4. Separa `DBVeterinaria2021222.sql` en `01_esquema.sql` (sin el `drop database`) y
   `02_datos_ejemplo.sql` (las llamadas `call sp_Agregar...` / `sp_Editar...`).
5. Copia una versión reducida de MySQL (~100 MB en lugar de 1.2 GB).
6. Compila [`installer/ClinicVetSanus.iss`](installer/ClinicVetSanus.iss) con Inno Setup.

Durante la instalación, [`installer/scripts/configurar-bd.ps1`](installer/scripts/configurar-bd.ps1)
inicializa MySQL, registra el servicio, crea la base y los usuarios, y escribe
`config.properties`. La aplicación se conecta con un usuario propio (`vetsanus_app`) con permisos
solo sobre `DBVeterinaria2021222`.

> Para probar el script de base de datos sin instalar nada, usa `-SinServicio`: levanta MySQL como
> proceso normal en la carpeta indicada en `-DirDatos`, sin servicio ni permisos de administrador.

Limitaciones conocidas:

- Solo **Windows 10/11 de 64 bits** y **un solo equipo** (MySQL no acepta conexiones de la red).
- El instalador **no está firmado**: Windows SmartScreen mostrará "Windows protegió su PC"
  (*Más información* → *Ejecutar de todas formas*).
- No hay actualizaciones automáticas ni respaldos programados. Si cambia la estructura de la
  base, una nueva versión debe incluir scripts de migración (el instalador no vuelve a ejecutar
  `01_esquema.sql` sobre una base existente).
- La instalación silenciosa (`/SILENT`) solo sirve para actualizar: una instalación nueva necesita
  los datos del administrador del asistente.
- MySQL Community se distribuye bajo GPLv2; antes de vender la aplicación revisa su licencia.

---

## Requisitos previos

- **JDK 21** o superior.
- **MySQL Server** (con acceso en `localhost:3306`).
- **Apache Ant** o **Apache NetBeans** (el proyecto está configurado como proyecto NetBeans/Ant).
- Las **librerías externas** incluidas en la carpeta [`APIJavaFXIN5AM/`](APIJavaFXIN5AM) de este
  repositorio (JavaFX SDK 23.0.2, MySQL Connector, JasperReports y el DatePicker).

> **Importante:** sin estas librerías el proyecto **no abre ni compila en NetBeans**
> (aparecerán referencias rotas y errores de compilación). Antes de abrir el proyecto sigue
> los pasos de la sección [Librerías (APIJavaFXIN5AM)](#librerías-apijavafxin5am).

---

## Librerías (APIJavaFXIN5AM)

Todas las dependencias necesarias están versionadas en la carpeta **`APIJavaFXIN5AM/`** de la
raíz del repositorio, con la misma estructura que espera el proyecto:

```
APIJavaFXIN5AM/
├── javafx-sdk-23.0.2/                      # SDK completo de JavaFX (bin, lib, legal, src.zip)
│   ├── bin/                                # DLLs nativas (glass, prism, javafx_font, ...)
│   └── lib/                                # JARs y javafx.properties
├── APIReport/                              # Dependencias de JasperReports
├── mysql-connector-java-5.1.47-bin.jar
├── schudt-javafx-datepicker-0.0.2.jar
└── jfoenix-9.0.10.jar
```

### Inventario y uso

| Librería | Versión | Uso en el proyecto |
|----------|---------|--------------------|
| **JavaFX SDK** (`javafx.base`, `javafx.controls`, `javafx.fxml`, `javafx.graphics`, `javafx.media`, `javafx.swing`, `javafx.web`, `javafx-swt`) | 23.0.2 | Toda la interfaz gráfica: `Application`, vistas FXML, controles, CSS y escenas. Los módulos realmente requeridos en ejecución son `javafx.controls` y `javafx.fxml`; la carpeta `bin/` contiene las **DLLs nativas** que JavaFX necesita en tiempo de ejecución en Windows. |
| **MySQL Connector/J** (`mysql-connector-java-5.1.47-bin.jar`) | 5.1.47 | Driver JDBC usado por `db/Conexion.java` para conectarse a `DBVeterinaria2021222` e invocar los procedimientos almacenados (`CallableStatement`). |
| **schudt-javafx-datepicker** (`schudt-javafx-datepicker-0.0.2.jar`) | 0.0.2 | Componente `eu.schudt.javafx.controls.calendar.DatePicker`, usado para seleccionar fechas en los formularios de Clientes, Mascotas, Veterinarios, Consultas, Tratamientos, Citas, Vacunaciones, Medicamentos, Facturas y Compras. |
| **JasperReports** (`jasperreports-5.6.0.jar`) | 5.6.0 | Motor de reportes: carga las plantillas `.jasper`, las rellena con la conexión activa y las muestra en `JasperViewer` desde `report/GenerarReporte.java`. |
| **iText** (`iText-2.1.7.js2.jar`) | 2.1.7 | Exportación de los reportes a **PDF** (requerido por JasperReports). |
| **Groovy** (`groovy-all-2.0.1.jar`) | 2.0.1 | Lenguaje de expresiones que JasperReports utiliza al compilar/evaluar las plantillas. |
| **Apache POI** (`poi-3.7-20101029.jar`) | 3.7 | Exportación de reportes a formatos de Office (Excel/Word) desde JasperReports. |
| **Commons Codec / Collections / Digester / Logging** | 1.15 / 3.2.1 / 2.1 / 1.1 | Dependencias internas de JasperReports (codificación, estructuras de datos, lectura de XML de plantillas y registro de eventos). |
| **JFoenix** (`jfoenix-9.0.10.jar`) | 9.0.10 | Librería opcional de componentes Material Design para JavaFX. **No se usa actualmente** en el código; se incluye por si se agregan controles JFoenix a futuro. |

### Instalación

El proyecto referencia las librerías mediante **rutas absolutas** en
`ClinicVetSanus/nbproject/project.properties`, apuntando a `C:\APIJavaFXIN5AM\`.
La forma más rápida de que todo funcione sin tocar la configuración es respetar esa ruta:

**1. Copia la carpeta a la raíz del disco C:**

```powershell
Copy-Item -Recurse "APIJavaFXIN5AM" "C:\APIJavaFXIN5AM"
```

Debe quedar exactamente así:

```
C:\APIJavaFXIN5AM\javafx-sdk-23.0.2\lib\...
C:\APIJavaFXIN5AM\APIReport\...
C:\APIJavaFXIN5AM\mysql-connector-java-5.1.47-bin.jar
C:\APIJavaFXIN5AM\schudt-javafx-datepicker-0.0.2.jar
```

**2. Crea las bibliotecas en NetBeans** (`Herramientas` → `Bibliotecas` /
*Tools* → *Libraries* → *New Library…*, tipo **Class Libraries**). Los nombres deben
escribirse **tal cual**, porque así los busca el proyecto:

| Nombre de la biblioteca | JARs a agregar (pestaña *Classpath*) |
|-------------------------|--------------------------------------|
| `JavaFX23.0.2IN5AM` | Los 8 JAR de `C:\APIJavaFXIN5AM\javafx-sdk-23.0.2\lib\` (`javafx-swt.jar`, `javafx.base.jar`, `javafx.controls.jar`, `javafx.fxml.jar`, `javafx.graphics.jar`, `javafx.media.jar`, `javafx.swing.jar`, `javafx.web.jar`) |
| `MySQLConnector_5.1.47` | `C:\APIJavaFXIN5AM\mysql-connector-java-5.1.47-bin.jar` |
| `APIReportIN5AM` | Los 8 JAR de `C:\APIJavaFXIN5AM\APIReport\` (`commons-codec-1.15.jar`, `commons-collections-3.2.1.jar`, `commons-digester-2.1.jar`, `commons-logging-1.1.jar`, `groovy-all-2.0.1.jar`, `iText-2.1.7.js2.jar`, `jasperreports-5.6.0.jar`, `poi-3.7-20101029.jar`) |

El JAR del DatePicker **no** forma parte de ninguna biblioteca: el proyecto lo referencia
directamente como archivo (`file.reference.schudt-javafx-datepicker-0.0.2.jar`), por lo que
basta con que exista en `C:\APIJavaFXIN5AM\`.

**3. Abre el proyecto `ClinicVetSanus`** en NetBeans. Si aparece el aviso de
*Project Problems* → *Resolve Problems…*, indica la ubicación de los JAR faltantes.

> **Atajo:** en lugar de crear las bibliotecas a mano puedes declararlas en el archivo
> `build.properties` de tu perfil de NetBeans
> (`C:\Users\<usuario>\AppData\Roaming\NetBeans\<versión>\build.properties`), separando los
> JAR con `;`:
>
> ```properties
> libs.JavaFX23.0.2IN5AM.classpath=C:\\APIJavaFXIN5AM\\javafx-sdk-23.0.2\\lib\\javafx.base.jar;C:\\APIJavaFXIN5AM\\javafx-sdk-23.0.2\\lib\\javafx.controls.jar;...
> libs.MySQLConnector_5.1.47.classpath=C:\\APIJavaFXIN5AM\\mysql-connector-java-5.1.47-bin.jar
> libs.APIReportIN5AM.classpath=C:\\APIJavaFXIN5AM\\APIReport\\jasperreports-5.6.0.jar;...
> ```

### Errores comunes

| Síntoma | Causa | Solución |
|---------|-------|----------|
| NetBeans muestra *Project Problems* / referencias en rojo al abrir el proyecto | Las bibliotecas `JavaFX23.0.2IN5AM`, `MySQLConnector_5.1.47` o `APIReportIN5AM` no existen en tu instalación | Créalas con los nombres exactos del paso 2 |
| `error: package javafx.application does not exist` | Falta la biblioteca de JavaFX en el classpath | Verifica los 8 JAR de `javafx-sdk-23.0.2\lib` |
| `Error: JavaFX runtime components are missing` | El `--module-path` de `run.jvmargs` apunta a una carpeta inexistente | Corrige la ruta en `nbproject/project.properties` |
| `UnsatisfiedLinkError` al abrir la primera ventana | Falta la carpeta `bin/` con las DLLs nativas de JavaFX | Copia el SDK completo, no solo la carpeta `lib` |
| `No suitable driver found for jdbc:mysql://...` | Falta `mysql-connector-java-5.1.47-bin.jar` | Agrega la biblioteca `MySQLConnector_5.1.47` |
| `package eu.schudt.javafx.controls.calendar does not exist` | Falta el JAR del DatePicker o cambió su ruta | Colócalo en `C:\APIJavaFXIN5AM\` o actualiza `file.reference...` |
| `ClassNotFoundException: net.sf.jasperreports...` al generar un reporte | Falta la biblioteca `APIReportIN5AM` | Agrega los 8 JAR de la carpeta `APIReport` |

### Si prefieres usar otra ruta

Si guardas las librerías en una ubicación distinta (por ejemplo dentro del propio repositorio),
debes actualizar **tres** lugares:

1. Las rutas de las tres bibliotecas en el *Library Manager* de NetBeans.
2. `file.reference.schudt-javafx-datepicker-0.0.2.jar` en
   `ClinicVetSanus/nbproject/project.properties`.
3. El `--module-path` de `run.jvmargs` en ese mismo archivo (debe apuntar a la carpeta
   `javafx-sdk-23.0.2\lib`).

---

## Configuración de la base de datos

El script `DBVeterinaria2021222.sql` crea la base de datos completa: esquema, procedimientos
almacenados y datos de ejemplo.

1. Inicia tu servidor MySQL.
2. Ejecuta el script (crea la base de datos `DBVeterinaria2021222`):

   ```bash
   mysql -u root -p < DBVeterinaria2021222.sql
   ```

   > El script comienza con `drop database if exists DBVeterinaria2021222;`, por lo que
   > **recreará** la base de datos desde cero cada vez que se ejecute.

3. El script crea un usuario administrativo y datos de prueba (clientes, usuario del sistema, etc.).

> **Si editas el script**, el instalador lo separa automáticamente en esquema y datos de ejemplo.
> Para que eso funcione, las sentencias de datos fuera de los procedimientos (`call`, `insert`,
> `update`, `delete`) deben ocupar **una sola línea**; si no, `build-installer.ps1` se detiene
> indicando la línea.

---

## Configuración de la conexión

La conexión a la base de datos está en
`ClinicVetSanus/src/org/alejandroecheverria/db/Conexion.java` (patrón **Singleton**).
Los datos se toman, en este orden, de:

1. La propiedad de la JVM `-Dvetsanus.config=<ruta a un .properties>`.
2. La variable de entorno `VETSANUS_CONFIG` (misma idea).
3. En la aplicación instalada: `C:\ProgramData\ClinicVetSanus\config.properties`, que escribe el
   instalador.
4. Si no aplica nada de lo anterior (por ejemplo, al ejecutar desde NetBeans), los valores de
   desarrollo:

- **Host / puerto:** `localhost:3306`
- **Base de datos:** `DBVeterinaria2021222`
- **Usuario:** `quintom`
- **Contraseña:** `admin`

Formato del archivo:

```properties
db.host=127.0.0.1
db.puerto=3307
db.nombre=DBVeterinaria2021222
db.usuario=vetsanus_app
db.contrasena=...
```

Si la conexión falla, la aplicación muestra un mensaje con el servidor y el motivo. Además, se
vuelve a conectar sola si MySQL cierra la conexión (por ejemplo, tras horas sin uso) o si el
servidor se inicia después que la aplicación.

---

## Compilación y ejecución

El proyecto usa **Ant** (a través de la configuración de NetBeans). La clase principal es
`org.alejandroecheverria.system.Principal`.

### Con NetBeans

Con las [librerías ya instaladas](#librerías-apijavafxin5am), abre el proyecto `ClinicVetSanus`
y ejecuta (Run). Las bibliotecas y argumentos de la JVM ya están configurados en el proyecto.

### Con Ant (línea de comandos)

```bash
cd ClinicVetSanus
ant clean
ant jar
ant run
```

JavaFX requiere argumentos de módulos al ejecutar. El proyecto los define en
`nbproject/project.properties` (`run.jvmargs`):

```
--module-path "C:\APIJavaFXIN5AM\javafx-sdk-23.0.2\lib"
--add-modules javafx.controls,javafx.fxml
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
```

Ajusta la ruta del `--module-path` a la ubicación de tu JavaFX SDK.

---

## Acceso al sistema

El script de base de datos crea un usuario de ejemplo (procedimiento `sp_AgregarUsuario`):

- **Usuario:** `aeche`
- **Contraseña:** `12345`

Puedes registrar más usuarios desde el módulo de **Usuarios**.

---

## Reportes

Los reportes se generan con **JasperReports** y se muestran en un visor de escritorio
(`JasperViewer`). Las plantillas se encuentran en
`ClinicVetSanus/src/org/alejandroecheverria/report/`:

- `ReporteClientes` — listado de clientes
- `ReporteMascotas` (+ `SubReporteMascotas`) — listado de mascotas
- `ReporteFacturas` — facturas emitidas
- `ReporteRecetas` — recetas médicas
- `ReporteCarneVacunacion` — carné de vacunación

Cada reporte se apoya en procedimientos almacenados dedicados
(`sp_ReporteRecetas`, `sp_ReporteCarneVacunaciones`, `sp_ReporteFacturas`, etc.).

---

## Estructura de la base de datos

Base de datos: **`DBVeterinaria2021222`**. Todas las operaciones se realizan mediante
**procedimientos almacenados** (`sp_Agregar*`, `sp_Listar*`, `sp_Editar*`, `sp_Eliminar*`,
`sp_Buscar*`).

**Tablas principales:**

`Clientes`, `Veterinarios`, `Proveedores`, `Vacunas`, `Empleados`, `Mascotas`, `Consultas`,
`Tratamientos`, `Citas`, `Vacunaciones`, `Medicamentos`, `Recetas`, `Facturas`, `Compras`,
`Usuario`, `Login`.

**Relaciones destacadas** (claves foráneas con `ON DELETE CASCADE`):

- `Mascotas` → `Clientes`
- `Consultas` → `Mascotas`, `Veterinarios`
- `Tratamientos` → `Consultas`
- `Citas` → `Mascotas`, `Veterinarios`
- `Vacunaciones` → `Mascotas`, `Vacunas`, `Veterinarios`
- `Medicamentos` → `Proveedores`
- `Recetas` → `Consultas`, `Medicamentos`
- `Facturas` → `Clientes`, `Empleados`
- `Compras` → `Proveedores`

---

## Autor

**Alejandro Arturo Echeverría Garrido**
Código Técnico: IN5AM · Carné: 2021222

---

## Licencia

Este proyecto se distribuye bajo la licencia **MIT**. Consulta el archivo [LICENSE](LICENSE)
para más detalles.
