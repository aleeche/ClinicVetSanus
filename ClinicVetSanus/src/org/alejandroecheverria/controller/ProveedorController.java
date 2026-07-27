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
import org.alejandroecheverria.bean.Proveedor;
import org.alejandroecheverria.db.Conexion;


public class ProveedorController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Proveedor> listaProveedor;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Button btnHome;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private TextField txtCodigoProveedor;
    @FXML private TextField txtNombreProveedor;
    @FXML private TextField txtDireccionProveedor;
    @FXML private TextField txtTelefonoProveedor;
    @FXML private TextField txtCorreoProveedor;
    @FXML private TableView tblProveedores;
    @FXML private TableColumn colCodigoProveedor;
    @FXML private TableColumn colNombreProveedor;
    @FXML private TableColumn colDireccionProveedor;
    @FXML private TableColumn colTelefonoProveedor;
    @FXML private TableColumn colCorreoProveedor;
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblProveedores.setItems(getProveedor());
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, Integer>("codigoProveedor"));
        colNombreProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("nombreProveedor"));
        colDireccionProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("direccionProveedor"));
        colTelefonoProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("telefonoProveedor"));
        colCorreoProveedor.setCellValueFactory(new PropertyValueFactory<Proveedor, String>("correoProveedor"));
    }

    public void seleccionarElemento(){
        if(tblProveedores.getSelectionModel().getSelectedItem() != null){
            txtCodigoProveedor.setText(String.valueOf(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getCodigoProveedor()));
            txtNombreProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getNombreProveedor());
            txtDireccionProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getDireccionProveedor());
            txtTelefonoProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getTelefonoProveedor());
            txtCorreoProveedor.setText(((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getCorreoProveedor());
        }
    
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblProveedores.setDisable(true);
                btnHome.setDisable(true);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconGuardar.png"));
                imgEliminar.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(guardar() == true){
                limpiarControles();
                desactivarControles();
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblProveedores.setDisable(false);
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
        Proveedor registro = new Proveedor();
        String correo = txtCorreoProveedor.getText().trim().toLowerCase();
        String[] dominiosPermitidos = {"@gmail.com", "@yahoo.com", "@outlook.com","hotmail.com","@live.com","@icloud.com","@protonmail.com","@aol.com","@claro.com.gt","@tigo.com.gt","@edu.gt","@kinal.edu.gt","@kinal.org.gt","@mail.com","@mailfence.com","@live.com","@msn.com"};
        boolean tieneArroba = correo.contains("@");
        boolean textoAntesDeArroba = tieneArroba && correo.indexOf('@') > 0;
        boolean esDominioValido = Arrays.stream(dominiosPermitidos).anyMatch(correo::endsWith);

        registro.setNombreProveedor(txtNombreProveedor.getText().trim());
        registro.setDireccionProveedor(txtDireccionProveedor.getText().trim());
        registro.setTelefonoProveedor(txtTelefonoProveedor.getText().trim());
        registro.setCorreoProveedor(txtCorreoProveedor.getText().trim().toLowerCase());
        try {
            if(txtNombreProveedor.getText().trim().isEmpty() || txtDireccionProveedor.getText().trim().isEmpty() || txtTelefonoProveedor.getText().trim().isEmpty() || txtCorreoProveedor.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            } else if (!txtTelefonoProveedor.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;

            } else if (!esDominioValido || !tieneArroba || !textoAntesDeArroba){
                JOptionPane.showMessageDialog(null, "Ingresar un correo valido");
                return false;
                
            }else if(txtNombreProveedor.getText().trim().length()>150 || txtDireccionProveedor.getText().trim().length()>255 || txtCorreoProveedor.getText().trim().length()>100){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else if(txtTelefonoProveedor.getText().trim().length() !=8 ){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");  
                return false;
                
            }else{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarProveedor(?,?,?,?)}");
                procedimiento.setString(1, registro.getNombreProveedor());
                procedimiento.setString(2, registro.getDireccionProveedor());
                procedimiento.setString(3, registro.getTelefonoProveedor());
                procedimiento.setString(4, registro.getCorreoProveedor());
                procedimiento.execute();
                listaProveedor.add(registro);
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
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblProveedores.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblProveedores.getSelectionModel().clearSelection();
                break;
            default:
                if(tblProveedores.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Proveedor",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarProveedor(?)}");
                            procedimiento.setInt(1, ((Proveedor)tblProveedores.getSelectionModel().getSelectedItem()).getCodigoProveedor());
                            procedimiento.execute();
                            listaProveedor.remove(tblProveedores.getSelectionModel().getSelectedIndex());
                            tblProveedores.getSelectionModel().clearSelection();
                            limpiarControles();
                        } catch (Exception e) {
                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar este proveedor porque está vinculado a otros registros en el sistema.");
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
                if(tblProveedores.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblProveedores.setDisable(true);
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
                tblProveedores.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblProveedores.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
        
        }
    }
    
    public boolean actualizar(){
        String correo = txtCorreoProveedor.getText().trim().toLowerCase();
        String[] dominiosPermitidos = {"@gmail.com", "@yahoo.com", "@outlook.com","hotmail.com","@live.com","@icloud.com","@protonmail.com","@aol.com","@claro.com.gt","@tigo.com.gt","@edu.gt","@kinal.edu.gt","@kinal.org.gt","@mail.com","@mailfence.com","@live.com","@msn.com"};
        boolean tieneArroba = correo.contains("@");
        boolean textoAntesDeArroba = tieneArroba && correo.indexOf('@') > 0;
        boolean esDominioValido = Arrays.stream(dominiosPermitidos).anyMatch(correo::endsWith);
        try {
            if(txtNombreProveedor.getText().trim().isEmpty() || txtDireccionProveedor.getText().trim().isEmpty() || txtTelefonoProveedor.getText().trim().isEmpty() || txtCorreoProveedor.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            } else if (!txtTelefonoProveedor.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;

            } else if (!esDominioValido || !tieneArroba || !textoAntesDeArroba ){
                JOptionPane.showMessageDialog(null, "Ingresar un correo valido");
                return false;
                
            }else if(txtNombreProveedor.getText().trim().length()>150 || txtDireccionProveedor.getText().trim().length()>255 || txtCorreoProveedor.getText().trim().length()>100){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else if(txtTelefonoProveedor.getText().trim().length() !=8 ){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;
                
            }else{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarProveedor(?,?,?,?,?)}");
                Proveedor registro = (Proveedor)tblProveedores.getSelectionModel().getSelectedItem();
                registro.setNombreProveedor(txtNombreProveedor.getText().trim());
                registro.setDireccionProveedor(txtDireccionProveedor.getText().trim());
                registro.setTelefonoProveedor(txtTelefonoProveedor.getText().trim());
                registro.setCorreoProveedor(txtCorreoProveedor.getText().trim().toLowerCase());
                procedimiento.setInt(1, registro.getCodigoProveedor());
                procedimiento.setString(2, registro.getNombreProveedor());
                procedimiento.setString(3, registro.getDireccionProveedor());
                procedimiento.setString(4, registro.getTelefonoProveedor());
                procedimiento.setString(5, registro.getCorreoProveedor());
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
                tblProveedores.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblProveedores.getSelectionModel().clearSelection();
                break;
        
        
        }
    }
    
    public ObservableList<Proveedor> getProveedor(){
        ArrayList<Proveedor> lista = new ArrayList<Proveedor>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarProveedores}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Proveedor(resultado.getInt("codigoProveedor"),
                                resultado.getString("nombreProveedor"),
                                resultado.getString("direccionProveedor"),
                                resultado.getString("telefonoProveedor"),
                                resultado.getString("correoProveedor")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaProveedor = FXCollections.observableArrayList(lista);
    
    }
    
    public void desactivarControles(){
        txtCodigoProveedor.setEditable(false);
        txtNombreProveedor.setEditable(false);
        txtDireccionProveedor.setEditable(false);
        txtTelefonoProveedor.setEditable(false);
        txtCorreoProveedor.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoProveedor.setEditable(false);
        txtNombreProveedor.setEditable(true);
        txtDireccionProveedor.setEditable(true);
        txtTelefonoProveedor.setEditable(true);
        txtCorreoProveedor.setEditable(true);
                
    }
    
    public void limpiarControles(){
        txtCodigoProveedor.clear();
        txtNombreProveedor.clear();
        txtDireccionProveedor.clear();
        txtTelefonoProveedor.clear();
        txtCorreoProveedor.clear();
        
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
