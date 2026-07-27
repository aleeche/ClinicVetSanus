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
import org.alejandroecheverria.bean.Vacunacion;
import org.alejandroecheverria.bean.Mascota;
import org.alejandroecheverria.bean.Vacuna;
import org.alejandroecheverria.bean.Veterinario;
import org.alejandroecheverria.db.Conexion;
import org.alejandroecheverria.report.GenerarReporte;



public class VacunacionController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Vacunacion> listaVacunacion;
    private ObservableList<Mascota> listaMascota;
    private ObservableList<Vacuna> listaVacuna;
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
    @FXML private TextField txtCodigoVacunacion;
    @FXML private TextField txtObservaciones;
    @FXML private ComboBox cmbCodigoMascota;
    @FXML private ComboBox cmbCodigoVacuna;
    @FXML private ComboBox cmbCodigoVeterinario;
    @FXML private TableView tblVacunaciones;
    @FXML private TableColumn colCodigoVacunacion;
    @FXML private TableColumn colFechaAplicacion;
    @FXML private TableColumn colObservaciones;
    @FXML private TableColumn colCodigoMascota;
    @FXML private TableColumn colCodigoVacuna;
    @FXML private TableColumn colCodigoVeterinario;
    @FXML private GridPane grpFechaAplicacion;

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
        anchorPaneFin.setMinWidth(294);        
        anchorPaneFin.setMaxWidth(294);        
        fecha.setStyle("-fx-alignment: CENTER;");
        grpFechaAplicacion.add(anchorPaneFin, 3, 0);
        
        Platform.runLater(() -> {
        for (Node node : fecha.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        
        
        
        cmbCodigoMascota.setItems(getMascota());
        
        cmbCodigoVacuna.setItems(getVacuna());
        
        cmbCodigoVeterinario.setItems(getVeterinario());
        cargarDatos();
        
    }
    
    public void cargarDatos(){
        tblVacunaciones.setItems(getVacunacion());
        colCodigoVacunacion.setCellValueFactory(new PropertyValueFactory<Vacunacion,Integer>("codigoVacunacion"));
        colFechaAplicacion.setCellValueFactory(new PropertyValueFactory<Vacunacion,Date>("fechaAplicacion"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<Vacunacion,String>("observaciones"));
        colCodigoMascota.setCellValueFactory(new PropertyValueFactory<Vacunacion,Integer>("codigoMascota"));
        colCodigoVacuna.setCellValueFactory(new PropertyValueFactory<Vacunacion,Integer>("codigoVacuna"));
        colCodigoVeterinario.setCellValueFactory(new PropertyValueFactory<Vacunacion,Integer>("codigoVeterinario"));
    }
    
    public void seleccionarElemento(){
        if (tblVacunaciones.getSelectionModel().getSelectedItem() != null){
            txtCodigoVacunacion.setText(String.valueOf(((Vacunacion)tblVacunaciones.getSelectionModel().getSelectedItem()).getCodigoVacunacion()));
            txtObservaciones.setText(((Vacunacion)tblVacunaciones.getSelectionModel().getSelectedItem()).getObservaciones());
            
            /*COMBOBOX*/
            cmbCodigoMascota.getSelectionModel().select(buscarMascota(((Vacunacion)tblVacunaciones.getSelectionModel().getSelectedItem()).getCodigoMascota()));
            cmbCodigoVacuna.getSelectionModel().select(buscarVacuna(((Vacunacion)tblVacunaciones.getSelectionModel().getSelectedItem()).getCodigoVacuna()));
            cmbCodigoVeterinario.getSelectionModel().select(buscarVeterinario(((Vacunacion)tblVacunaciones.getSelectionModel().getSelectedItem()).getCodigoVeterinario()));
            fecha.selectedDateProperty().set(((Vacunacion)tblVacunaciones.getSelectionModel().getSelectedItem()).getFechaAplicacion());
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblVacunaciones.setDisable(true);
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
                tblVacunaciones.setDisable(false);
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
        Vacunacion registro = new Vacunacion();
        if(txtObservaciones.getText().trim().isEmpty() || fecha.getSelectedDate() == null ||cmbCodigoMascota.getSelectionModel().isEmpty() || cmbCodigoVacuna.getSelectionModel().isEmpty() ||cmbCodigoVeterinario.getSelectionModel().isEmpty() ){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
        } else if(txtObservaciones.getText().trim().length()>250){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
        
        } else {
            registro.setObservaciones(txtObservaciones.getText().trim());
            registro.setFechaAplicacion(fecha.getSelectedDate());
            registro.setCodigoMascota(((Mascota)cmbCodigoMascota.getSelectionModel().getSelectedItem()).getCodigoMascota());
            registro.setCodigoVacuna(((Vacuna)cmbCodigoVacuna.getSelectionModel().getSelectedItem()).getCodigoVacuna());
            registro.setCodigoVeterinario(((Veterinario)cmbCodigoVeterinario.getSelectionModel().getSelectedItem()).getCodigoVeterinario());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarVacunacion(?,?,?,?,?)}");
                procedimiento.setDate(1, new java.sql.Date(registro.getFechaAplicacion().getTime()));
                procedimiento.setString(2, registro.getObservaciones());
                procedimiento.setInt(3, registro.getCodigoMascota());
                procedimiento.setInt(4, registro.getCodigoVacuna());
                procedimiento.setInt(5, registro.getCodigoVeterinario());
                procedimiento.execute();
                listaVacunacion.add(registro);
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
                tblVacunaciones.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblVacunaciones.getSelectionModel().clearSelection();
                break;
            default:
                if(tblVacunaciones.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Vacunación",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarVacunacion(?)}");
                            procedimiento.setInt(1, ((Vacunacion)tblVacunaciones.getSelectionModel().getSelectedItem()).getCodigoVacunacion());
                            procedimiento.execute();
                            listaVacunacion.remove(tblVacunaciones.getSelectionModel().getSelectedIndex());
                            tblVacunaciones.getSelectionModel().clearSelection();
                            limpiarControles();
                        
                        } catch (Exception e) {
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar esta vacunación porque está vinculado a otros registros en el sistema.");
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
                if(tblVacunaciones.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblVacunaciones.setDisable(true);
                    btnHome.setDisable(true);
                    imgEditar.setImage(new Image("/org/alejandroecheverria/image/IconActua.png"));
                    imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                    activarControles();
                    cmbCodigoMascota.setDisable(true);
                    cmbCodigoVacuna.setDisable(true);
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
                tblVacunaciones.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblVacunaciones.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
        
        }
    
    }
    
    public boolean actualizar(){
        try {
            if(txtObservaciones.getText().trim().isEmpty() || fecha.getSelectedDate() == null){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
            } else if(txtObservaciones.getText().trim().length()>250){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
        
            } else {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarVacunacion(?,?,?)}");
                Vacunacion registro = (Vacunacion)tblVacunaciones.getSelectionModel().getSelectedItem();
                registro.setFechaAplicacion(fecha.getSelectedDate());
                registro.setObservaciones(txtObservaciones.getText().trim());
                procedimiento.setInt(1, registro.getCodigoVacunacion());
                procedimiento.setDate(2, new java.sql.Date(registro.getFechaAplicacion().getTime()));
                procedimiento.setString(3, registro.getObservaciones());
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
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblVacunaciones.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblVacunaciones.getSelectionModel().clearSelection();
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

            GenerarReporte.mostrarReporte("ReporteCarneVacunacion.jasper", "Reporte Carné de Vacunación", parametros);

        } catch (Exception e) {
            throw new RuntimeException("Error al leer la imagen", e);
        }
    }
    
    public ObservableList<Vacunacion> getVacunacion(){
        ArrayList<Vacunacion> lista = new ArrayList<Vacunacion>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarVacunaciones}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Vacunacion(resultado.getInt("codigoVacunacion"),
                                resultado.getDate("fechaAplicacion"),
                                resultado.getString("observaciones"),
                                resultado.getInt("codigoMascota"),
                                resultado.getInt("codigoVacuna"),
                                resultado.getInt("codigoVeterinario")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaVacunacion = FXCollections.observableArrayList(lista);
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
    
    public ObservableList<Vacuna> getVacuna(){
        ArrayList<Vacuna> lista = new ArrayList<Vacuna>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarVacunas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Vacuna(resultado.getInt("codigoVacuna"),
                                resultado.getString("nombreVacuna"),
                                resultado.getString("descripcion"),
                                resultado.getString("dosis"),
                                resultado.getInt("frecuenciaMeses")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaVacuna = FXCollections.observableArrayList(lista);
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
    
    public Vacuna buscarVacuna(int codigoVacuna){
        Vacuna resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarVacuna(?)}");
            procedimiento.setInt(1, codigoVacuna);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Vacuna(registro.getInt("codigoVacuna"),
                                registro.getString("nombreVacuna"),
                                registro.getString("descripcion"),
                                registro.getString("dosis"),
                                registro.getInt("frecuenciaMeses"));
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
        txtCodigoVacunacion.setEditable(false);
        fecha.setDisable(true);
        txtObservaciones.setEditable(false);
        cmbCodigoMascota.setDisable(true);
        cmbCodigoVacuna.setDisable(true);
        cmbCodigoVeterinario.setDisable(true);   
    }
    
    public void activarControles(){
        txtCodigoVacunacion.setEditable(false);
        fecha.setDisable(false);   
        txtObservaciones.setEditable(true);
        cmbCodigoMascota.setDisable(false);
        cmbCodigoVacuna.setDisable(false);
        cmbCodigoVeterinario.setDisable(false);
            
    }
    
    public void limpiarControles(){
        txtCodigoVacunacion.clear();
        fecha.setSelectedDate(null);
        txtObservaciones.clear();
        cmbCodigoMascota.setValue(null);
        cmbCodigoVacuna.setValue(null);
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
