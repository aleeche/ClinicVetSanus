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
import org.alejandroecheverria.bean.Tratamiento;
import org.alejandroecheverria.bean.Consulta;
import org.alejandroecheverria.db.Conexion;


public class TratamientoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Tratamiento> listaTratamiento;
    private ObservableList<Consulta> listaConsulta;
    private DatePicker fechaInicio;
    private DatePicker fechaFin;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Button btnHome;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private TextField txtCodigoTratamiento;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtMedicamentosIndicados;
    @FXML private ComboBox cmbCodigoConsulta;
    @FXML private TableView tblTratamientos;
    @FXML private TableColumn colCodigoTratamiento;
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colFechaInicio;
    @FXML private TableColumn colFechaFin;
    @FXML private TableColumn colMedicamentosIndicados;
    @FXML private TableColumn colCodigoConsulta;
    @FXML private GridPane grpFechaInicioFin;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fechaInicio = new DatePicker(Locale.ENGLISH);
        fechaInicio.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaInicio.getCalendarView().todayButtonTextProperty().set("Today");
        fechaInicio.getCalendarView().setShowWeeks(false);
        fechaInicio.getStylesheets().add("/org/alejandroecheverria/resource/plantilla.css");
        fechaInicio.setDisable(true);
        
        fechaFin = new DatePicker(Locale.ENGLISH);
        fechaFin.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaFin.getCalendarView().todayButtonTextProperty().set("Today");
        fechaFin.getCalendarView().setShowWeeks(false);
        fechaFin.getStylesheets().add("/org/alejandroecheverria/resource/plantilla.css");
        fechaFin.setDisable(true);
        
        AnchorPane anchorPaneFin = new AnchorPane();
        anchorPaneFin.getChildren().add(fechaInicio);
        AnchorPane.setTopAnchor(fechaInicio, 0.0);
        AnchorPane.setRightAnchor(fechaInicio, 0.0);
        AnchorPane.setBottomAnchor(fechaInicio, 0.0);
        AnchorPane.setLeftAnchor(fechaInicio, 0.0);
        anchorPaneFin.setPrefWidth(0);      
        anchorPaneFin.setMinWidth(282.5);        
        anchorPaneFin.setMaxWidth(282.5);        
        fechaInicio.setStyle("-fx-alignment: CENTER;");
        grpFechaInicioFin.add(anchorPaneFin, 1, 1);
        
        AnchorPane anchorPaneFinDos = new AnchorPane();
        anchorPaneFinDos.getChildren().add(fechaFin);
        AnchorPane.setTopAnchor(fechaFin, 0.0);
        AnchorPane.setRightAnchor(fechaFin, 0.0);
        AnchorPane.setBottomAnchor(fechaFin, 0.0);
        AnchorPane.setLeftAnchor(fechaFin, 0.0);
        anchorPaneFinDos.setPrefWidth(0);      
        anchorPaneFinDos.setMinWidth(288.5);        
        anchorPaneFinDos.setMaxWidth(288.5);        
        fechaFin.setStyle("-fx-alignment: CENTER;");
        grpFechaInicioFin.add(anchorPaneFinDos, 3, 1);
        
        Platform.runLater(() -> {
        for (Node node : fechaInicio.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        
        Platform.runLater(() -> {
        for (Node node : fechaFin.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        
//        grpFechaInicioFin.add(fechaInicio, 1, 1);
//        grpFechaInicioFin.add(fechaFin, 3, 1);
        
        cmbCodigoConsulta.setItems(getConsulta());
        cargarDatos();
        
    }
    
    public void cargarDatos(){
        tblTratamientos.setItems(getTratamiento());
        colCodigoTratamiento.setCellValueFactory(new PropertyValueFactory<Tratamiento,Integer>("codigoTratamiento"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Tratamiento, String>("descripcion"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<Tratamiento, Date>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<Tratamiento, Date>("fechaFin"));
        colMedicamentosIndicados.setCellValueFactory(new PropertyValueFactory<Tratamiento, String>("medicamentosIndicados"));
        colCodigoConsulta.setCellValueFactory(new PropertyValueFactory<Tratamiento,Integer>("codigoConsulta"));
    }
    
    public void seleccionarElemento(){
        if (tblTratamientos.getSelectionModel().getSelectedItem() != null){
            txtCodigoTratamiento.setText(String.valueOf(((Tratamiento)tblTratamientos.getSelectionModel().getSelectedItem()).getCodigoTratamiento()));
            txtDescripcion.setText(((Tratamiento)tblTratamientos.getSelectionModel().getSelectedItem()).getDescripcion());
            txtMedicamentosIndicados.setText(((Tratamiento)tblTratamientos.getSelectionModel().getSelectedItem()).getMedicamentosIndicados());
            /*COMBOBOX*/
            cmbCodigoConsulta.getSelectionModel().select(buscarConsulta(((Tratamiento)tblTratamientos.getSelectionModel().getSelectedItem()).getCodigoConsulta()));
            fechaInicio.selectedDateProperty().set(((Tratamiento)tblTratamientos.getSelectionModel().getSelectedItem()).getFechaInicio());
            fechaFin.selectedDateProperty().set(((Tratamiento)tblTratamientos.getSelectionModel().getSelectedItem()).getFechaFin());
            
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblTratamientos.setDisable(true);
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
                tblTratamientos.setDisable(false);
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
        Tratamiento registro = new Tratamiento();
        if(txtDescripcion.getText().trim().isEmpty() || txtMedicamentosIndicados.getText().trim().isEmpty() || cmbCodigoConsulta.getSelectionModel().isEmpty() ||fechaInicio.getSelectedDate() == null
                ||fechaFin.getSelectedDate() == null){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
        } else if(txtDescripcion.getText().trim().length()>255 || txtMedicamentosIndicados.getText().trim().length()>250){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
            
        } else if(fechaInicio.getSelectedDate().compareTo(fechaFin.getSelectedDate()) == 0){
            JOptionPane.showMessageDialog(null,"Las fechas ingresadas no pueden ser iguales.");
            return false;
            
        } else if(fechaInicio.getSelectedDate().compareTo(fechaFin.getSelectedDate()) > 0){
            JOptionPane.showMessageDialog(null,"La fecha de fin no puede ser anterior a la fecha de inicio.");
            return false;
           
        } else {
            registro.setDescripcion(txtDescripcion.getText().trim());
            registro.setFechaInicio(fechaInicio.getSelectedDate());
            registro.setFechaFin(fechaFin.getSelectedDate());
            registro.setMedicamentosIndicados(txtMedicamentosIndicados.getText().trim());
            registro.setCodigoConsulta(((Consulta)cmbCodigoConsulta.getSelectionModel().getSelectedItem()).getCodigoConsulta());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarTratamiento(?,?,?,?,?)}");
                procedimiento.setString(1, registro.getDescripcion());
                procedimiento.setDate(2, new java.sql.Date(registro.getFechaInicio().getTime()));
                procedimiento.setDate(3, new java.sql.Date(registro.getFechaFin().getTime()));
                procedimiento.setString(4, registro.getMedicamentosIndicados());
                procedimiento.setInt(5, registro.getCodigoConsulta());
                procedimiento.execute();
                listaTratamiento.add(registro);
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
                tblTratamientos.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblTratamientos.getSelectionModel().clearSelection();
                
                break;
            default:
                if(tblTratamientos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Tratamiento",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarTratamiento(?)}");
                            procedimiento.setInt(1, ((Tratamiento)tblTratamientos.getSelectionModel().getSelectedItem()).getCodigoTratamiento());
                            procedimiento.execute();
                            listaTratamiento.remove(tblTratamientos.getSelectionModel().getSelectedIndex());
                            tblTratamientos.getSelectionModel().clearSelection();
                            limpiarControles();
                            
                            
                        } catch (Exception e) {
                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar este tratamiento porque está vinculado a otros registros en el sistema.");
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
                if(tblTratamientos.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblTratamientos.setDisable(true);
                    btnHome.setDisable(true);
                    imgEditar.setImage(new Image("/org/alejandroecheverria/image/IconActua.png"));
                    imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                    activarControles();
                    cmbCodigoConsulta.setDisable(true);
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
                tblTratamientos.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblTratamientos.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
                
        
        }
    
    }
    
    public boolean actualizar(){
        try {
            if(txtDescripcion.getText().trim().isEmpty() || txtMedicamentosIndicados.getText().trim().isEmpty() || fechaInicio.getSelectedDate() == null
                ||fechaFin.getSelectedDate() == null){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
        
            } else if(txtDescripcion.getText().trim().length()>255 || txtMedicamentosIndicados.getText().trim().length()>250){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            } else if(fechaInicio.getSelectedDate().compareTo(fechaFin.getSelectedDate()) == 0){
                JOptionPane.showMessageDialog(null,"Las fechas ingresadas no pueden ser iguales.");
                return false;
            
            } else if(fechaInicio.getSelectedDate().compareTo(fechaFin.getSelectedDate()) > 0){
                JOptionPane.showMessageDialog(null,"La fecha de fin no puede ser anterior a la fecha de inicio.");
                return false;
           
            } else {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarTratamiento(?,?,?,?,?)}");
                Tratamiento registro = (Tratamiento)tblTratamientos.getSelectionModel().getSelectedItem();
                registro.setDescripcion(txtDescripcion.getText().trim());
                registro.setFechaInicio(fechaInicio.getSelectedDate());
                registro.setFechaFin(fechaFin.getSelectedDate());
                registro.setMedicamentosIndicados(txtMedicamentosIndicados.getText().trim());
                procedimiento.setInt(1, registro.getCodigoTratamiento());
                procedimiento.setString(2, registro.getDescripcion());
                procedimiento.setDate(3, new java.sql.Date(registro.getFechaInicio().getTime()));
                procedimiento.setDate(4, new java.sql.Date(registro.getFechaFin().getTime()));
                procedimiento.setString(5, registro.getMedicamentosIndicados());
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
                tblTratamientos.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblTratamientos.getSelectionModel().clearSelection();
                break;
            default:
        
        }
    
    }
    
    public ObservableList<Tratamiento> getTratamiento(){
        ArrayList<Tratamiento> lista = new ArrayList<Tratamiento>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarTratamientos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Tratamiento(resultado.getInt("codigoTratamiento"),
                                resultado.getString("descripcion"),
                                resultado.getDate("fechaInicio"),
                                resultado.getDate("fechaFin"),
                                resultado.getString("medicamentosIndicados"),
                                resultado.getInt("codigoConsulta")));
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        return listaTratamiento = FXCollections.observableArrayList(lista);
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
    
    public Consulta buscarConsulta(int codigoConsulta){
        Consulta resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarConsulta(?)}");
            procedimiento.setInt(1, codigoConsulta);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()){
                resultado = new Consulta(registro.getInt("codigoConsulta"),
                                registro.getTimestamp("fechaConsulta").toLocalDateTime(),
                                registro.getString("motivo"),
                                registro.getString("diagnostico"),
                                registro.getString("observaciones"),
                                registro.getInt("codigoMascota"),
                                registro.getInt("codigoVeterinario"));
            
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return resultado;
    }
    
    public void desactivarControles(){
        txtCodigoTratamiento.setEditable(false);
        txtDescripcion.setEditable(false);
        fechaInicio.setDisable(true);
        fechaFin.setDisable(true);
        txtMedicamentosIndicados.setEditable(false);
        cmbCodigoConsulta.setDisable(true);
        
    }
    
    public void activarControles(){
        txtCodigoTratamiento.setEditable(false);
        txtDescripcion.setEditable(true);
        txtMedicamentosIndicados.setEditable(true);
        fechaInicio.setDisable(false);
        fechaFin.setDisable(false);
        cmbCodigoConsulta.setDisable(false);
            
    }
    
    public void limpiarControles(){
        txtCodigoTratamiento.clear();
        txtDescripcion.clear();
        txtMedicamentosIndicados.clear();
        fechaInicio.setSelectedDate(null);
        fechaFin.setSelectedDate(null);
        cmbCodigoConsulta.setValue(null);
        
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
