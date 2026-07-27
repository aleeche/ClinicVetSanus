package org.alejandroecheverria.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import org.alejandroecheverria.bean.Consulta;
import org.alejandroecheverria.bean.Mascota;
import org.alejandroecheverria.bean.Veterinario;
import org.alejandroecheverria.db.Conexion;


public class ConsultaController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Consulta> listaConsulta;
    private ObservableList<Mascota> listaMascota;
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
    @FXML private TextField txtCodigoConsulta;
    @FXML private TextField txtMotivo;
    @FXML private TextField txtDiagnostico;
    @FXML private TextField txtObservaciones;
    @FXML private TextField txtHoraConsulta;
    @FXML private ComboBox cmbCodigoMascota;
    @FXML private ComboBox cmbCodigoVeterinario;
    @FXML private TableView tblConsultas;
    @FXML private TableColumn colCodigoConsulta;
    @FXML private TableColumn colFechaConsulta;
    @FXML private TableColumn colMotivo;
    @FXML private TableColumn colDiagnostico;
    @FXML private TableColumn colObservaciones;
    @FXML private TableColumn colCodigoMascota;
    @FXML private TableColumn colCodigoVeterinario;
    @FXML private GridPane grpFechaConsulta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
//        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/alejandroecheverria/resource/plantilla.css");
        fecha.setDisable(true);
        
        
        AnchorPane anchorPaneFin = new AnchorPane();
        anchorPaneFin.getChildren().add(fecha);
        AnchorPane.setTopAnchor(fecha, 0.0);
        AnchorPane.setRightAnchor(fecha, 0.0);
        AnchorPane.setBottomAnchor(fecha, 0.0);
        AnchorPane.setLeftAnchor(fecha, 0.0);
        anchorPaneFin.setPrefWidth(0);      
        anchorPaneFin.setMinWidth(150.5);        
        anchorPaneFin.setMaxWidth(150.5);        
        fecha.setStyle("-fx-alignment: CENTER;");
        grpFechaConsulta.add(anchorPaneFin, 3, 0);
        
        
        Platform.runLater(() -> {
        for (Node node : fecha.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        
        
        cmbCodigoMascota.setItems(getMascota());
        
        cmbCodigoVeterinario.setItems(getVeterinario());
        cargarDatos();
        
    }
    
    public void cargarDatos(){
        tblConsultas.setItems(getConsulta());
        colCodigoConsulta.setCellValueFactory(new PropertyValueFactory<Consulta,Integer>("codigoConsulta"));
        
        colFechaConsulta.setCellValueFactory(new PropertyValueFactory<>("nuevaFechaConsulta"));
        
        colMotivo.setCellValueFactory(new PropertyValueFactory<Consulta,String>("motivo"));
        colDiagnostico.setCellValueFactory(new PropertyValueFactory<Consulta,String>("diagnostico"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<Consulta,String>("observaciones"));
        colCodigoMascota.setCellValueFactory(new PropertyValueFactory<Consulta,Integer>("codigoMascota"));
        colCodigoVeterinario.setCellValueFactory(new PropertyValueFactory<Consulta,Integer>("codigoVeterinario"));
    }
    
    public void seleccionarElemento(){
        if (tblConsultas.getSelectionModel().getSelectedItem() != null){
            txtCodigoConsulta.setText(String.valueOf(((Consulta)tblConsultas.getSelectionModel().getSelectedItem()).getCodigoConsulta()));
            txtMotivo.setText(((Consulta)tblConsultas.getSelectionModel().getSelectedItem()).getMotivo());
            txtDiagnostico.setText(((Consulta)tblConsultas.getSelectionModel().getSelectedItem()).getDiagnostico());
            txtObservaciones.setText(((Consulta)tblConsultas.getSelectionModel().getSelectedItem()).getObservaciones());
            
            /*COMBOBOX*/
            cmbCodigoMascota.getSelectionModel().select(buscarMascota(((Consulta)tblConsultas.getSelectionModel().getSelectedItem()).getCodigoMascota()));
            cmbCodigoVeterinario.getSelectionModel().select(buscarVeterinario(((Consulta)tblConsultas.getSelectionModel().getSelectedItem()).getCodigoVeterinario()));
            
            fecha.setSelectedDate(java.sql.Timestamp.valueOf(((Consulta)tblConsultas.getSelectionModel().getSelectedItem()).getFechaConsulta()));
            txtHoraConsulta.setText(((Consulta)tblConsultas.getSelectionModel().getSelectedItem()).getFechaConsulta().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            
        } 
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblConsultas.setDisable(true);
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
                tblConsultas.setDisable(false);
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
        Consulta registro = new Consulta();
        String horaTexto = txtHoraConsulta.getText().trim();
        if(fecha.getSelectedDate() == null || horaTexto.isEmpty() ||txtMotivo.getText().trim().isEmpty() || txtDiagnostico.getText().trim().isEmpty() ||
            txtObservaciones.getText().trim().isEmpty() ||  cmbCodigoMascota.getSelectionModel().isEmpty() ||  cmbCodigoVeterinario.getSelectionModel().isEmpty() ){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
        } else if(txtMotivo.getText().trim().length()>255 || txtDiagnostico.getText().trim().length()>255 || txtObservaciones.getText().trim().length()>255){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
            
        } else if(!horaTexto.matches("([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")){
            JOptionPane.showMessageDialog(null,"Formato de hora cita inválido. Usa HH:mm:ss");
            return false;
        
        } else {
            
            registro.setFechaConsulta(LocalDateTime.of(fecha.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.parse(horaTexto, DateTimeFormatter.ofPattern("HH:mm:ss"))));
            registro.setMotivo(txtMotivo.getText().trim());
            registro.setDiagnostico(txtDiagnostico.getText().trim());
            registro.setObservaciones(txtObservaciones.getText().trim());
            registro.setCodigoMascota(((Mascota)cmbCodigoMascota.getSelectionModel().getSelectedItem()).getCodigoMascota());
            registro.setCodigoVeterinario(((Veterinario)cmbCodigoVeterinario.getSelectionModel().getSelectedItem()).getCodigoVeterinario());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarConsulta(?,?,?,?,?,?)}");
                procedimiento.setTimestamp(1, java.sql.Timestamp.valueOf(registro.getFechaConsulta()));
                procedimiento.setString(2, registro.getMotivo());
                procedimiento.setString(3, registro.getDiagnostico());
                procedimiento.setString(4, registro.getObservaciones());
                procedimiento.setInt(5, registro.getCodigoMascota());
                procedimiento.setInt(6, registro.getCodigoVeterinario());
                procedimiento.execute();
                listaConsulta.add(registro);
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
                tblConsultas.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblConsultas.getSelectionModel().clearSelection();
                break;
            default:
                if(tblConsultas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Consulta",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarConsulta(?)}");
                            procedimiento.setInt(1, ((Consulta)tblConsultas.getSelectionModel().getSelectedItem()).getCodigoConsulta());
                            procedimiento.execute();
                            listaConsulta.remove(tblConsultas.getSelectionModel().getSelectedIndex());
                            tblConsultas.getSelectionModel().clearSelection();
                            limpiarControles();
                        
                        } catch (Exception e) {
                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar esta consulta porque está vinculado a otros registros en el sistema.");
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
                if(tblConsultas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblConsultas.setDisable(true);
                    btnHome.setDisable(true);
                    imgEditar.setImage(new Image("/org/alejandroecheverria/image/IconActua.png"));
                    imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                    activarControles();
                    cmbCodigoMascota.setDisable(true);
                    cmbCodigoVeterinario.setDisable(true);
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
                tblConsultas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblConsultas.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
        
        }
    
    }
    
    public boolean actualizar(){
        String horaTexto = txtHoraConsulta.getText().trim();
        try {
            if(fecha.getSelectedDate() == null || horaTexto.isEmpty() ||txtMotivo.getText().trim().isEmpty() || txtDiagnostico.getText().trim().isEmpty() ||
            txtObservaciones.getText().trim().isEmpty() ){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
        
            } else if(txtMotivo.getText().trim().length()>255 || txtDiagnostico.getText().trim().length()>255 || txtObservaciones.getText().trim().length()>255){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
                
            } else if(!horaTexto.matches("([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")){
            JOptionPane.showMessageDialog(null,"Formato de hora cita inválido. Usa HH:mm:ss");
            return false;
        
            } else {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarConsulta(?,?,?,?,?)}");
                Consulta registro = (Consulta)tblConsultas.getSelectionModel().getSelectedItem();
                registro.setFechaConsulta(LocalDateTime.of(fecha.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.parse(horaTexto, DateTimeFormatter.ofPattern("HH:mm:ss"))));
                registro.setMotivo(txtMotivo.getText().trim());
                registro.setDiagnostico(txtDiagnostico.getText().trim());
                registro.setObservaciones(txtObservaciones.getText().trim());
                
                procedimiento.setInt(1, registro.getCodigoConsulta());
                procedimiento.setTimestamp(2, java.sql.Timestamp.valueOf(registro.getFechaConsulta()));
                procedimiento.setString(3, registro.getMotivo());
                procedimiento.setString(4, registro.getDiagnostico());
                procedimiento.setString(5, registro.getObservaciones());
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
                tblConsultas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblConsultas.getSelectionModel().clearSelection();
                break;
            default:
        
        }
    }
    
    public ObservableList<Consulta> getConsulta(){
        ArrayList<Consulta> lista = new ArrayList<Consulta>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarConsultas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Consulta(resultado.getInt("codigoConsulta"),
                                resultado.getTimestamp("fechaConsulta").toLocalDateTime(),
                                resultado.getString("motivo"),
                                resultado.getString("diagnostico"),
                                resultado.getString("observaciones"),
                                resultado.getInt("codigoMascota"),
                                resultado.getInt("codigoVeterinario")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaConsulta = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Mascota> getMascota(){
        ArrayList<Mascota> lista = new ArrayList<Mascota>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarMascotas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Mascota(resultado.getInt("codigoMascota"),
                                resultado.getString("nombreMascota"),
                                resultado.getString("especie"),
                                resultado.getString("raza"),
                                resultado.getString("sexo"),
                                resultado.getDate("fechaNacimiento"),
                                resultado.getString("color"),
                                resultado.getFloat("pesoActualKg"),
                                resultado.getInt("codigoCliente")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaMascota = FXCollections.observableArrayList(lista);
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
    
    
    public Mascota buscarMascota(int codigoMascota){
        Mascota resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarMascota(?)}");
            procedimiento.setInt(1, codigoMascota);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Mascota(registro.getInt("codigoMascota"),
                                registro.getString("nombreMascota"),
                                registro.getString("especie"),
                                registro.getString("raza"),
                                registro.getString("sexo"),
                                registro.getDate("fechaNacimiento"),
                                registro.getString("color"),
                                registro.getFloat("pesoActualKg"),
                                registro.getInt("codigoCliente"));
            }
            
            
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        
        return resultado;
    }
    
    public Veterinario buscarVeterinario(int codigoVeterinario){
        Veterinario resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarVeterinario(?)}");
            procedimiento.setInt(1, codigoVeterinario);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Veterinario(registro.getInt("codigoVeterinario"),
                                registro.getString("nombreVeterinario"),
                                registro.getString("apellidoVeterinario"),
                                registro.getString("especialidad"),
                                registro.getString("telefonoVeterinario"),
                                registro.getString("correoVeterinario"),
                                registro.getDate("fechaIngreso"),
                                registro.getString("estado"));
            }
            
            
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        
        return resultado;
    }
    
    public void desactivarControles(){
        txtCodigoConsulta.setEditable(false);
        fecha.setDisable(true);
        txtHoraConsulta.setEditable(false);
        txtMotivo.setEditable(false);
        txtDiagnostico.setEditable(false);
        txtObservaciones.setEditable(false);
        cmbCodigoMascota.setDisable(true);
        cmbCodigoVeterinario.setDisable(true);
        
    }
    
    public void activarControles(){
        txtCodigoConsulta.setEditable(false);
        fecha.setDisable(false);   
        txtHoraConsulta.setEditable(true);
        txtMotivo.setEditable(true);
        txtDiagnostico.setEditable(true);
        txtObservaciones.setEditable(true);
        cmbCodigoMascota.setDisable(false);
        cmbCodigoVeterinario.setDisable(false);
            
    }
    
    public void limpiarControles(){
        txtCodigoConsulta.clear();
        fecha.setSelectedDate(null);
        txtHoraConsulta.clear();
        txtMotivo.clear();
        txtDiagnostico.clear();
        txtObservaciones.clear();
        cmbCodigoMascota.setValue(null);
        cmbCodigoVeterinario.setValue(null);
        
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
