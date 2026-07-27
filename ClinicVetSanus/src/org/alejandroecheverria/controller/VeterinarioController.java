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
import org.alejandroecheverria.bean.Veterinario;
import org.alejandroecheverria.db.Conexion;

public class VeterinarioController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Veterinario> listaVeterinario;
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
    @FXML private TextField txtCodigoVeterinario;
    @FXML private TextField txtNombreVeterinario;
    @FXML private TextField txtApellidoVeterinario;
    @FXML private TextField txtEspecialidad;
    @FXML private TextField txtTelefonoVeterinario;
    @FXML private TextField txtCorreoVeterinario;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TableView tblVeterinarios;
    @FXML private TableColumn colCodigoVeterinario;
    @FXML private TableColumn colNombreVeterinario;
    @FXML private TableColumn colApellidoVeterinario;
    @FXML private TableColumn colEspecialidad;
    @FXML private TableColumn colTelefonoVeterinario;
    @FXML private TableColumn colCorreoVeterinario;
    @FXML private TableColumn colFechaIngreso;
    @FXML private TableColumn colEstado;
    @FXML private GridPane grpFechaIngreso;
        
    
    
    
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
        anchorPaneFin.setMinWidth(281.5);        
        anchorPaneFin.setMaxWidth(281.5);        
        fecha.setStyle("-fx-alignment: CENTER;");
        grpFechaIngreso.add(anchorPaneFin, 1, 3);
        
        Platform.runLater(() -> {
        for (Node node : fecha.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        
        cmbEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        
        cargarDatos();
        
    }

    public void cargarDatos(){
        tblVeterinarios.setItems(getVeterinario());
        colCodigoVeterinario.setCellValueFactory(new PropertyValueFactory<Veterinario, Integer>("codigoVeterinario"));
        colNombreVeterinario.setCellValueFactory(new PropertyValueFactory<Veterinario, String>("nombreVeterinario"));
        colApellidoVeterinario.setCellValueFactory(new PropertyValueFactory<Veterinario, String>("apellidoVeterinario"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<Veterinario, String>("especialidad"));
        colTelefonoVeterinario.setCellValueFactory(new PropertyValueFactory<Veterinario, String>("telefonoVeterinario"));
        colCorreoVeterinario.setCellValueFactory(new PropertyValueFactory<Veterinario, String>("correoVeterinario"));
        colFechaIngreso.setCellValueFactory(new PropertyValueFactory<Veterinario, Date>("fechaIngreso"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Veterinario, String>("estado"));
    
    }
    
    public void seleccionarElemento(){
        if(tblVeterinarios.getSelectionModel().getSelectedItem() != null){
            txtCodigoVeterinario.setText(String.valueOf(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getCodigoVeterinario()));
            txtNombreVeterinario.setText(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getNombreVeterinario());
            txtApellidoVeterinario.setText(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getApellidoVeterinario());
            txtEspecialidad.setText(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getEspecialidad());
            txtTelefonoVeterinario.setText(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getTelefonoVeterinario());
            txtCorreoVeterinario.setText(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getCorreoVeterinario());
            fecha.selectedDateProperty().set(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getFechaIngreso()); 
            cmbEstado.getSelectionModel().select(((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getEstado());
            
        
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblVeterinarios.setDisable(true);
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
                tblVeterinarios.setDisable(false);
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
        Veterinario registro = new Veterinario();
        String correo = txtCorreoVeterinario.getText().trim().toLowerCase();
        String[] dominiosPermitidos = {"@gmail.com", "@yahoo.com", "@outlook.com","hotmail.com","@live.com","@icloud.com","@protonmail.com","@aol.com","@claro.com.gt","@tigo.com.gt","@edu.gt","@kinal.edu.gt","@kinal.org.gt","@mail.com","@mailfence.com","@live.com","@msn.com"};
        boolean tieneArroba = correo.contains("@");
        boolean textoAntesDeArroba = tieneArroba && correo.indexOf('@') > 0;
        boolean esDominioValido = Arrays.stream(dominiosPermitidos).anyMatch(correo::endsWith);
        registro.setNombreVeterinario(txtNombreVeterinario.getText().trim());
        registro.setApellidoVeterinario(txtApellidoVeterinario.getText().trim());
        registro.setEspecialidad(txtEspecialidad.getText().trim());
        registro.setTelefonoVeterinario(txtTelefonoVeterinario.getText().trim());
        registro.setCorreoVeterinario(txtCorreoVeterinario.getText().trim().toLowerCase());
        registro.setFechaIngreso(fecha.getSelectedDate());
        registro.setEstado(cmbEstado.getSelectionModel().getSelectedItem());
        try {
            if(txtNombreVeterinario.getText().trim().isEmpty() || txtApellidoVeterinario.getText().trim().isEmpty() || txtEspecialidad.getText().trim().isEmpty() 
                    || txtTelefonoVeterinario.getText().trim().isEmpty() || txtCorreoVeterinario.getText().trim().isEmpty() || cmbEstado.getSelectionModel().isEmpty() || fecha.getSelectedDate() == null ){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            
            } else if (!txtTelefonoVeterinario.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678).");
                return false;
            
            } else if (!esDominioValido || !tieneArroba || !textoAntesDeArroba){
                JOptionPane.showMessageDialog(null, "Ingresar un correo valido");
                return false;
            
            }else if (txtNombreVeterinario.getText().trim().length()>75 || txtApellidoVeterinario.getText().trim().length()>75 || txtEspecialidad.getText().trim().length()>100
                        || txtCorreoVeterinario.getText().trim().length()>80){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else if(txtTelefonoVeterinario.getText().trim().length() !=8 ){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678)."); 
                return false;
    
            
            
            }else{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarVeterinario(?,?,?,?,?,?,?)}");
                procedimiento.setString(1, registro.getNombreVeterinario());
                procedimiento.setString(2, registro.getApellidoVeterinario());
                procedimiento.setString(3, registro.getEspecialidad());
                procedimiento.setString(4, registro.getTelefonoVeterinario());
                procedimiento.setString(5, registro.getCorreoVeterinario());
                procedimiento.setDate(6, new java.sql.Date(registro.getFechaIngreso().getTime()));
                procedimiento.setString(7, registro.getEstado());
                procedimiento.execute();
                listaVeterinario.add(registro);
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
                tblVeterinarios.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblVeterinarios.getSelectionModel().clearSelection();
                break;
            default:
                if(tblVeterinarios.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Veterinario",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarVeterinario(?)}");
                            procedimiento.setInt(1, ((Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem()).getCodigoVeterinario());
                            procedimiento.execute();
                            listaVeterinario.remove(tblVeterinarios.getSelectionModel().getSelectedIndex());
                            tblVeterinarios.getSelectionModel().clearSelection();
                            limpiarControles();
                        } catch (Exception e) {
                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar este veterinario porque está vinculado a otros registros en el sistema.");
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
                if(tblVeterinarios.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblVeterinarios.setDisable(true);
                    btnHome.setDisable(true);
                    imgEditar.setImage(new Image("/org/alejandroecheverria/image/IconActua.png"));
                    imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                
                } else {
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento.");
                
                }
                break;
            case ACTUALIZAR:
                if(actualizar() == true){
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblVeterinarios.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblVeterinarios.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
        
        }
    
    }
    
    public boolean actualizar(){
        String correo = txtCorreoVeterinario.getText().trim().toLowerCase();
        String[] dominiosPermitidos = {"@gmail.com", "@yahoo.com", "@outlook.com","hotmail.com","@live.com","@icloud.com","@protonmail.com","@aol.com","@claro.com.gt","@tigo.com.gt","@edu.gt","@kinal.edu.gt","@kinal.org.gt","@mail.com","@mailfence.com","@live.com","@msn.com"};
        boolean tieneArroba = correo.contains("@");
        boolean textoAntesDeArroba = tieneArroba && correo.indexOf('@') > 0;
        boolean esDominioValido = Arrays.stream(dominiosPermitidos).anyMatch(correo::endsWith);
        try {
            if(txtNombreVeterinario.getText().trim().isEmpty() || txtApellidoVeterinario.getText().trim().isEmpty() || txtEspecialidad.getText().trim().isEmpty() 
                    || txtTelefonoVeterinario.getText().trim().isEmpty() || txtCorreoVeterinario.getText().trim().isEmpty() || cmbEstado.getSelectionModel().isEmpty() || fecha.getSelectedDate() == null ){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            
            } else if (!txtTelefonoVeterinario.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"El número de teléfono ingresado contiene letras.");
                return false;
            
            } else if (!esDominioValido || !tieneArroba || !textoAntesDeArroba){
                JOptionPane.showMessageDialog(null, "Número de teléfono invalido (Ejemplo: 12345678).");
                return false;
            
            }else if (txtNombreVeterinario.getText().trim().length()>75 || txtApellidoVeterinario.getText().trim().length()>75 || txtEspecialidad.getText().trim().length()>100
                        || txtCorreoVeterinario.getText().trim().length()>80){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else if(txtTelefonoVeterinario.getText().trim().length() !=8 ){
                JOptionPane.showMessageDialog(null,"Número de teléfono invalido (Ejemplo: 12345678)."); 
                return false;
            
            
            
            }else{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarVeterinario(?,?,?,?,?,?,?,?)}");
                Veterinario registro = (Veterinario)tblVeterinarios.getSelectionModel().getSelectedItem();
                registro.setNombreVeterinario(txtNombreVeterinario.getText().trim());
                registro.setApellidoVeterinario(txtApellidoVeterinario.getText().trim());
                registro.setEspecialidad(txtEspecialidad.getText().trim());
                registro.setTelefonoVeterinario(txtTelefonoVeterinario.getText().trim());
                registro.setCorreoVeterinario(txtCorreoVeterinario.getText().trim().toLowerCase());
                registro.setFechaIngreso(fecha.getSelectedDate());
                registro.setEstado(cmbEstado.getSelectionModel().getSelectedItem());
                procedimiento.setInt(1, registro.getCodigoVeterinario());
                procedimiento.setString(2, registro.getNombreVeterinario());
                procedimiento.setString(3, registro.getApellidoVeterinario());
                procedimiento.setString(4, registro.getEspecialidad());
                procedimiento.setString(5, registro.getTelefonoVeterinario());
                procedimiento.setString(6, registro.getCorreoVeterinario());
                procedimiento.setDate(7, new java.sql.Date(registro.getFechaIngreso().getTime()));
                procedimiento.setString(8, registro.getEstado());
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
                tblVeterinarios.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblVeterinarios.getSelectionModel().clearSelection();
                
                break;
            default:
        
        }
    }
    
    public ObservableList<Veterinario> getVeterinario(){
        ArrayList<Veterinario> lista = new ArrayList<Veterinario>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarVeterinarios}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Veterinario(resultado.getInt("codigoVeterinario"),
                                resultado.getString("nombreVeterinario"),
                                resultado.getString("apellidoVeterinario"),
                                resultado.getString("especialidad"),
                                resultado.getString("telefonoVeterinario"),
                                resultado.getString("correoVeterinario"),
                                resultado.getDate("fechaIngreso"),
                                resultado.getString("estado")));
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaVeterinario = FXCollections.observableArrayList(lista);
    }
    
    public void desactivarControles(){
        txtCodigoVeterinario.setEditable(false);
        txtNombreVeterinario.setEditable(false);
        txtApellidoVeterinario.setEditable(false);
        txtEspecialidad.setEditable(false);
        txtTelefonoVeterinario.setEditable(false);
        txtCorreoVeterinario.setEditable(false);
        cmbEstado.setDisable(true);
        fecha.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoVeterinario.setEditable(false);
        txtNombreVeterinario.setEditable(true);
        txtApellidoVeterinario.setEditable(true);
        txtEspecialidad.setEditable(true);
        txtTelefonoVeterinario.setEditable(true);
        txtCorreoVeterinario.setEditable(true);
        cmbEstado.setDisable(false);
        fecha.setDisable(false);
    
    }
    
    public void limpiarControles(){
        txtCodigoVeterinario.clear();
        txtNombreVeterinario.clear();
        txtApellidoVeterinario.clear();
        txtEspecialidad.clear();
        txtTelefonoVeterinario.clear();
        txtCorreoVeterinario.clear();
        cmbEstado.setValue(null);
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
