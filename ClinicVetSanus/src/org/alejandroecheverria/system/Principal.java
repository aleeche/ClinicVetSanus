/*
*   Programador: Alejandro Arturo Echeverría Garrido
*   Código Técnico: IN5AM
*   Carné: 2021222
*   Fecha de creación: 2/04/2025
*   Fecha de modificaciónes: 02/04/2025
                             09/04/2025
                             22/04/2025
                             23/04/2025
                             28/04/2025
                             29/04/2025
                             30/04/2025
                             05/05/2025
                             06/05/2025
                             07/05/2025
                             08/05/2025
                             09/05/2025
                             12/05/2025
                             13/05/2025
                             14/05/2025
                             15/05/2025
                             17/05/2025
                             18/05/2025
                             19/05/2025
                             20/05/2025
                             21/05/2025
                             22/05/2025
                             23/05/2025
                             10/06/2025
                             11/06/2025
                             14/06/2025
                             15/06/2025
                             17/06/2025
                             18/06/2025
                             02/07/2025
*/
package org.alejandroecheverria.system;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.alejandroecheverria.controller.CitaController;
import org.alejandroecheverria.controller.ClienteController;
import org.alejandroecheverria.controller.CompraController;
import org.alejandroecheverria.controller.ConsultaController;
import org.alejandroecheverria.controller.EmpleadoController;
import org.alejandroecheverria.controller.FacturaController;
import org.alejandroecheverria.controller.LoginController;
import org.alejandroecheverria.controller.MascotaController;
import org.alejandroecheverria.controller.MedicamentoController;
import org.alejandroecheverria.controller.MenuPrincipalController;
import org.alejandroecheverria.controller.ProgramadorController;
import org.alejandroecheverria.controller.ProveedorController;
import org.alejandroecheverria.controller.RecetaController;
import org.alejandroecheverria.controller.TratamientoController;
import org.alejandroecheverria.controller.UsuarioController;
import org.alejandroecheverria.controller.VacunaController;
import org.alejandroecheverria.controller.VacunacionController;
import org.alejandroecheverria.controller.VeterinarioController;


public class Principal extends Application {
    /*Variable constante*/
    private final String PAQUETE_VISTA = "/org/alejandroecheverria/view/";
    /*Clase CSS del contenido que se agranda proporcionalmente para llenar la ventana*/
    private final String CLASE_ESCALABLE = "escalable";
    /*Clase CSS que limita cuánto se agranda ese contenido (para formularios)*/
    private final String CLASE_ESCALA_MODERADA = "escalaModerada";
    private final double ESCALA_MODERADA = 1.6;
    private Stage escenarioPrincipal;
    private Scene escena;
    /*Grosor de los bordes y barra de título de la ventana (se mide al mostrarla)*/
    private double anchoDecoracion = 0;
    private double altoDecoracion = 0;
    /*Tamaño de diseño de la vista actual, se usa como tamaño mínimo de la ventana*/
    private double anchoMinimo = 0;
    private double altoMinimo = 0;
    /*Indica si se cambió de vista mientras la ventana estaba maximizada*/
    private boolean ajustarAlRestaurar = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        this.escenarioPrincipal = escenarioPrincipal;
//        Parent root = FXMLLoader.load(getClass().getResource("/org/alejandroecheverria/view/MenuPrincipalView.fxml"));
//        Scene escena = new Scene(root);
        escenarioPrincipal.setTitle("Clinic VetSanus");
        escenarioPrincipal.getIcons().add(new Image("/org/alejandroecheverria/image/LogoVetSanusBeta1.png"));
//        escenarioPrincipal.setScene(escena);
//        menuPrincipal();
        /*Permitir maximizar y redimensionar todas las ventanas*/
        escenarioPrincipal.setResizable(true);
        escenarioPrincipal.maximizedProperty().addListener((observable, estabaMaximizada, estaMaximizada) -> {
            if (!estaMaximizada && ajustarAlRestaurar) {
                /*Al restaurar, la ventana toma el tamaño de la vista que se abrió estando maximizada*/
                ajustarAlRestaurar = false;
                Platform.runLater(() -> {
                    Parent vista = escena.getRoot();
                    /*Liberar la vista de la escena actual para pasarla a una nueva*/
                    escena.setRoot(new Group());
                    mostrarConTamanoDeDiseno(vista);
                });
            }
        });
        ventanaLogin();

