package org.alejandroecheverria.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.alejandroecheverria.system.Principal;
import org.alejandroecheverria.bean.Compra;
import org.alejandroecheverria.bean.Proveedor;
import org.alejandroecheverria.db.Conexion;


public class CompraController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Compra> listaCompra;
    private ObservableList<Proveedor> listaProveedor;
    private DatePicker fecha;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Button btnHome;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private TextField txtCodigoCompra;
    @FXML private TextField txtTotal;
    @FXML private TextField txtDetalle;
    @FXML private ComboBox cmbCodigoProveedor;
    @FXML private TableView tblCompras;
    @FXML private TableColumn colCodigoCompra;
    @FXML private TableColumn colFechaCompra;
    @FXML private TableColumn colTotal;
    @FXML private TableColumn colDetalle;
    @FXML private TableColumn colCodigoProveedor;
    @FXML private GridPane grpFechaCompra;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/alejandroecheverria/resource/plantilla.css");
        fecha.setDisable(true);
        
        AnchorPane anchorPaneFin = new AnchorPane();
        anchorPaneFin.getChildren().add(fecha);
        AnchorPane.setTopAnchor(fecha, 0.0);
        AnchorPane.setRightAnchor(fecha, 0.0);
        AnchorPane.setBottomAnchor(fecha, 0.0);
        AnchorPane.setLeftAnchor(fecha, 0.0);
        anchorPaneFin.setPrefWidth(0);      
        anchorPaneFin.setMinWidth(310);        
        anchorPaneFin.setMaxWidth(310);        
        fecha.setStyle("-fx-alignment: CENTER;");
        grpFechaCompra.add(anchorPaneFin, 3, 0);
        
        
        Platform.runLater(() -> {
        for (Node node : fecha.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        

        
        cmbCodigoProveedor.setItems(getProveedor());
        cargarDatos();
        
    }
    
    public void cargarDatos(){
        tblCompras.setItems(getCompra());
        colCodigoCompra.setCellValueFactory(new PropertyValueFactory<Compra,Integer>("codigoCompra"));
        colFechaCompra.setCellValueFactory(new PropertyValueFactory<Compra, Date>("fechaCompra"));
        colTotal.setCellValueFactory(new PropertyValueFactory<Compra, Float>("total"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<Compra, String>("detalle"));
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<Compra,Integer>("codigoProveedor"));
        
    }
    
    public void seleccionarElemento(){
        if (tblCompras.getSelectionModel().getSelectedItem() != null){
            txtCodigoCompra.setText(String.valueOf(((Compra)tblCompras.getSelectionModel().getSelectedItem()).getCodigoCompra()));
            txtTotal.setText(String.valueOf(((Compra)tblCompras.getSelectionModel().getSelectedItem()).getTotal()));
            txtDetalle.setText(((Compra)tblCompras.getSelectionModel().getSelectedItem()).getDetalle());
            
            
            cmbCodigoProveedor.getSelectionModel().select(buscarProveedor(((Compra)tblCompras.getSelectionModel().getSelectedItem()).getCodigoProveedor()));
            fecha.selectedDateProperty().set(((Compra)tblCompras.getSelectionModel().getSelectedItem()).getFechaCompra()); 
            
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblCompras.setDisable(true);
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
                tblCompras.setDisable(false);
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
        Compra registro = new Compra();

        if(txtTotal.getText().trim().isEmpty() || txtDetalle.getText().trim().isEmpty() || cmbCodigoProveedor.getSelectionModel().isEmpty() ||fecha.getSelectedDate() == null){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
        } else if(txtDetalle.getText().trim().length()>255 ){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
       
        
        } else if (!txtTotal.getText().trim().matches("\\d{0,8}(\\.\\d{0,2})?")){
            JOptionPane.showMessageDialog(null,"El total contiene un formato inválido (Ejemplo: 10.2) , (max. 8 enteros y 2 decimales).");
            return false;
           
        }else {
            registro.setFechaCompra(fecha.getSelectedDate());
            registro.setTotal(Float.parseFloat(txtTotal.getText().trim()));
            registro.setDetalle(txtDetalle.getText().trim());

            registro.setCodigoProveedor(((Proveedor)cmbCodigoProveedor.getSelectionModel().getSelectedItem()).getCodigoProveedor());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarCompra(?,?,?,?)}");
                procedimiento.setDate(1, new java.sql.Date(registro.getFechaCompra().getTime()));
                procedimiento.setFloat(2, registro.getTotal());
                procedimiento.setString(3, registro.getDetalle());              
                procedimiento.setInt(4, registro.getCodigoProveedor());
                procedimiento.execute();
                listaCompra.add(registro);
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
                desactivarControles();
                limpiarControles();
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblCompras.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblCompras.getSelectionModel().clearSelection();
                
                break;
            default:
                if(tblCompras.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Compra",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarCompra(?)}");
                            procedimiento.setInt(1, ((Compra)tblCompras.getSelectionModel().getSelectedItem()).getCodigoCompra());
                            procedimiento.execute();
                            listaCompra.remove(tblCompras.getSelectionModel().getSelectedIndex());
                            tblCompras.getSelectionModel().clearSelection();
                            limpiarControles();
                            
                        } catch (Exception e) {
                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar esta compra porque está vinculado a otros registros en el sistema.");
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
                if(tblCompras.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblCompras.setDisable(true);
                    btnHome.setDisable(true);
                    imgEditar.setImage(new Image("/org/alejandroecheverria/image/IconActua.png"));
                    imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                    activarControles();
                    cmbCodigoProveedor.setDisable(true);
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
                tblCompras.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblCompras.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
                
        
        }
    
    }
    
    public boolean actualizar(){
        try {
            if(txtTotal.getText().trim().isEmpty() || txtDetalle.getText().trim().isEmpty() ||fecha.getSelectedDate() == null){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
            } else if(txtDetalle.getText().trim().length()>255 ){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
       
        
            } else if (!txtTotal.getText().trim().matches("\\d{0,8}(\\.\\d{0,2})?")){
            JOptionPane.showMessageDialog(null,"El total contiene un formato inválido (Ejemplo: 10.2) , (max. 8 enteros y 2 decimales).");
            return false;
           
            }else {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarCompra(?,?,?,?)}");
                Compra registro = (Compra)tblCompras.getSelectionModel().getSelectedItem();
                registro.setFechaCompra(fecha.getSelectedDate());
                registro.setTotal(Float.parseFloat(txtTotal.getText().trim()));
                registro.setDetalle(txtDetalle.getText().trim());
                
                procedimiento.setInt(1, registro.getCodigoCompra());
                procedimiento.setDate(2, new java.sql.Date(registro.getFechaCompra().getTime()));
                procedimiento.setFloat(3, registro.getTotal());
                procedimiento.setString(4, registro.getDetalle());  
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
                tblCompras.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblCompras.getSelectionModel().clearSelection();
                break;
            default:
        
        }
    
    }
    
    public ObservableList<Compra> getCompra(){
        ArrayList<Compra> lista = new ArrayList<Compra>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarCompras}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Compra(resultado.getInt("codigoCompra"),
                                resultado.getDate("fechaCompra"),
                                resultado.getFloat("total"),
                                resultado.getString("detalle"),
                                resultado.getInt("codigoProveedor")));
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        return listaCompra = FXCollections.observableArrayList(lista);
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
    
    public Proveedor buscarProveedor(int codigoProveedor){
        Proveedor resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarProveedor(?)}");
            procedimiento.setInt(1, codigoProveedor);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()){
                resultado = new Proveedor(registro.getInt("codigoProveedor"),
                                registro.getString("nombreProveedor"),
                                registro.getString("direccionProveedor"),
                                registro.getString("telefonoProveedor"),
                                registro.getString("correoProveedor"));
            
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return resultado;
    }
    
    public void desactivarControles(){
        txtCodigoCompra.setEditable(false);
        fecha.setDisable(true);
        txtTotal.setEditable(false);
        txtDetalle.setEditable(false);
        cmbCodigoProveedor.setDisable(true);
        
    }
    
    public void activarControles(){
        txtCodigoCompra.setEditable(false);
        fecha.setDisable(false);   
        txtTotal.setEditable(true);
        txtDetalle.setEditable(true);
        cmbCodigoProveedor.setDisable(false);
            
    }
    
    public void limpiarControles(){
        txtCodigoCompra.clear();
        fecha.setSelectedDate(null);
        txtTotal.clear();
        txtDetalle.clear();
        cmbCodigoProveedor.setValue(null);
        
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
