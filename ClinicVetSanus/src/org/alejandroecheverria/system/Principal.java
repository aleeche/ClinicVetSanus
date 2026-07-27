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
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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
    private Stage escenarioPrincipal;
    private Scene escena;
    
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
        ventanaLogin();
       
        escenarioPrincipal.show();
        escenarioPrincipal.setResizable(false);
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
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        
        /*Centrar la ventana antes de mostrarla*/
        escenarioPrincipal.centerOnScreen();
        resultado = (Initializable)cargadorFXML.getController();
        return resultado;
    }
}
