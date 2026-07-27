package org.alejandroecheverria.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.alejandroecheverria.system.Principal;



public class MenuPrincipalController implements Initializable {
    private Principal escenarioPrincipal;
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaProgramador(){
        escenarioPrincipal.ventanaProgramador();
    }
    
    public void ventanaClientes(){
        escenarioPrincipal.ventanaClientes();
    }
    
    public void ventanaVeterinarios(){
        escenarioPrincipal.ventanaVeterinarios();
    }
    
    public void ventanaProveedores(){
        escenarioPrincipal.ventanaProveedores();
    }
    
    public void ventanaVacunas(){
        escenarioPrincipal.ventanaVacunas();
    }
    
    public void ventanaEmpleados(){
        escenarioPrincipal.ventanaEmpleados();
    }
    
    public void ventanaMascotas(){
        escenarioPrincipal.ventanaMascotas();
    }
    
    public void ventanaConsultas(){
        escenarioPrincipal.ventanaConsultas();
    }
    
    public void ventanaTratamientos(){
        escenarioPrincipal.ventanaTratamientos();
    }
    
    public void ventanaCitas(){
        escenarioPrincipal.ventanaCitas();
    }
    
    public void ventanaVacunaciones(){
        escenarioPrincipal.ventanaVacunaciones();
    }
    
    public void ventanaMedicamentos(){
        escenarioPrincipal.ventanaMedicamentos();
    }
    
    public void ventanaRecetas(){
        escenarioPrincipal.ventanaRecetas();
    }
    
    public void ventanaFacturas(){
        escenarioPrincipal.ventanaFacturas();
    }
    
    public void ventanaCompras(){
        escenarioPrincipal.ventanaCompras();
    }
    
    
}
