package org.alejandroecheverria.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.alejandroecheverria.system.Principal;
import org.alejandroecheverria.bean.Empleado;
import org.alejandroecheverria.db.Conexion;


public class EmpleadoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Empleado> listaEmpleado;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Button btnHome;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private TextField txtCodigoEmpleado;
    @FXML private TextField txtNombreEmpleado;
    @FXML private TextField txtApellidoEmpleado;
    @FXML private TextField txtCargo;
    @FXML private TextField txtTelefonoEmpleado;
    @FXML private TextField txtCorreoEmpleado;
    @FXML private TableView tblEmpleados;
    @FXML private TableColumn colCodigoEmpleado;
    @FXML private TableColumn colNombreEmpleado;
    @FXML private TableColumn colApellidoEmpleado;
    @FXML private TableColumn colCargo;
    @FXML private TableColumn colTelefonoEmpleado;
    @FXML private TableColumn colCorreoEmpleado;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
        
    }
    
    public void cargarDatos(){
        tblEmpleados.setItems(getEmpleado());
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, Integer>("codigoEmpleado"));
        colNombreEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("nombreEmpleado"));
        colApellidoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("apellidoEmpleado"));
        colCargo.setCellValueFactory(new PropertyValueFactory<Empleado, String>("cargo"));
        colTelefonoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("telefonoEmpleado"));
        colCorreoEmpleado.setCellValueFactory(new PropertyValueFactory<Empleado, String>("correoEmpleado"));
    
    }
    
    public void seleccionarElemento(){
        if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
            txtCodigoEmpleado.setText(String.valueOf(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
            txtNombreEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getNombreEmpleado());
            txtApellidoEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getApellidoEmpleado());
            txtCargo.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCargo());
            txtTelefonoEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getTelefonoEmpleado());
            txtCorreoEmpleado.setText(((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCorreoEmpleado());
        
        }
    
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                /*btnNuevo.setText("Guardar");
                /*btnEliminar.setText("Cancelar");*/
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblEmpleados.setDisable(true);
                btnHome.setDisable(true);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconGuardar.png"));
                imgEliminar.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                tipoDeOperacion = operaciones.GUARDAR;
                
                break;
            case GUARDAR:
                if(guardar() == true){
                limpiarControles();
                desactivarControles();
                /*btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");*/
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblEmpleados.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                }
                break;
        }
    }
    
    public boolean guardar(){
        Empleado registro = new Empleado();
        String correo = txtCorreoEmpleado.getText().trim().toLowerCase();
        String[] dominiosPermitidos = {"@gmail.com", "@yahoo.com", "@outlook.com","hotmail.com","@live.com","@icloud.com","@protonmail.com","@aol.com","@claro.com.gt","@tigo.com.gt","@edu.gt","@kinal.edu.gt","@kinal.org.gt","@mail.com","@mailfence.com","@live.com","@msn.com"};
        boolean tieneArroba = correo.contains("@");
        boolean textoAntesDeArroba = tieneArroba && correo.indexOf('@') > 0;
        boolean esDominioValido = Arrays.stream(dominiosPermitidos).anyMatch(correo::endsWith);

        registro.setNombreEmpleado(txtNombreEmpleado.getText().trim());
        registro.setApellidoEmpleado(txtApellidoEmpleado.getText().trim());
        registro.setCargo(txtCargo.getText().trim());
        registro.setTelefonoEmpleado(txtTelefonoEmpleado.getText().trim());
        registro.setCorreoEmpleado(txtCorreoEmpleado.getText().trim().toLowerCase());
        try {
            if(txtNombreEmpleado.getText().trim().isEmpty() || txtApellidoEmpleado.getText().trim().isEmpty() || txtCargo.getText().trim().isEmpty() || txtTelefonoEmpleado.getText().trim().isEmpty() ||
                    txtCorreoEmpleado.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            } else if (!txtTelefonoEmpleado.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;

            } else if (!esDominioValido || !tieneArroba || !textoAntesDeArroba){
                JOptionPane.showMessageDialog(null, "Ingresar un correo valido");
                return false;
                
            } else if(txtNombreEmpleado.getText().trim().length()>75 || txtApellidoEmpleado.getText().trim().length()>75 || txtCargo.getText().trim().length()>50 || txtCorreoEmpleado.getText().trim().length()>100){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else if(txtTelefonoEmpleado.getText().trim().length() !=8 ){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;
                
            }else{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarEmpleado(?,?,?,?,?)}");
                procedimiento.setString(1, registro.getNombreEmpleado());
                procedimiento.setString(2, registro.getApellidoEmpleado());
                procedimiento.setString(3, registro.getCargo());
                procedimiento.setString(4, registro.getTelefonoEmpleado());
                procedimiento.setString(5, registro.getCorreoEmpleado());
                procedimiento.execute();
                listaEmpleado.add(registro);
                return true;
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void eliminar(){
         switch(tipoDeOperacion){
             case GUARDAR:
                desactivarControles();
                limpiarControles();
                /*btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");*/
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblEmpleados.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblEmpleados.getSelectionModel().clearSelection();
                 break;
             default:
                 if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
                     int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Empleado",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                     if(respuesta == JOptionPane.YES_OPTION){
                         try {
                             PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarEmpleado(?)}");
                             procedimiento.setInt(1, ((Empleado)tblEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
                             procedimiento.execute();
                             listaEmpleado.remove(tblEmpleados.getSelectionModel().getSelectedIndex());
                             tblEmpleados.getSelectionModel().clearSelection();
                             limpiarControles();
                             
                         } catch (Exception e) {
                             e.printStackTrace();
//                             JOptionPane.showMessageDialog(null, "No se puede eliminar este empleado porque está vinculado a otros registros en el sistema.");
                         }
                     
                     }
                 
                 }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                 }
         
         }
    
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblEmpleados.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblEmpleados.setDisable(true);
                    btnHome.setDisable(true);
                    imgEditar.setImage(new Image("/org/alejandroecheverria/image/IconActua.png"));
                    imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento.");
                }
                break;
            case ACTUALIZAR:
                if(actualizar() == true){
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblEmpleados.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblEmpleados.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
        }
    
    
    }
    
    public boolean actualizar(){
        String correo = txtCorreoEmpleado.getText().trim().toLowerCase();
        String[] dominiosPermitidos = {"@gmail.com", "@yahoo.com", "@outlook.com","hotmail.com","@live.com","@icloud.com","@protonmail.com","@aol.com","@claro.com.gt","@tigo.com.gt","@edu.gt","@kinal.edu.gt","@kinal.org.gt","@mail.com","@mailfence.com","@live.com","@msn.com"};
        boolean tieneArroba = correo.contains("@");
        boolean textoAntesDeArroba = tieneArroba && correo.indexOf('@') > 0;
        boolean esDominioValido = Arrays.stream(dominiosPermitidos).anyMatch(correo::endsWith);
        try {
            if(txtNombreEmpleado.getText().trim().isEmpty() || txtApellidoEmpleado.getText().trim().isEmpty() || txtCargo.getText().trim().isEmpty() || txtTelefonoEmpleado.getText().trim().isEmpty() ||
                    txtCorreoEmpleado.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            } else if (!txtTelefonoEmpleado.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;

            } else if (!esDominioValido || !tieneArroba || !textoAntesDeArroba){
                JOptionPane.showMessageDialog(null, "Ingresar un correo valido");
                return false;
                
            } else if(txtNombreEmpleado.getText().trim().length()>75 || txtApellidoEmpleado.getText().trim().length()>75 || txtCargo.getText().trim().length()>50 || txtCorreoEmpleado.getText().trim().length()>100){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else if(txtTelefonoEmpleado.getText().trim().length() !=8 ){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;
                
            }else{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarEmpleado(?,?,?,?,?,?)}");
            Empleado registro = (Empleado)tblEmpleados.getSelectionModel().getSelectedItem();
            registro.setNombreEmpleado(txtNombreEmpleado.getText().trim());
            registro.setApellidoEmpleado(txtApellidoEmpleado.getText().trim());
            registro.setCargo(txtCargo.getText().trim());
            registro.setTelefonoEmpleado(txtTelefonoEmpleado.getText().trim());
            registro.setCorreoEmpleado(txtCorreoEmpleado.getText().trim().toLowerCase());
            procedimiento.setInt(1, registro.getCodigoEmpleado());
            procedimiento.setString(2, registro.getNombreEmpleado());
            procedimiento.setString(3, registro.getApellidoEmpleado());
            procedimiento.setString(4, registro.getCargo());
            procedimiento.setString(5, registro.getTelefonoEmpleado());
            procedimiento.setString(6, registro.getCorreoEmpleado());
            procedimiento.execute();
            return true;
            
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void generarReporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                
                break;
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblEmpleados.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblEmpleados.getSelectionModel().clearSelection();
                break;
            default:
        
        }
    
    }
    
    public ObservableList<Empleado> getEmpleado(){
        ArrayList<Empleado> lista = new ArrayList<Empleado>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarEmpleados}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Empleado(resultado.getInt("codigoEmpleado"),
                                resultado.getString("nombreEmpleado"),
                                resultado.getString("apellidoEmpleado"),
                                resultado.getString("cargo"),
                                resultado.getString("telefonoEmpleado"),
                                resultado.getString("correoEmpleado")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaEmpleado = FXCollections.observableArrayList(lista);
    
    }
    
    public void desactivarControles(){
        txtCodigoEmpleado.setEditable(false);
        txtNombreEmpleado.setEditable(false);
        txtApellidoEmpleado.setEditable(false);
        txtCargo.setEditable(false);
        txtTelefonoEmpleado.setEditable(false);
        txtCorreoEmpleado.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoEmpleado.setEditable(false);
        txtNombreEmpleado.setEditable(true);
        txtApellidoEmpleado.setEditable(true);
        txtCargo.setEditable(true);
        txtTelefonoEmpleado.setEditable(true);
        txtCorreoEmpleado.setEditable(true);
        
    }
    
    public void limpiarControles(){
        txtCodigoEmpleado.clear();
        txtNombreEmpleado.clear();
        txtApellidoEmpleado.clear();
        txtCargo.clear();
        txtTelefonoEmpleado.clear();
        txtCorreoEmpleado.clear();
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    
}
