package org.alejandroecheverria.controller;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.alejandroecheverria.system.Principal;
import org.alejandroecheverria.bean.Receta;
import org.alejandroecheverria.bean.Consulta;
import org.alejandroecheverria.bean.Medicamento;
import org.alejandroecheverria.db.Conexion;
import org.alejandroecheverria.report.GenerarReporte;


public class RecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Receta> listaReceta;
    private ObservableList<Consulta> listaConsulta;
    private ObservableList<Medicamento> listaMedicamento;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Button btnHome;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private TextField txtCodigoReceta;
    @FXML private TextField txtDosis;
    @FXML private TextField txtFrecuencia;
    @FXML private TextField txtDuracionDosis;
    @FXML private TextField txtIndicaciones;
    @FXML private ComboBox cmbCodigoConsulta;
    @FXML private ComboBox cmbCodigoMedicamento;
    @FXML private TableView tblRecetas;
    @FXML private TableColumn colCodigoReceta;
    @FXML private TableColumn colDosis;
    @FXML private TableColumn colFrecuencia;
    @FXML private TableColumn colDuracionDosis;
    @FXML private TableColumn colIndicaciones;
    @FXML private TableColumn colCodigoConsulta;
    @FXML private TableColumn colCodigoMedicamento;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbCodigoConsulta.setItems(getConsulta());
        
        cmbCodigoMedicamento.setItems(getMedicamento());
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblRecetas.setItems(getReceta());
        colCodigoReceta.setCellValueFactory(new PropertyValueFactory<Receta,Integer>("codigoReceta"));
        colDosis.setCellValueFactory(new PropertyValueFactory<Receta,String>("dosis"));
        colFrecuencia.setCellValueFactory(new PropertyValueFactory<Receta,String>("frecuencia"));
        colDuracionDosis.setCellValueFactory(new PropertyValueFactory<Receta,Integer>("duracionDosis"));
        colIndicaciones.setCellValueFactory(new PropertyValueFactory<Receta,String>("indicaciones"));
        colCodigoConsulta.setCellValueFactory(new PropertyValueFactory<Receta,Integer>("codigoConsulta"));
        colCodigoMedicamento.setCellValueFactory(new PropertyValueFactory<Receta,Integer>("codigoMedicamento"));
    }
    
    public void seleccionarElemento(){
        if (tblRecetas.getSelectionModel().getSelectedItem() != null){
            txtCodigoReceta.setText(String.valueOf(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta()));
            txtDosis.setText(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getDosis());
            txtFrecuencia.setText(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getFrecuencia());
            txtDuracionDosis.setText(String.valueOf(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getDuracionDosis()));
            txtIndicaciones.setText(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getIndicaciones());
            
            /*COMBOBOX*/
            cmbCodigoConsulta.getSelectionModel().select(buscarConsulta(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoConsulta()));
            cmbCodigoMedicamento.getSelectionModel().select(buscarMedicamento(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoMedicamento()));
            
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblRecetas.setDisable(true);
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
                tblRecetas.setDisable(false);
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
        Receta registro = new Receta();
        if(txtDosis.getText().trim().isEmpty() || txtFrecuencia.getText().trim().isEmpty() ||txtDuracionDosis.getText().trim().isEmpty() || 
                txtIndicaciones.getText().trim().isEmpty() || cmbCodigoConsulta.getSelectionModel().isEmpty() ||  cmbCodigoMedicamento.getSelectionModel().isEmpty() ){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
        } else if(txtCodigoReceta.getText().trim().length()>100 || txtDosis.getText().trim().length()>100 || txtFrecuencia.getText().trim().length()>100 || txtIndicaciones.getText().trim().length()>250){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
        
        } else if (!txtDuracionDosis.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Ingrese solamente números enteros (Duración Dosis).");
                return false;
        
        } else {
            registro.setDosis(txtDosis.getText().trim());
            registro.setFrecuencia(txtFrecuencia.getText().trim());
            registro.setDuracionDosis(Integer.parseInt(txtDuracionDosis.getText().trim()));
            registro.setIndicaciones(txtIndicaciones.getText().trim());
            registro.setCodigoConsulta(((Consulta)cmbCodigoConsulta.getSelectionModel().getSelectedItem()).getCodigoConsulta());
            registro.setCodigoMedicamento(((Medicamento)cmbCodigoMedicamento.getSelectionModel().getSelectedItem()).getCodigoMedicamento());
            try {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarReceta(?,?,?,?,?,?)}");
                procedimiento.setString(1, registro.getDosis());
                procedimiento.setString(2, registro.getFrecuencia());
                procedimiento.setInt(3, registro.getDuracionDosis());
                procedimiento.setString(4, registro.getIndicaciones());
                procedimiento.setInt(5, registro.getCodigoConsulta());
                procedimiento.setInt(6, registro.getCodigoMedicamento());
                procedimiento.execute();
                listaReceta.add(registro);
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
                tblRecetas.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblRecetas.getSelectionModel().clearSelection();
                break;
            default:
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Receta",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarReceta(?)}");
                            procedimiento.setInt(1, ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta());
                            procedimiento.execute();
                            listaReceta.remove(tblRecetas.getSelectionModel().getSelectedIndex());
                            tblRecetas.getSelectionModel().clearSelection();
                            limpiarControles();
                        
                        } catch (Exception e) {
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar esta receta porque está vinculado a otros registros en el sistema.");
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
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblRecetas.setDisable(true);
                    btnHome.setDisable(true);
                    imgEditar.setImage(new Image("/org/alejandroecheverria/image/IconActua.png"));
                    imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconCancelar.png"));
                    activarControles();
                    cmbCodigoConsulta.setDisable(true);
                    cmbCodigoMedicamento.setDisable(true);
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
                tblRecetas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblRecetas.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                break;
        
        }
    
    }
    
    public boolean actualizar(){
        try {
            if(txtDosis.getText().trim().isEmpty() || txtFrecuencia.getText().trim().isEmpty() ||txtDuracionDosis.getText().trim().isEmpty() || 
                txtIndicaciones.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
            return false;
        
            } else if(txtCodigoReceta.getText().trim().length()>100 || txtDosis.getText().trim().length()>100 || txtFrecuencia.getText().trim().length()>100 || txtIndicaciones.getText().trim().length()>250){
            JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
            return false;
        
            } else if (!txtDuracionDosis.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Ingrese solamente números enteros (Duración Dosis).");
                return false;
        
            } else {
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarReceta(?,?,?,?,?)}");
                Receta registro = (Receta)tblRecetas.getSelectionModel().getSelectedItem();
                registro.setDosis(txtDosis.getText().trim());
                registro.setFrecuencia(txtFrecuencia.getText().trim());
                registro.setDuracionDosis(Integer.parseInt(txtDuracionDosis.getText().trim()));
                registro.setIndicaciones(txtIndicaciones.getText().trim());
                procedimiento.setInt(1, registro.getCodigoReceta());
                procedimiento.setString(2, registro.getDosis());
                procedimiento.setString(3, registro.getFrecuencia());
                procedimiento.setInt(4, registro.getDuracionDosis());
                procedimiento.setString(5, registro.getIndicaciones());
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
                tblRecetas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblRecetas.getSelectionModel().clearSelection();
                break;
            default:
        
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
//        parametros.put("codigoReceta", null);
        
        try {
            InputStream is = getClass().getResourceAsStream("/org/alejandroecheverria/image/HojaMe.jpg");
            if (is == null) {
                throw new RuntimeException("No se encontró la imagen HojaMe.jpg");
            }
        
            BufferedImage imagen = ImageIO.read(is);
            parametros.put("imagenMembrete", imagen);

            GenerarReporte.mostrarReporte("ReporteRecetas.jasper", "Reporte de Recetas", parametros);

        } catch (Exception e) {
            throw new RuntimeException("Error al leer la imagen", e);
        }
    }
    
    public ObservableList<Receta> getReceta(){
        ArrayList<Receta> lista = new ArrayList<Receta>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarRecetas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Receta(resultado.getInt("codigoReceta"),
                                resultado.getString("dosis"),
                                resultado.getString("frecuencia"),
                                resultado.getInt("duracionDosis"),
                                resultado.getString("indicaciones"),
                                resultado.getInt("codigoConsulta"),
                                resultado.getInt("codigoMedicamento")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaReceta = FXCollections.observableArrayList(lista);
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
    
    public Medicamento buscarMedicamento(int codigoMedicamento){
        Medicamento resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarMedicamento(?)}");
            procedimiento.setInt(1, codigoMedicamento);
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()){
                resultado = new Medicamento(registro.getInt("codigoMedicamento"),
                                registro.getString("nombreMedicamento"),
                                registro.getString("descripcion"),
                                registro.getInt("stock"),
                                registro.getFloat("precioUnitario"),
                                registro.getDate("fechaVencimiento"),
                                registro.getInt("codigoProveedor"));
            
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return resultado;
    }
    
    public void desactivarControles(){
        txtCodigoReceta.setEditable(false);
        txtDosis.setEditable(false);
        txtFrecuencia.setEditable(false);
        txtDuracionDosis.setEditable(false);
        txtIndicaciones.setEditable(false);
        cmbCodigoConsulta.setDisable(true);
        cmbCodigoMedicamento.setDisable(true);
        
    }
    
    public void activarControles(){
        txtCodigoReceta.setEditable(false); 
        txtDosis.setEditable(true);
        txtFrecuencia.setEditable(true);
        txtDuracionDosis.setEditable(true);
        txtIndicaciones.setEditable(true);
        cmbCodigoConsulta.setDisable(false);
        cmbCodigoMedicamento.setDisable(false);
            
    }
    
    public void limpiarControles(){
        txtCodigoReceta.clear();
        txtDosis.clear();
        txtFrecuencia.clear();
        txtDuracionDosis.clear();
        txtIndicaciones.clear();
        cmbCodigoConsulta.setValue(null);
        cmbCodigoMedicamento.setValue(null);
        
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
