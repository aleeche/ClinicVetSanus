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
import org.alejandroecheverria.bean.Cliente;
import org.alejandroecheverria.db.Conexion;
import org.alejandroecheverria.report.GenerarReporte;


public class ClienteController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Cliente> listaCliente;
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
    @FXML private TextField txtCodigoCliente;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtApellidoCliente;
    @FXML private TextField txtTelefonoCliente;
    @FXML private TextField txtDireccionCliente;
    @FXML private TextField txtCorreoCliente;
    @FXML private TableView tblClientes;
    @FXML private TableColumn colCodigoCliente;
    @FXML private TableColumn colNombreCliente;
    @FXML private TableColumn colApellidoCliente;
    @FXML private TableColumn colTelefonoCliente;
    @FXML private TableColumn colDireccionCliente;
    @FXML private TableColumn colCorreoCliente;
    @FXML private TableColumn colFechaRegistro;
    @FXML private GridPane grpFechaRegistro;
    
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
        anchorPaneFin.setMinWidth(293.5);        
        anchorPaneFin.setMaxWidth(293.5);        
        fecha.setStyle("-fx-alignment: CENTER;");
        grpFechaRegistro.add(anchorPaneFin, 1, 3);
        
        Platform.runLater(() -> {
        for (Node node : fecha.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblClientes.setItems(getCliente());
        colCodigoCliente.setCellValueFactory(new PropertyValueFactory<Cliente,Integer>("codigoCliente"));
        colNombreCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("nombreCliente"));
        colApellidoCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("apellidoCliente"));
        colTelefonoCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("telefonoCliente"));
        colDireccionCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("direccionCliente"));
        colCorreoCliente.setCellValueFactory(new PropertyValueFactory<Cliente, String>("correoCliente"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<Cliente, Date>("fechaRegistro"));
        
    }
    
    public void seleccionarElemento(){
        if (tblClientes.getSelectionModel().getSelectedItem() != null){
            txtCodigoCliente.setText(String.valueOf(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCodigoCliente()));
            txtNombreCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getNombreCliente());
            txtApellidoCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getApellidoCliente());
            txtTelefonoCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getTelefonoCliente());
            txtDireccionCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getDireccionCliente());
            txtCorreoCliente.setText(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCorreoCliente());
            fecha.selectedDateProperty().set(((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getFechaRegistro()); 
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
                tblClientes.setDisable(true);
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
                tblClientes.setDisable(false);
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
        Cliente registro = new Cliente();
        String correo = txtCorreoCliente.getText().trim().toLowerCase();
        String[] dominiosPermitidos = {"@gmail.com", "@yahoo.com", "@outlook.com","hotmail.com","@live.com","@icloud.com","@protonmail.com","@aol.com","@claro.com.gt","@tigo.com.gt","@edu.gt","@kinal.edu.gt","@kinal.org.gt","@mail.com","@mailfence.com","@live.com","@msn.com"};
        boolean tieneArroba = correo.contains("@");
        boolean textoAntesDeArroba = tieneArroba && correo.indexOf('@') > 0;
        boolean esDominioValido = Arrays.stream(dominiosPermitidos).anyMatch(correo::endsWith);
        
//        registro.setCodigoCliente(Integer.parseInt(txtCodigoCliente.getText()));
        registro.setNombreCliente(txtNombreCliente.getText().trim());
        registro.setApellidoCliente(txtApellidoCliente.getText().trim());
        registro.setTelefonoCliente(txtTelefonoCliente.getText().trim());
        registro.setDireccionCliente(txtDireccionCliente.getText().trim());
        registro.setCorreoCliente(txtCorreoCliente.getText().trim().toLowerCase());
        registro.setFechaRegistro(fecha.getSelectedDate());
        try {
            if(txtNombreCliente.getText().trim().isEmpty() || txtApellidoCliente.getText().trim().isEmpty() || txtTelefonoCliente.getText().trim().isEmpty() || txtDireccionCliente.getText().trim().isEmpty() ||
                    txtCorreoCliente.getText().trim().isEmpty() || fecha.getSelectedDate() == null){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
                
            } else if (!txtTelefonoCliente.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;

            } else if (!esDominioValido || !tieneArroba || !textoAntesDeArroba){
                JOptionPane.showMessageDialog(null, "Ingresar un correo valido");
                return false;
                
            }else if(txtNombreCliente.getText().trim().length()>75 || txtApellidoCliente.getText().trim().length()>75 || txtDireccionCliente.getText().trim().length()>200 || txtCorreoCliente.getText().trim().length()>80){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else if(txtTelefonoCliente.getText().trim().length() !=8 ){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;
                
            }else{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarCliente(?,?,?,?,?,?)}");
                procedimiento.setString(1, registro.getNombreCliente());
                procedimiento.setString(2, registro.getApellidoCliente());
                procedimiento.setString(3, registro.getTelefonoCliente());
                procedimiento.setString(4, registro.getDireccionCliente());
                procedimiento.setString(5, registro.getCorreoCliente());
                procedimiento.setDate(6, new java.sql.Date(registro.getFechaRegistro().getTime()));
                procedimiento.execute();
                listaCliente.add(registro);
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
                /*CANCELAR*/
                desactivarControles();
                limpiarControles();
                /*btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");*/
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblClientes.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblClientes.getSelectionModel().clearSelection();
                break;
            default:
                if(tblClientes.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Cliente",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarCliente(?)}");
                            procedimiento.setInt(1, ((Cliente)tblClientes.getSelectionModel().getSelectedItem()).getCodigoCliente());
                            procedimiento.execute();
                            listaCliente.remove(tblClientes.getSelectionModel().getSelectedIndex());
                            tblClientes.getSelectionModel().clearSelection();
                            limpiarControles();
                            
                        } catch (Exception e) {
                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar este cliente porque está vinculado a otros registros en el sistema.");
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
                if(tblClientes.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblClientes.setDisable(true);
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
                tblClientes.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblClientes.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
               break;
        
        
        
        }
                 
    }
    
    public boolean actualizar(){
        String correo = txtCorreoCliente.getText().trim().toLowerCase();
        String[] dominiosPermitidos = {"@gmail.com", "@yahoo.com", "@outlook.com","hotmail.com","@live.com","@icloud.com","@protonmail.com","@aol.com","@claro.com.gt","@tigo.com.gt","@edu.gt","@kinal.edu.gt","@kinal.org.gt","@mail.com","@mailfence.com","@live.com","@msn.com"};
        boolean tieneArroba = correo.contains("@");
        boolean textoAntesDeArroba = tieneArroba && correo.indexOf('@') > 0;
        boolean esDominioValido = Arrays.stream(dominiosPermitidos).anyMatch(correo::endsWith);
        try {
            if(txtNombreCliente.getText().trim().isEmpty() || txtApellidoCliente.getText().trim().isEmpty() || txtTelefonoCliente.getText().trim().isEmpty() || txtDireccionCliente.getText().trim().isEmpty() ||
                    txtCorreoCliente.getText().trim().isEmpty() || fecha.getSelectedDate() == null){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            } else if (!txtTelefonoCliente.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;

            } else if (!esDominioValido || !tieneArroba || !textoAntesDeArroba){
                JOptionPane.showMessageDialog(null, "Ingresar un correo valido");
                return false;
                
            }else if(txtNombreCliente.getText().trim().length()>75 || txtApellidoCliente.getText().trim().length()>75 || txtDireccionCliente.getText().trim().length()>200 || txtCorreoCliente.getText().trim().length()>80){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else if(txtTelefonoCliente.getText().trim().length() !=8 ){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;
            
            }else{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarCliente(?,?,?,?,?,?,?)}");
            Cliente registro = (Cliente)tblClientes.getSelectionModel().getSelectedItem();
            registro.setNombreCliente(txtNombreCliente.getText().trim());
            registro.setApellidoCliente(txtApellidoCliente.getText().trim());
            registro.setTelefonoCliente(txtTelefonoCliente.getText().trim());
            registro.setDireccionCliente(txtDireccionCliente.getText().trim());
            registro.setCorreoCliente(txtCorreoCliente.getText().trim().toLowerCase());
            registro.setFechaRegistro(fecha.getSelectedDate());
            procedimiento.setInt(1, registro.getCodigoCliente());
            procedimiento.setString(2, registro.getNombreCliente());
            procedimiento.setString(3, registro.getApellidoCliente());
            procedimiento.setString(4, registro.getTelefonoCliente());
            procedimiento.setString(5, registro.getDireccionCliente());
            procedimiento.setString(6, registro.getCorreoCliente());
            procedimiento.setDate(7, new java.sql.Date(registro.getFechaRegistro().getTime()));
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
                /*CANCELAR*/
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblClientes.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblClientes.getSelectionModel().clearSelection();
                break;
            default:
                
            
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
//        parametros.put("codigoCliente", null);
        
        try {
            InputStream is = getClass().getResourceAsStream("/org/alejandroecheverria/image/HojaMe.jpg");
            if (is == null) {
                throw new RuntimeException("No se encontró la imagen HojaMe.jpg");
            }
        
            BufferedImage imagen = ImageIO.read(is);
            parametros.put("imagenMembrete", imagen);

            GenerarReporte.mostrarReporte("ReporteClientes.jasper", "Reporte de Clientes", parametros);

        } catch (Exception e) {
            throw new RuntimeException("Error al leer la imagen", e);
        }
    
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
    
    
    public void desactivarControles(){
        txtCodigoCliente.setEditable(false);
        txtNombreCliente.setEditable(false);
        txtApellidoCliente.setEditable(false);
        txtTelefonoCliente.setEditable(false);
        txtDireccionCliente.setEditable(false);
        txtCorreoCliente.setEditable(false);
        fecha.setDisable(true);
        
    }
    
    public void activarControles(){
        txtCodigoCliente.setEditable(false);
        txtNombreCliente.setEditable(true);
        txtApellidoCliente.setEditable(true);
        txtTelefonoCliente.setEditable(true);
        txtDireccionCliente.setEditable(true);
        txtCorreoCliente.setEditable(true);
        fecha.setDisable(false);
                
    }
    
    public void limpiarControles(){
        txtCodigoCliente.clear();
        txtNombreCliente.clear();
        txtApellidoCliente.clear();
        txtTelefonoCliente.clear();
        txtDireccionCliente.clear();
        txtCorreoCliente.clear();
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
