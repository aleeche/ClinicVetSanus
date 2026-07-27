package org.alejandroecheverria.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
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
import org.alejandroecheverria.bean.Cita;
import org.alejandroecheverria.bean.Mascota;
import org.alejandroecheverria.bean.Veterinario;
import org.alejandroecheverria.db.Conexion;


public class CitaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Cita> listaCita;
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
    @FXML private TextField txtCodigoCita;
    @FXML private TextField txtHoraCita;
    @FXML private TextField txtMotivo;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox cmbCodigoMascota;
    @FXML private ComboBox cmbCodigoVeterinario;
    @FXML private TableView tblCitas;
    @FXML private TableColumn colCodigoCita;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colHoraCita;
    @FXML private TableColumn colMotivo;
    @FXML private TableColumn colEstado;
    @FXML private TableColumn colCodigoMascota;
    @FXML private TableColumn colCodigoVeterinario;
    @FXML private GridPane grpFechaCita;

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
        anchorPaneFin.setMinWidth(294.5);        
        anchorPaneFin.setMaxWidth(294.5);        
        fecha.setStyle("-fx-alignment: CENTER;");
        grpFechaCita.add(anchorPaneFin, 3, 0);
        
        Platform.runLater(() -> {
        for (Node node : fecha.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        
        
        cmbEstado.setItems(FXCollections.observableArrayList("Pendiente", "Completada","Cancelada"));
        
        cmbCodigoMascota.setItems(getMascota());
        
        cmbCodigoVeterinario.setItems(getVeterinario());
        cargarDatos();
        
    }
    
    public void cargarDatos(){
        tblCitas.setItems(getCita());
        colCodigoCita.setCellValueFactory(new PropertyValueFactory<Cita,Integer>("codigoCita"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Cita,Date>("fechaCita"));
        colHoraCita.setCellValueFactory(new PropertyValueFactory<Cita,Time>("horaCita"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<Cita,String>("motivo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<Cita,String>("estado"));
        colCodigoMascota.setCellValueFactory(new PropertyValueFactory<Cita,Integer>("codigoMascota"));
        colCodigoVeterinario.setCellValueFactory(new PropertyValueFactory<Cita,Integer>("codigoVeterinario"));
    
    }
    
    public void seleccionarElemento(){
        if (tblCitas.getSelectionModel().getSelectedItem() != null){
            txtCodigoCita.setText(String.valueOf(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoCita()));
            fecha.selectedDateProperty().set(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getFechaCita());
            txtHoraCita.setText(String.valueOf(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getHoraCita()));
            txtMotivo.setText(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getMotivo());
            cmbEstado.getSelectionModel().select(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getEstado());
            
            cmbCodigoMascota.getSelectionModel().select(buscarMascota(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoMascota()));
            cmbCodigoVeterinario.getSelectionModel().select(buscarVeterinario(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoVeterinario()));
        }
        
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblCitas.setDisable(true);
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
                tblCitas.setDisable(false);
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
        Cita registro = new Cita();
        String horaTexto = txtHoraCita.getText().trim();
        if(fecha.getSelectedDate() == null || txtHoraCita.getText().trim().isEmpty()|| txtMotivo.getText().trim().isEmpty() ||cmbEstado.getSelectionModel().isEmpty() || cmbCodigoMascota.getSelectionModel().isEmpty() ||cmbCodigoVeterinario.getSelectionModel().isEmpty() ){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
            
        } else if(txtMotivo.getText().trim().length()>250){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
          
        } else if(!horaTexto.matches("([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")){
            JOptionPane.showMessageDialog(null,"Formato de hora cita inválido. Usa HH:mm:ss");
            return false;
        
        
        } else {
            registro.setFechaCita(fecha.getSelectedDate());
            registro.setHoraCita(Time.valueOf(txtHoraCita.getText().trim()));
            registro.setMotivo(txtMotivo.getText().trim());
            registro.setEstado(cmbEstado.getSelectionModel().getSelectedItem());
            registro.setCodigoMascota(((Mascota)cmbCodigoMascota.getSelectionModel().getSelectedItem()).getCodigoMascota());
            registro.setCodigoVeterinario(((Veterinario)cmbCodigoVeterinario.getSelectionModel().getSelectedItem()).getCodigoVeterinario());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarCita(?,?,?,?,?,?)}");
                procedimiento.setDate(1, new java.sql.Date(registro.getFechaCita().getTime()));
                procedimiento.setTime(2, registro.getHoraCita());
                procedimiento.setString(3, registro.getMotivo());
                procedimiento.setString(4, registro.getEstado());
                procedimiento.setInt(5, registro.getCodigoMascota());
                procedimiento.setInt(6, registro.getCodigoVeterinario());
                procedimiento.execute();
                listaCita.add(registro);
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
                tblCitas.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblCitas.getSelectionModel().clearSelection();
                break;
            default:
                if(tblCitas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Cita",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarCita(?)}");
                            procedimiento.setInt(1, ((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoCita());
                            procedimiento.execute();
                            listaCita.remove(tblCitas.getSelectionModel().getSelectedIndex());
                            tblCitas.getSelectionModel().clearSelection();
                            limpiarControles();
                        
                        } catch (Exception e) {
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar esta cita porque está vinculado a otros registros en el sistema.");
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
                if(tblCitas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblCitas.setDisable(true);
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
                tblCitas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblCitas.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
        
        }
    
    }
    
    public boolean actualizar(){
        String horaTexto = txtHoraCita.getText().trim();
        try {
            if(fecha.getSelectedDate() == null || txtHoraCita.getText().trim().isEmpty()|| txtMotivo.getText().trim().isEmpty() ||cmbEstado.getSelectionModel().isEmpty() ){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            
            } else if(txtMotivo.getText().trim().length()>250){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
          
            } else if(!horaTexto.matches("([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")){
                JOptionPane.showMessageDialog(null,"Formato de hora cita inválido. Usa HH:mm:ss");
                return false;
        
            } else {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarCita(?,?,?,?,?)}");
                Cita registro = (Cita)tblCitas.getSelectionModel().getSelectedItem();
                registro.setFechaCita(fecha.getSelectedDate());
                registro.setHoraCita(Time.valueOf(txtHoraCita.getText().trim()));
                registro.setMotivo(txtMotivo.getText().trim());
                registro.setEstado(cmbEstado.getSelectionModel().getSelectedItem());
                procedimiento.setInt(1, registro.getCodigoCita());
                procedimiento.setDate(2, new java.sql.Date(registro.getFechaCita().getTime()));
                procedimiento.setTime(3, registro.getHoraCita());
                procedimiento.setString(4, registro.getMotivo());
                procedimiento.setString(5, registro.getEstado());
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
                tblCitas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblCitas.getSelectionModel().clearSelection();
                break;
            default:
        
        }
    }
    

    
    public ObservableList<Cita> getCita(){
        ArrayList<Cita> lista = new ArrayList<Cita>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarCitas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cita(resultado.getInt("codigoCita"),
                                resultado.getDate("fechaCita"),
                                resultado.getTime("horaCita"),
                                resultado.getString("motivo"),
                                resultado.getString("estado"),
                                resultado.getInt("codigoMascota"),
                                resultado.getInt("codigoVeterinario")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaCita = FXCollections.observableArrayList(lista);
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
        txtCodigoCita.setEditable(false);
        fecha.setDisable(true);
        txtHoraCita.setEditable(false);
        txtMotivo.setEditable(false);
        cmbEstado.setDisable(true);
        cmbCodigoMascota.setDisable(true);
        cmbCodigoVeterinario.setDisable(true);   
    }
    
    public void activarControles(){
        txtCodigoCita.setEditable(false);
        fecha.setDisable(false);
        txtHoraCita.setEditable(true);
        txtMotivo.setEditable(true);
        cmbEstado.setDisable(false);
        cmbCodigoMascota.setDisable(false);
        cmbCodigoVeterinario.setDisable(false);
            
    }
    
    public void limpiarControles(){
        txtCodigoCita.clear();
        fecha.setSelectedDate(null);
        txtHoraCita.clear();
        txtMotivo.clear();
        cmbEstado.setValue(null);
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
