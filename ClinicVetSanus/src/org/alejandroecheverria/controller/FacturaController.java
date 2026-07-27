package org.alejandroecheverria.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.alejandroecheverria.system.Principal;
import org.alejandroecheverria.bean.Factura;
import org.alejandroecheverria.bean.Cliente;
import org.alejandroecheverria.bean.Empleado;
import org.alejandroecheverria.db.Conexion;
import org.alejandroecheverria.report.GenerarReporte;


public class FacturaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Factura> listaFactura;
    private ObservableList<Cliente> listaCliente;
    private ObservableList<Empleado> listaEmpleado;
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
    @FXML private TextField txtCodigoFactura;
    @FXML private TextField txtTotal;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private ComboBox cmbCodigoCliente;
    @FXML private ComboBox cmbCodigoEmpleado;
    @FXML private TableView tblFacturas;
    @FXML private TableColumn colCodigoFactura;
    @FXML private TableColumn colFechaEmision;
    @FXML private TableColumn colTotal;
    @FXML private TableColumn colMetodoPago;
    @FXML private TableColumn colCodigoCliente;
    @FXML private TableColumn colCodigoEmpleado;
    @FXML private GridPane grpFechaEmision;
    

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
        grpFechaEmision.add(anchorPaneFin, 3, 0);
        
        Platform.runLater(() -> {
        for (Node node : fecha.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        
        
        cmbMetodoPago.setItems(FXCollections.observableArrayList("Efectivo", "Tarjeta","Transferencia"));
        
        cmbCodigoCliente.setItems(getCliente());
        
        cmbCodigoEmpleado.setItems(getEmpleado());
        cargarDatos();
        
    }
    
    public void cargarDatos(){
        tblFacturas.setItems(getFactura());
        colCodigoFactura.setCellValueFactory(new PropertyValueFactory<Factura,Integer>("codigoFactura"));
        colFechaEmision.setCellValueFactory(new PropertyValueFactory<Factura,Date>("fechaEmision"));
        colTotal.setCellValueFactory(new PropertyValueFactory<Factura,Float>("total"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<Factura,String>("metodoPago"));
        colCodigoCliente.setCellValueFactory(new PropertyValueFactory<Factura,Integer>("codigoCliente"));
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<Factura,Integer>("codigoEmpleado"));
    }
    
    public void seleccionarElemento(){
        if (tblFacturas.getSelectionModel().getSelectedItem() != null){
            txtCodigoFactura.setText(String.valueOf(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getCodigoFactura()));
            txtTotal.setText(String.valueOf(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getTotal()));
            cmbMetodoPago.getSelectionModel().select(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getMetodoPago());
            
            cmbCodigoCliente.getSelectionModel().select(buscarCliente(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getCodigoCliente()));
            cmbCodigoEmpleado.getSelectionModel().select(buscarEmpleado(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
            fecha.selectedDateProperty().set(((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getFechaEmision()); 
            
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                txtTotal.setEditable(false);
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblFacturas.setDisable(true);
                btnHome.setDisable(true);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconGuardar.png"));
                imgEliminar.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(guardar() == true){
                limpiarControles();
                desactivarControles();
                txtTotal.setEditable(false);
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblFacturas.setDisable(false);
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
        Factura registro = new Factura();
        if(fecha.getSelectedDate() == null ||cmbMetodoPago.getSelectionModel().isEmpty() || cmbCodigoCliente.getSelectionModel().isEmpty() ||cmbCodigoEmpleado.getSelectionModel().isEmpty() ){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
//        } else if (!txtTotal.getText().trim().matches("\\d{0,8}(\\.\\d{0,2})?")){
//            JOptionPane.showMessageDialog(null,"El total contiene un formato inválido (Ejemplo: 10.2) , (max. 8 enteros y 2 decimales).");
//            return false;
        
        } else {
            registro.setFechaEmision(fecha.getSelectedDate());
//            registro.setTotal(Float.parseFloat(txtTotal.getText().trim()));
            registro.setMetodoPago(cmbMetodoPago.getSelectionModel().getSelectedItem());
            registro.setCodigoCliente(((Cliente)cmbCodigoCliente.getSelectionModel().getSelectedItem()).getCodigoCliente());
            registro.setCodigoEmpleado(((Empleado)cmbCodigoEmpleado.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarFactura(?,?,?,?)}");
                procedimiento.setDate(1, new java.sql.Date(registro.getFechaEmision().getTime()));
                procedimiento.setString(2, registro.getMetodoPago());
                procedimiento.setInt(3, registro.getCodigoCliente());
                procedimiento.setInt(4, registro.getCodigoEmpleado());
                procedimiento.execute();
                listaFactura.add(registro);
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
                tblFacturas.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblFacturas.getSelectionModel().clearSelection();
                break;
            default:
                if(tblFacturas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Factura",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarFactura(?)}");
                            procedimiento.setInt(1, ((Factura)tblFacturas.getSelectionModel().getSelectedItem()).getCodigoFactura());
                            procedimiento.execute();
                            listaFactura.remove(tblFacturas.getSelectionModel().getSelectedIndex());
                            tblFacturas.getSelectionModel().clearSelection();
                            limpiarControles();
                        
                        } catch (Exception e) {
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar esta factura porque está vinculado a otros registros en el sistema.");
                        e.printStackTrace();
                        
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
                if(tblFacturas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblFacturas.setDisable(true);
                    btnHome.setDisable(true);
                    imgEditar.setImage(new Image("/org/alejandroecheverria/image/IconActua.png"));
                    imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                    activarControles();
                    txtTotal.setEditable(true);
                    cmbCodigoCliente.setDisable(true);
                    cmbCodigoEmpleado.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento.");
                }
                
                break;
            case ACTUALIZAR:
                if(actualizar() == true){
                desactivarControles();
                limpiarControles();
                txtTotal.setEditable(false);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblFacturas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblFacturas.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
        
        }
    
    }
    
    public boolean actualizar(){
        try {
            if(txtTotal.getText().trim().isEmpty() || fecha.getSelectedDate() == null ||cmbMetodoPago.getSelectionModel().isEmpty()){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
        
            } else if (!txtTotal.getText().trim().matches("\\d{0,8}(\\.\\d{0,2})?")){
                JOptionPane.showMessageDialog(null,"El total contiene un formato inválido (Ejemplo: 10.2) , (max. 8 enteros y 2 decimales).");
                return false;
        
            } else {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarFactura(?,?,?,?)}");
                Factura registro = (Factura)tblFacturas.getSelectionModel().getSelectedItem();
                registro.setFechaEmision(fecha.getSelectedDate());
                registro.setTotal(Float.parseFloat(txtTotal.getText().trim()));
                registro.setMetodoPago(cmbMetodoPago.getSelectionModel().getSelectedItem());
                
                procedimiento.setInt(1, registro.getCodigoFactura());
                procedimiento.setDate(2, new java.sql.Date(registro.getFechaEmision().getTime()));
                procedimiento.setFloat(3, registro.getTotal());
                procedimiento.setString(4, registro.getMetodoPago());
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
                imprimirReporte();
                
                break;
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                txtTotal.setEditable(false);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblFacturas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblFacturas.getSelectionModel().clearSelection();
                break;
            default:
        
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        
        try {
            InputStream is = getClass().getResourceAsStream("/org/alejandroecheverria/image/HojaMe.jpg");
            if (is == null) {
                throw new RuntimeException("No se encontró la imagen HojaMe.jpg");
            }
        
            BufferedImage imagen = ImageIO.read(is);
            parametros.put("imagenMembrete", imagen);

            GenerarReporte.mostrarReporte("ReporteFacturas.jasper", "Reporte de Facturas", parametros);

        } catch (Exception e) {
            throw new RuntimeException("Error al leer la imagen", e);
        }
    }
    
    public ObservableList<Factura> getFactura(){
        ArrayList<Factura> lista = new ArrayList<Factura>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarFacturas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Factura(resultado.getInt("codigoFactura"),
                                resultado.getDate("fechaEmision"),
                                resultado.getFloat("total"),
                                resultado.getString("metodoPago"),
                                resultado.getInt("codigoCliente"),
                                resultado.getInt("codigoEmpleado")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaFactura = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Cliente> getCliente(){
        ArrayList<Cliente> lista = new ArrayList<Cliente>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarClientes}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cliente(resultado.getInt("codigoCliente"),
                                resultado.getString("nombreCliente"),
                                resultado.getString("apellidoCliente"),
                                resultado.getString("telefonoCliente"),
                                resultado.getString("direccionCliente"),
                                resultado.getString("correoCliente"),
                                resultado.getDate("fechaRegistro")));
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaCliente = FXCollections.observableArrayList(lista);
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
    
    public Cliente buscarCliente(int codigoCliente){
        Cliente resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarCliente(?)}");
            procedimiento.setInt(1, codigoCliente);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()){
                resultado = new Cliente(registro.getInt("codigoCliente"),
                                    registro.getString("nombreCliente"),
                                    registro.getString("apellidoCliente"),
                                    registro.getString("telefonoCliente"),
                                    registro.getString("direccionCliente"),
                                    registro.getString("correoCliente"),
                                    registro.getDate("fechaRegistro"));
            
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return resultado;
    }
    
    public Empleado buscarEmpleado (int codigoEmpleado){
        Empleado resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEmpleado(?)}");
            procedimiento.setInt(1, codigoEmpleado);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()){
                resultado = new Empleado(registro.getInt("codigoEmpleado"),
                                    registro.getString("nombreEmpleado"),
                                    registro.getString("apellidoEmpleado"),
                                    registro.getString("cargo"),
                                    registro.getString("telefonoEmpleado"),
                                    registro.getString("correoEmpleado"));
            
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return resultado;
    }
    
    public void desactivarControles(){
        txtCodigoFactura.setEditable(false);
        fecha.setDisable(true);
        cmbMetodoPago.setDisable(true);
        cmbCodigoCliente.setDisable(true);
        cmbCodigoEmpleado.setDisable(true);   
    }
    
    public void activarControles(){
        txtCodigoFactura.setEditable(false);
        fecha.setDisable(false);   
        cmbMetodoPago.setDisable(false);
        cmbCodigoCliente.setDisable(false);
        cmbCodigoEmpleado.setDisable(false);
            
    }
    
    public void limpiarControles(){
        txtCodigoFactura.clear();
        fecha.setSelectedDate(null);
        txtTotal.clear();
        cmbMetodoPago.setValue(null);
        cmbCodigoCliente.setValue(null);
        cmbCodigoEmpleado.setValue(null);
        
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
