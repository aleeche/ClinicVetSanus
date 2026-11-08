package org.alejandroecheverria.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;




public class Conexion {
    /*Valores de desarrollo: se usan cuando no hay archivo de configuración (por ejemplo, al ejecutar desde NetBeans)*/
    private static final String HOST_DESARROLLO = "localhost";
    private static final String PUERTO_DESARROLLO = "3306";
    private static final String BASE_DATOS = "DBVeterinaria2021222";
    private static final String USUARIO_DESARROLLO = "quintom";
    private static final String CONTRASENA_DESARROLLO = "admin";
    /*Tiempo mínimo entre dos avisos de error, para no mostrar varias ventanas por una sola acción*/
    private static final long PAUSA_ENTRE_AVISOS_MS = 3000;

    private Connection conexion;
    private static Conexion instancia;
    private static long ultimoAviso = 0;

    /*SINGLETON*/
    private Conexion() {
        Path rutaConfiguracion = rutaConfiguracion();
        String host = HOST_DESARROLLO;
        String puerto = PUERTO_DESARROLLO;
        String baseDatos = BASE_DATOS;
        String usuario = USUARIO_DESARROLLO;
        String contrasena = CONTRASENA_DESARROLLO;
        try {
            if (rutaConfiguracion != null) {
                /*Instalación: los datos de conexión los escribe el instalador*/
                Properties configuracion = leerConfiguracion(rutaConfiguracion);
                host = configuracion.getProperty("db.host", host);
                puerto = configuracion.getProperty("db.puerto", puerto);
                baseDatos = configuracion.getProperty("db.nombre", baseDatos);
                usuario = configuracion.getProperty("db.usuario", usuario);
                contrasena = configuracion.getProperty("db.contrasena", contrasena);
            }
            Class.forName("com.mysql.jdbc.Driver");
            /*allowPublicKeyRetrieval permite el inicio de sesión caching_sha2_password de MySQL 8 sin SSL*/
            String url = "jdbc:mysql://" + host + ":" + puerto + "/" + baseDatos
                    + "?useSSL=false&allowPublicKeyRetrieval=true";
            conexion = DriverManager.getConnection(url, usuario, contrasena);
        } catch (Exception e) {
            e.printStackTrace();
            avisarError(e, rutaConfiguracion, host, puerto, baseDatos);
        }
    }

    public static Conexion getInstance(){
        /*Se vuelve a conectar si nunca se logró o si MySQL cerró la conexión (por ejemplo, tras horas sin uso)*/
        if(instancia == null || !instancia.conexionValida())
            instancia = new Conexion();
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    private boolean conexionValida() {
        try {
            return conexion != null && conexion.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /*
     * Ubicación del archivo de configuración, en este orden:
     *  1. Propiedad del sistema -Dvetsanus.config=<ruta>
     *  2. Variable de entorno VETSANUS_CONFIG
     *  3. Aplicación instalada (-Dvetsanus.instalado=true): %ProgramData%\ClinicVetSanus\config.properties
     * Si no aplica ninguna, se usan los valores de desarrollo.
     */
    private static Path rutaConfiguracion() {
        String ruta = System.getProperty("vetsanus.config");
        if (ruta == null || ruta.trim().isEmpty())
            ruta = System.getenv("VETSANUS_CONFIG");
        if (ruta != null && !ruta.trim().isEmpty())
            return Paths.get(ruta.trim());
        if (Boolean.getBoolean("vetsanus.instalado")) {
            String programData = System.getenv("ProgramData");
            if (programData == null || programData.trim().isEmpty())
                programData = "C:\\ProgramData";
            return Paths.get(programData, "ClinicVetSanus", "config.properties");
        }
        return null;
    }

    private static Properties leerConfiguracion(Path ruta) throws IOException {
        if (!Files.isRegularFile(ruta))
            throw new IOException("No existe el archivo de configuración: " + ruta);
        Properties configuracion = new Properties();
        try (InputStream entrada = Files.newInputStream(ruta)) {
            configuracion.load(entrada);
        }
        return configuracion;
    }

    private static void avisarError(Exception e, Path rutaConfiguracion, String host, String puerto, String baseDatos) {
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoAviso < PAUSA_ENTRE_AVISOS_MS)
            return;
        String mensaje = "No se pudo conectar con la base de datos.\n\n"
                + "Servidor: " + host + ":" + puerto + "\n"
                + "Base de datos: " + baseDatos + "\n";
        if (rutaConfiguracion != null)
            mensaje += "Configuración: " + rutaConfiguracion + "\n"
                    + "\nVerifique que el servicio de Windows \"ClinicVetSanusDB\" esté iniciado.\n";
        mensaje += "\nDetalle: " + e.getMessage();
        JOptionPane.showMessageDialog(null, mensaje, "Error de conexión", JOptionPane.ERROR_MESSAGE);
        ultimoAviso = System.currentTimeMillis();
    }
}