        escenarioPrincipal.show();
        /*Medir los bordes del sistema para que el tamaño mínimo respete el diseño de cada vista*/
        /*Con escalado de pantalla (125%, 150%) la medida varía en decimales, por eso se deja 1 px de tolerancia*/
        anchoDecoracion = Math.max(0, Math.floor(escenarioPrincipal.getWidth() - escena.getWidth()) - 1);
        altoDecoracion = Math.max(0, Math.floor(escenarioPrincipal.getHeight() - escena.getHeight()) - 1);
        aplicarTamanoMinimo();
    }
    
    public void menuPrincipal(){
        try {
            MenuPrincipalController menu = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml",365,460);
            menu.setEscenarioPrincipal(this);
        } catch (Exception e) {
              e.printStackTrace();
        }
    }
    
    public void ventanaProgramador(){
        try {
            ProgramadorController vistaProgramador = (ProgramadorController) cambiarEscena("ProgramadorView.fxml",509,555);
            vistaProgramador.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaClientes(){
        try {
            ClienteController vistaCliente = (ClienteController) cambiarEscena("ClienteView.fxml",1150,439);
            vistaCliente.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaVeterinarios(){
        try {
            VeterinarioController vistaVeterinario = (VeterinarioController) cambiarEscena("VeterinarioView.fxml",1150,439);
            vistaVeterinario.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaProveedores(){
        try {
            ProveedorController vistaProveedor = (ProveedorController) cambiarEscena("ProveedorView.fxml",1150,439);
            vistaProveedor.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaVacunas(){
        try {
            VacunaController vistaVacuna = (VacunaController) cambiarEscena("VacunaView.fxml",1150,439);
            vistaVacuna.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaEmpleados(){
        try {
            EmpleadoController vistaEmpleado = (EmpleadoController) cambiarEscena("EmpleadoView.fxml",1200,439);
            vistaEmpleado.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaMascotas(){
        try {
            MascotaController vistaMascota = (MascotaController) cambiarEscena("MascotaView.fxml",1150,439);
            vistaMascota.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaConsultas(){
        try {
            ConsultaController vistaConsulta = (ConsultaController) cambiarEscena("ConsultaView.fxml",1225,439);
            vistaConsulta.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaTratamientos(){
        try {
            TratamientoController vistaTratamiento = (TratamientoController) cambiarEscena("TratamientoView.fxml",1150,439);
            vistaTratamiento.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaCitas(){
        try {
            CitaController vistaCita = (CitaController) cambiarEscena("CitaView.fxml",1150,439);
            vistaCita.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaVacunaciones(){
        try {
            VacunacionController vistaVacunacion = (VacunacionController) cambiarEscena("VacunacionView.fxml",1150,439);
            vistaVacunacion.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicamentos(){
        try {
            MedicamentoController vistaMedicamento = (MedicamentoController) cambiarEscena("MedicamentoView.fxml",1150,439);
            vistaMedicamento.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaRecetas(){
        try {
            RecetaController vistaReceta = (RecetaController) cambiarEscena("RecetaView.fxml",1150,439);
            vistaReceta.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaFacturas(){
        try {
            FacturaController vistaFactura = (FacturaController) cambiarEscena("FacturaView.fxml",1150,439);
            vistaFactura.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaCompras(){
        try {
            CompraController vistaCompra = (CompraController) cambiarEscena("CompraView.fxml",1150,439);
            vistaCompra.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaLogin(){
        try {
            LoginController vistaLogin = (LoginController) cambiarEscena("LoginView.fxml",613,387);
            vistaLogin.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaUsuario(){
        try {
            UsuarioController vistaUsuario = (UsuarioController) cambiarEscena("UsuarioView.fxml",734,370);
            vistaUsuario.setEscenarioPrincipal (this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        Parent vista = cargadorFXML.load(archivo);
        aplicarEscalado(vista);
        /*El tamaño de diseño de la vista también es el tamaño mínimo de la ventana*/
        anchoMinimo = ancho;
        altoMinimo = alto;
        /*Liberar el mínimo de la vista anterior para poder ajustar la ventana a la nueva*/
        escenarioPrincipal.setMinWidth(0);
        escenarioPrincipal.setMinHeight(0);
        if (escena != null && escenarioPrincipal.isMaximized()) {
            /*Solo se cambia el contenido para que la ventana siga maximizada*/
            escena.setRoot(vista);
            ajustarAlRestaurar = true;
        } else {
            mostrarConTamanoDeDiseno(vista);
        }
        aplicarTamanoMinimo();
        resultado = (Initializable)cargadorFXML.getController();
        return resultado;
    }

    /*Muestra la vista en una escena con su tamaño de diseño y centra la ventana*/
    private void mostrarConTamanoDeDiseno(Parent vista){
        escena = new Scene(vista, anchoMinimo, altoMinimo);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        /*Centrar la ventana*/
        escenarioPrincipal.centerOnScreen();
    }

    /*
     * Agranda sin deformar el contenido marcado con la clase "escalable" para que llene su
     * contenedor, igual que en el tamaño original. Todos los contenidos de una vista usan la
     * misma escala y nunca se reducen por debajo de su tamaño de diseño.
     */
    private void aplicarEscalado(Parent vista){
        List<Region> contenidos = new ArrayList<>();
        List<Observable> dependencias = new ArrayList<>();
        for (Node nodo : vista.lookupAll("." + CLASE_ESCALABLE)) {
            if (nodo instanceof Region contenido && contenido.getParent() instanceof Region contenedor) {
                contenidos.add(contenido);
                dependencias.add(contenedor.widthProperty());
                dependencias.add(contenedor.heightProperty());
            }
        }
        if (contenidos.isEmpty()) {
            return;
        }
        DoubleBinding escala = Bindings.createDoubleBinding(() -> {
            double factor = Double.MAX_VALUE;
            for (Region contenido : contenidos) {
                Region contenedor = (Region) contenido.getParent();
                Insets margen = contenedor.getInsets();
                double ancho = contenedor.getWidth() - margen.getLeft() - margen.getRight();
                double alto = contenedor.getHeight() - margen.getTop() - margen.getBottom();
                factor = Math.min(factor, Math.min(ancho / contenido.prefWidth(-1), alto / contenido.prefHeight(-1)));
                if (contenido.getStyleClass().contains(CLASE_ESCALA_MODERADA)) {
                    factor = Math.min(factor, ESCALA_MODERADA);
                }
            }
            /*Se redondea a centésimas para que en el tamaño de diseño la escala sea exactamente 1*/
            return Math.max(1.0, Math.floor(factor * 100) / 100);
        }, dependencias.toArray(new Observable[0]));
        for (Region contenido : contenidos) {
            contenido.scaleXProperty().bind(escala);
            contenido.scaleYProperty().bind(escala);
        }
    }

    /*Evita que la ventana se reduzca por debajo del tamaño de diseño de la vista actual*/
    private void aplicarTamanoMinimo(){
        escenarioPrincipal.setMinWidth(anchoMinimo + anchoDecoracion);
        escenarioPrincipal.setMinHeight(altoMinimo + altoDecoracion);
    }
}
