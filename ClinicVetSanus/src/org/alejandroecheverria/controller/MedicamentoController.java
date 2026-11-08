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
import org.alejandroecheverria.bean.Medicamento;
import org.alejandroecheverria.bean.Proveedor;
import org.alejandroecheverria.db.Conexion;


public class MedicamentoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Medicamento> listaMedicamento;
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
    @FXML private TextField txtCodigoMedicamento;
    @FXML private TextField txtNombreMedicamento;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtStock;
    @FXML private TextField txtPrecioUnitario;
    @FXML private ComboBox cmbCodigoProveedor;
    @FXML private TableView tblMedicamentos;
    @FXML private TableColumn colCodigoMedicamento;
    @FXML private TableColumn colNombreMedicamento;
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colStock;
    @FXML private TableColumn colPrecioUnitario;
    @FXML private TableColumn colFechaVencimiento;
    @FXML private TableColumn colCodigoProveedor;
    @FXML private GridPane grpFechaVencimiento;
    
    

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
        anchorPaneFin.setMinWidth(309.5);        
        anchorPaneFin.setMaxWidth(Double.MAX_VALUE);        
        fecha.setStyle("-fx-alignment: CENTER;");
        grpFechaVencimiento.add(anchorPaneFin, 3, 2);
        
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
        tblMedicamentos.setItems(getMedicamento());
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<Medicamento,Integer>("codigoMedicamento"));
        colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<Medicamento, String>("nombreMedicamento"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Medicamento, String>("descripcion"));
        colStock.setCellValueFactory(new PropertyValueFactory<Medicamento,Integer>("stock"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<Medicamento, Float>("precioUnitario"));
        colFechaVencimiento.setCellValueFactory(new PropertyValueFactory<Medicamento, Date>("fechaVencimiento"));
        colCodigoProveedor.setCellValueFactory(new PropertyValueFactory<Medicamento,Integer>("codigoProveedor"));
        
    }
    
    public void seleccionarElemento(){
        if (tblMedicamentos.getSelectionModel().getSelectedItem() != null){
            txtCodigoMedicamento.setText(String.valueOf(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getCodigoMedicamento()));
            txtNombreMedicamento.setText(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getNombreMedicamento());
            txtDescripcion.setText(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getDescripcion());
            txtStock.setText(String.valueOf(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getStock()));
            txtPrecioUnitario.setText(String.valueOf(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getPrecioUnitario()));
            /*COMBOBOX*/
            cmbCodigoProveedor.getSelectionModel().select(buscarProveedor(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getCodigoProveedor()));
            fecha.selectedDateProperty().set(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getFechaVencimiento()); 
            
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblMedicamentos.setDisable(true);
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
                tblMedicamentos.setDisable(false);
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
        Medicamento registro = new Medicamento();

        if(txtNombreMedicamento.getText().trim().isEmpty() || txtDescripcion.getText().trim().isEmpty() || txtStock.getText().trim().isEmpty() ||
            txtPrecioUnitario.getText().trim().isEmpty() || cmbCodigoProveedor.getSelectionModel().isEmpty() ||fecha.getSelectedDate() == null){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
        } else if(txtNombreMedicamento.getText().trim().length()>100 || txtDescripcion.getText().trim().length()>250 ){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
       
        } else if (!txtStock.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Ingrese solamente números enteros (Stock).");
                return false;
        
        } else if (!txtPrecioUnitario.getText().trim().matches("\\d{0,8}(\\.\\d{0,2})?")){
            JOptionPane.showMessageDialog(null,"El precio unitario contiene un formato inválido (Ejemplo: 10.2) , (max. 8 enteros y 2 decimales).");
            return false;
           
        }else {
            registro.setNombreMedicamento(txtNombreMedicamento.getText().trim());
            registro.setDescripcion(txtDescripcion.getText().trim());
            registro.setStock(Integer.parseInt(txtStock.getText().trim()));
            registro.setPrecioUnitario(Float.parseFloat(txtPrecioUnitario.getText().trim()));
            registro.setFechaVencimiento(fecha.getSelectedDate());
            registro.setCodigoProveedor(((Proveedor)cmbCodigoProveedor.getSelectionModel().getSelectedItem()).getCodigoProveedor());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarMedicamento(?,?,?,?,?,?)}");
                procedimiento.setString(1, registro.getNombreMedicamento());
                procedimiento.setString(2, registro.getDescripcion());
                procedimiento.setInt(3, registro.getStock());
                procedimiento.setFloat(4, registro.getPrecioUnitario());
                procedimiento.setDate(5, new java.sql.Date(registro.getFechaVencimiento().getTime()));                
                procedimiento.setInt(6, registro.getCodigoProveedor());
                procedimiento.execute();
                listaMedicamento.add(registro);
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
                tblMedicamentos.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblMedicamentos.getSelectionModel().clearSelection();
                
                break;
            default:
                if(tblMedicamentos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Medicamento",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarMedicamento(?)}");
                            procedimiento.setInt(1, ((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getCodigoMedicamento());
                            procedimiento.execute();
                            listaMedicamento.remove(tblMedicamentos.getSelectionModel().getSelectedIndex());
                            tblMedicamentos.getSelectionModel().clearSelection();
                            limpiarControles();
                            
                        } catch (Exception e) {
                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar este medicamento porque está vinculado a otros registros en el sistema.");
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
                if(tblMedicamentos.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblMedicamentos.setDisable(true);
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
                tblMedicamentos.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblMedicamentos.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
                
        
        }
    
    }
    
    public boolean actualizar(){
        try {
            if(txtNombreMedicamento.getText().trim().isEmpty() || txtDescripcion.getText().trim().isEmpty() || txtStock.getText().trim().isEmpty() ||
            txtPrecioUnitario.getText().isEmpty() || fecha.getSelectedDate() == null){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
            } else if(txtNombreMedicamento.getText().trim().length()>100 || txtDescripcion.getText().trim().length()>250 ){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
       
            } else if (!txtStock.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Ingrese solamente números enteros (Stock).");
                return false;

            } else if (!txtPrecioUnitario.getText().trim().matches("\\d{0,10}(\\.\\d{0,2})?")){
            JOptionPane.showMessageDialog(null,"El precio unitario ingresado contiene un formato inválido (Ejemplo: 10.2) , (max. 8 enteros y 2 decimales).");
            return false;
           
            }else {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarMedicamento(?,?,?,?,?,?)}");
                Medicamento registro = (Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem();
                registro.setNombreMedicamento(txtNombreMedicamento.getText().trim());
                registro.setDescripcion(txtDescripcion.getText().trim());
                registro.setStock(Integer.parseInt(txtStock.getText().trim()));
                registro.setPrecioUnitario(Float.parseFloat(txtPrecioUnitario.getText().trim()));
                registro.setFechaVencimiento(fecha.getSelectedDate());
                
                procedimiento.setInt(1, registro.getCodigoMedicamento());
                procedimiento.setString(2, registro.getNombreMedicamento());
                procedimiento.setString(3, registro.getDescripcion());
                procedimiento.setInt(4, registro.getStock());
                procedimiento.setFloat(5, registro.getPrecioUnitario());
                procedimiento.setDate(6, new java.sql.Date(registro.getFechaVencimiento().getTime()));
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
                tblMedicamentos.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblMedicamentos.getSelectionModel().clearSelection();
                break;
            default:
        
        }
    
    }
    
    public ObservableList<Medicamento> getMedicamento(){
        ArrayList<Medicamento> lista = new ArrayList<Medicamento>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarMedicamentos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medicamento(resultado.getInt("codigoMedicamento"),
                                resultado.getString("nombreMedicamento"),
                                resultado.getString("descripcion"),
                                resultado.getInt("stock"),
                                resultado.getFloat("precioUnitario"),
                                resultado.getDate("fechaVencimiento"),
                                resultado.getInt("codigoProveedor")));
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        return listaMedicamento = FXCollections.observableArrayList(lista);
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
        txtCodigoMedicamento.setEditable(false);
        txtNombreMedicamento.setEditable(false);
        txtDescripcion.setEditable(false);
        txtStock.setEditable(false);
        txtPrecioUnitario.setEditable(false);
        cmbCodigoProveedor.setDisable(true);
        fecha.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoMedicamento.setEditable(false);
        txtNombreMedicamento.setEditable(true);
        txtDescripcion.setEditable(true);
        txtStock.setEditable(true);
        txtPrecioUnitario.setEditable(true);
        cmbCodigoProveedor.setDisable(false);
        fecha.setDisable(false);       
    }
    
    public void limpiarControles(){
        txtCodigoMedicamento.clear();
        txtNombreMedicamento.clear();
        txtDescripcion.clear();
        txtStock.clear();
        txtPrecioUnitario.clear();
        cmbCodigoProveedor.setValue(null);
        fecha.setSelectedDate(null);
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
