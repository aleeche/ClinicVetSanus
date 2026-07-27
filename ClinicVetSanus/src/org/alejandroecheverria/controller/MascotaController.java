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
import org.alejandroecheverria.bean.Cliente;
import org.alejandroecheverria.bean.Mascota;
import org.alejandroecheverria.db.Conexion;
import org.alejandroecheverria.report.GenerarReporte;


public class MascotaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Mascota> listaMascota;
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
    @FXML private TextField txtCodigoMascota;
    @FXML private TextField txtNombreMascota;
    @FXML private TextField txtEspecie;
    @FXML private TextField txtRaza;
    @FXML private TextField txtColor;
    @FXML private TextField txtPesoActualKg;
    @FXML private ComboBox<String> cmbSexo;
    @FXML private ComboBox cmbCodigoCliente;
    @FXML private TableView tblMascotas;
    @FXML private TableColumn colCodigoMascota;
    @FXML private TableColumn colNombreMascota;
    @FXML private TableColumn colEspecie;
    @FXML private TableColumn colRaza;
    @FXML private TableColumn colSexo;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colColor;
    @FXML private TableColumn colPesoActualKg;
    @FXML private TableColumn colCodigoCliente;
    @FXML private GridPane grpFechaNacimiento;

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
        anchorPaneFin.setMinWidth(258.5);        
        anchorPaneFin.setMaxWidth(258.5);        
        fecha.setStyle("-fx-alignment: CENTER;");
        grpFechaNacimiento.add(anchorPaneFin, 3, 2);
        
        Platform.runLater(() -> {
        for (Node node : fecha.lookupAll(".text-field")) {
            if (node instanceof TextField textField) {
                    textField.setEditable(false);
                }
            }
        });
        
        cmbSexo.setItems(FXCollections.observableArrayList("Macho", "Hembra"));
        
        
        cmbCodigoCliente.setItems(getCliente());
        cargarDatos();

    }
    
    public void cargarDatos(){
        tblMascotas.setItems(getMascota());
        colCodigoMascota.setCellValueFactory(new PropertyValueFactory<Mascota,Integer>("codigoMascota"));
        colNombreMascota.setCellValueFactory(new PropertyValueFactory<Mascota, String>("nombreMascota"));
        colEspecie.setCellValueFactory(new PropertyValueFactory<Mascota, String>("especie"));
        colRaza.setCellValueFactory(new PropertyValueFactory<Mascota, String>("raza"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Mascota, String>("sexo"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Mascota, Date>("fechaNacimiento"));
        colColor.setCellValueFactory(new PropertyValueFactory<Mascota, String>("color"));
        colPesoActualKg.setCellValueFactory(new PropertyValueFactory<Mascota, Float>("pesoActualKg"));
        colCodigoCliente.setCellValueFactory(new PropertyValueFactory<Mascota,Integer>("codigoCliente"));
    }
    
    
    public void seleccionarElemento(){
        if (tblMascotas.getSelectionModel().getSelectedItem() != null){
            txtCodigoMascota.setText(String.valueOf(((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getCodigoMascota()));
            txtNombreMascota.setText(((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getNombreMascota());
            txtEspecie.setText(((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getEspecie());
            txtRaza.setText(((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getRaza());
            cmbSexo.getSelectionModel().select(((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getSexo());
            txtColor.setText(((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getColor());
            txtPesoActualKg.setText(String.valueOf(((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getPesoActualKg()));
            /*COMBOBOX*/
            cmbCodigoCliente.getSelectionModel().select(buscarCliente(((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getCodigoCliente()));
            fecha.selectedDateProperty().set(((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getFechaNacimiento()); 
            
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblMascotas.setDisable(true);
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
                tblMascotas.setDisable(false);
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
        Mascota registro = new Mascota();

        if(txtNombreMascota.getText().trim().isEmpty() || txtEspecie.getText().trim().isEmpty() || txtRaza.getText().trim().isEmpty() || cmbSexo.getSelectionModel().isEmpty() ||
            txtColor.getText().trim().isEmpty() || txtPesoActualKg.getText().trim().isEmpty()||  cmbCodigoCliente.getSelectionModel().isEmpty() ||fecha.getSelectedDate() == null){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
        } else if(txtNombreMascota.getText().trim().length()>50 || txtEspecie.getText().trim().length()>50 || txtRaza.getText().trim().length()>50 ||txtColor.getText().trim().length()>50 ){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
        
        
        
        } else if (!txtPesoActualKg.getText().trim().matches("\\d{0,3}(\\.\\d{0,2})?")){
            JOptionPane.showMessageDialog(null,"El peso ingresado contiene un formato inválido (Ejemplo: 10.2) , (max. 3 enteros y 2 decimales).");
            return false;
           
        }else {
            registro.setNombreMascota(txtNombreMascota.getText().trim());
            registro.setEspecie(txtEspecie.getText().trim());
            registro.setRaza(txtRaza.getText().trim());
            registro.setSexo(cmbSexo.getSelectionModel().getSelectedItem());
            registro.setFechaNacimiento(fecha.getSelectedDate());
            registro.setColor(txtColor.getText().trim());
            registro.setPesoActualKg(Float.parseFloat(txtPesoActualKg.getText().trim()));
            registro.setCodigoCliente(((Cliente)cmbCodigoCliente.getSelectionModel().getSelectedItem()).getCodigoCliente());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarMascota(?,?,?,?,?,?,?,?)}");
                procedimiento.setString(1, registro.getNombreMascota());
                procedimiento.setString(2, registro.getEspecie());
                procedimiento.setString(3, registro.getRaza());
                procedimiento.setString(4, registro.getSexo());
                procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
                procedimiento.setString(6, registro.getColor());
                procedimiento.setFloat(7, registro.getPesoActualKg());
                procedimiento.setInt(8, registro.getCodigoCliente());
                procedimiento.execute();
                listaMascota.add(registro);
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
                tblMascotas.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblMascotas.getSelectionModel().clearSelection();
                
                break;
            default:
                if(tblMascotas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Mascota",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarMascota(?)}");
                            procedimiento.setInt(1, ((Mascota)tblMascotas.getSelectionModel().getSelectedItem()).getCodigoMascota());
                            procedimiento.execute();
                            listaMascota.remove(tblMascotas.getSelectionModel().getSelectedIndex());
                            tblMascotas.getSelectionModel().clearSelection();
                            limpiarControles();
                            
                        } catch (Exception e) {
                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar esta mascota porque está vinculado a otros registros en el sistema.");
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
                if(tblMascotas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblMascotas.setDisable(true);
                    btnHome.setDisable(true);
                    imgEditar.setImage(new Image("/org/alejandroecheverria/image/IconActua.png"));
                    imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                    activarControles();
                    cmbCodigoCliente.setDisable(true);
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
                tblMascotas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblMascotas.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
                
        
        }
    
    }
    
    public boolean actualizar(){
        try {
            if(txtNombreMascota.getText().trim().isEmpty() || txtEspecie.getText().trim().isEmpty() || txtRaza.getText().trim().isEmpty() || cmbSexo.getSelectionModel().isEmpty() ||
            txtColor.getText().trim().isEmpty() || txtPesoActualKg.getText().trim().isEmpty()|| fecha.getSelectedDate() == null){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
        
            } else if(txtNombreMascota.getText().trim().length()>50 || txtEspecie.getText().trim().length()>50 || txtRaza.getText().trim().length()>50 ||txtColor.getText().trim().length()>50 ){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo."); 
                return false;
        
        
            } else if (!txtPesoActualKg.getText().matches("\\d{0,3}(\\.\\d{0,2})?")){
                JOptionPane.showMessageDialog(null,"El peso ingresado contiene un formato inválido (Ejemplo: 10.2) , (max. 3 enteros y 2 decimales).");
                return false;
            
 
            } else {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarMascota(?,?,?,?,?,?,?,?)}");
                Mascota registro = (Mascota)tblMascotas.getSelectionModel().getSelectedItem();
                registro.setNombreMascota(txtNombreMascota.getText().trim());
                registro.setEspecie(txtEspecie.getText().trim());
                registro.setRaza(txtRaza.getText().trim());
                registro.setSexo(cmbSexo.getSelectionModel().getSelectedItem());
                registro.setFechaNacimiento(fecha.getSelectedDate());
                registro.setColor(txtColor.getText().trim());
                registro.setPesoActualKg(Float.parseFloat(txtPesoActualKg.getText().trim())); 
                
                procedimiento.setInt(1, registro.getCodigoMascota());
                procedimiento.setString(2, registro.getNombreMascota());
                procedimiento.setString(3, registro.getEspecie());
                procedimiento.setString(4, registro.getRaza());
                procedimiento.setString(5, registro.getSexo());
                procedimiento.setDate(6, new java.sql.Date(registro.getFechaNacimiento().getTime()));
                procedimiento.setString(7, registro.getColor());
                procedimiento.setFloat(8, registro.getPesoActualKg());
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
                if(tblMascotas.getSelectionModel().getSelectedItem() != null){
                    imprimirReporte();
                
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento.");
                }
                break;
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblMascotas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblMascotas.getSelectionModel().clearSelection();
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
            
            InputStream subreporte = getClass().getResourceAsStream("/org/alejandroecheverria/report/SubReporteMascotas.jasper");
            parametros.put("SUBREPORT", subreporte);

            int codCliente = Integer.valueOf(((Cliente)cmbCodigoCliente.getSelectionModel().getSelectedItem()).getCodigoCliente());
            parametros.put("codCliente", codCliente);
            GenerarReporte.mostrarReporte("ReporteMascotas.jasper", "Reporte de Mascotas", parametros);

        } catch (Exception e) {
            throw new RuntimeException("Error al leer la imagen", e);
        }
        
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
    
    public void desactivarControles(){
        txtCodigoMascota.setEditable(false);
        txtNombreMascota.setEditable(false);
        txtEspecie.setEditable(false);
        txtRaza.setEditable(false);
        cmbSexo.setDisable(true);
        txtColor.setEditable(false);
        txtPesoActualKg.setEditable(false);
        cmbCodigoCliente.setDisable(true);
        fecha.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoMascota.setEditable(false);
        txtNombreMascota.setEditable(true);
        txtEspecie.setEditable(true);
        txtRaza.setEditable(true);
        cmbSexo.setDisable(false);
        txtColor.setEditable(true);
        txtPesoActualKg.setEditable(true);
        cmbCodigoCliente.setDisable(false);
        fecha.setDisable(false);       
    }
    
    
    
    public void limpiarControles(){
        txtCodigoMascota.clear();
        txtNombreMascota.clear();
        txtEspecie.clear();
        txtRaza.clear();
        cmbSexo.setValue(null);
        txtColor.clear();
        txtPesoActualKg.clear();
        cmbCodigoCliente.setValue(null);
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

