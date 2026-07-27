package org.alejandroecheverria.controller;

import java.sql.PreparedStatement;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.alejandroecheverria.bean.Usuario;
import org.alejandroecheverria.db.Conexion;
import org.alejandroecheverria.system.Principal;


public class UsuarioController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, GUARDAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    @FXML private TextField txtCodigoUsuario;
    @FXML private TextField txtNombreUsuario;
    @FXML private TextField txtApellidoUsuario;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtPassword;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnHome;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnHome.setDisable(true);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconGuardar.png"));
                imgEliminar.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                tipoDeOperacion = operaciones.GUARDAR;
                
                break;
            case GUARDAR:
                if(guardar() == true){
                limpiarControles();
                desactivarControles();
//                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                ventanaLogin();
                }
                
                break;
        
        }
    
    }
    
    public boolean guardar(){
        Usuario registro = new Usuario();
        if(txtNombreUsuario.getText().trim().isEmpty()|| txtApellidoUsuario.getText().trim().isEmpty() || txtUsuario.getText().trim().isEmpty() || txtPassword.getText().trim().isEmpty() ){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
            
        } else if(txtNombreUsuario.getText().trim().length()>100 || txtApellidoUsuario.getText().trim().length()>100 || txtUsuario.getText().trim().length()>50 || txtPassword.getText().trim().length()>50){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;

        } else {
            registro.setNombreUsuario(txtNombreUsuario.getText().trim());
            registro.setApellidoUsuario(txtApellidoUsuario.getText().trim());
            registro.setUsuarioLogin(txtUsuario.getText().trim());
            registro.setContrasena(txtPassword.getText().trim());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarUsuario(?,?,?,?)}");
                procedimiento.setString(1, registro.getNombreUsuario());
                procedimiento.setString(2, registro.getApellidoUsuario());
                procedimiento.setString(3, registro.getUsuarioLogin());
                procedimiento.setString(4, registro.getContrasena());
                procedimiento.execute();
                return true;
                
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                /*CANCELAR*/
                desactivarControles();
                limpiarControles();
                /*btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");*/
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                
        }
    }
    
    
    
    public void desactivarControles(){
        txtCodigoUsuario.setEditable(false);
        txtNombreUsuario.setEditable(false);
        txtApellidoUsuario.setEditable(false);
        txtUsuario.setEditable(false);
        txtPassword.setEditable(false);
        
    }
    
    public void activarControles(){
        txtCodigoUsuario.setEditable(false);
        txtNombreUsuario.setEditable(true);
        txtApellidoUsuario.setEditable(true);
        txtUsuario.setEditable(true);
        txtPassword.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoUsuario.clear();
        txtNombreUsuario.clear();
        txtApellidoUsuario.clear();
        txtUsuario.clear();
        txtPassword.clear();
    }
    
    public void ventanaLogin(){
        escenarioPrincipal.ventanaLogin();
    
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    
    
}
