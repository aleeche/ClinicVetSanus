package org.alejandroecheverria.controller;


import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import org.alejandroecheverria.system.Principal;
import org.alejandroecheverria.bean.Vacuna;
import org.alejandroecheverria.db.Conexion;



public class VacunaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{GUARDAR, ELIMINAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Vacuna> listaVacuna;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Button btnHome;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private TextField txtCodigoVacuna;
    @FXML private TextField txtNombreVacuna;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtDosis;
    @FXML private TextField txtFrecuenciaMeses;
    @FXML private TableView tblVacunas;
    @FXML private TableColumn colCodigoVacuna;
    @FXML private TableColumn colNombreVacuna;
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colDosis;
    @FXML private TableColumn colFrecuenciaMeses;
    
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
        
    }
    
    public void cargarDatos(){
        tblVacunas.setItems(getVacuna());
        colCodigoVacuna.setCellValueFactory(new PropertyValueFactory<Vacuna,Integer>("codigoVacuna"));
        colNombreVacuna.setCellValueFactory(new PropertyValueFactory<Vacuna, String>("nombreVacuna"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Vacuna, String>("descripcion"));
        colDosis.setCellValueFactory(new PropertyValueFactory<Vacuna, String>("dosis"));
        colFrecuenciaMeses.setCellValueFactory(new PropertyValueFactory<Vacuna,Integer>("frecuenciaMeses"));
  
    }
    
    public void seleccionarElemento(){
        if (tblVacunas.getSelectionModel().getSelectedItem() != null){
            txtCodigoVacuna.setText(String.valueOf(((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getCodigoVacuna()));
            txtNombreVacuna.setText(((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getNombreVacuna());
            txtDescripcion.setText(((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getDescripcion());
            txtDosis.setText(((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getDosis());
            txtFrecuenciaMeses.setText(String.valueOf(((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getFrecuenciaMeses()));
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
                tblVacunas.setDisable(true);
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
                tblVacunas.setDisable(false);
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
        Vacuna registro = new Vacuna();
            if(txtNombreVacuna.getText().trim().isEmpty() || txtDescripcion.getText().trim().isEmpty() || txtDosis.getText().trim().isEmpty() || txtFrecuenciaMeses.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            } else if (!txtFrecuenciaMeses.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Ingrese solamente números enteros (Frecuencia Meses).");
                return false;

            }else if(txtNombreVacuna.getText().trim().length()>100 || txtDescripcion.getText().trim().length()>255 || txtDosis.getText().trim().length()>100){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else{
                registro.setNombreVacuna(txtNombreVacuna.getText().trim());
                registro.setDescripcion(txtDescripcion.getText().trim());
                registro.setDosis(txtDosis.getText().trim());
                registro.setFrecuenciaMeses(Integer.parseInt(txtFrecuenciaMeses.getText().trim()));
                try {
                    PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarVacuna(?,?,?,?)}");
                    procedimiento.setString(1, registro.getNombreVacuna());
                    procedimiento.setString(2, registro.getDescripcion());
                    procedimiento.setString(3, registro.getDosis());
                    procedimiento.setInt(4, registro.getFrecuenciaMeses());
                    procedimiento.execute();
                    listaVacuna.add(registro);
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
                /*btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");*/
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblVacunas.setDisable(false);
                btnHome.setDisable(false);
                imgNuevo.setImage(new Image("/org/alejandroecheverria/image/IconNuevo.png"));
                imgEliminar.setImage(new Image ("/org/alejandroecheverria/image/IconEliminar.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblVacunas.getSelectionModel().clearSelection();
                
                break;
            default:
                if(tblVacunas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?","Eliminar Cliente",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarVacuna(?)}");
                            procedimiento.setInt(1, ((Vacuna)tblVacunas.getSelectionModel().getSelectedItem()).getCodigoVacuna());
                            procedimiento.execute();
                            listaVacuna.remove(tblVacunas.getSelectionModel().getSelectedIndex());
                            tblVacunas.getSelectionModel().clearSelection();
                            limpiarControles();
                            
                        } catch (Exception e) {
                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "No se puede eliminar esta vacuna porque está vinculado a otros registros en el sistema.");
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
                if(tblVacunas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tblVacunas.setDisable(true);
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
                if (actualizar() == true){
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tblVacunas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                cargarDatos();
                tblVacunas.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                }
                
                break;
                
        }
    
    }
    
    public boolean actualizar(){
        try {
            if(txtNombreVacuna.getText().trim().isEmpty() || txtDescripcion.getText().trim().isEmpty() || txtDosis.getText().trim().isEmpty() || txtFrecuenciaMeses.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null,"Faltan campos por completar.");
                return false;
            } else if (!txtFrecuenciaMeses.getText().trim().matches("\\d+")){
                JOptionPane.showMessageDialog(null,"Ingrese solamente números enteros (Frecuencia Meses).");
                return false;

            }else if(txtNombreVacuna.getText().trim().length()>100 || txtDescripcion.getText().trim().length()>255 || txtDosis.getText().trim().length()>100){
                JOptionPane.showMessageDialog(null,"Número de caracteres superado en un campo.");
                return false;
            
            }else{
                PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarVacuna(?,?,?,?,?)}");
                Vacuna registro = (Vacuna)tblVacunas.getSelectionModel().getSelectedItem();
                registro.setNombreVacuna(txtNombreVacuna.getText().trim());
                registro.setDescripcion(txtDescripcion.getText().trim());
                registro.setDosis(txtDosis.getText().trim());
                registro.setFrecuenciaMeses(Integer.parseInt(txtFrecuenciaMeses.getText().trim()));
                procedimiento.setInt(1, registro.getCodigoVacuna());
                procedimiento.setString(2, registro.getNombreVacuna());
                procedimiento.setString(3, registro.getDescripcion());
                procedimiento.setString(4, registro.getDosis());
                procedimiento.setInt(5, registro.getFrecuenciaMeses());
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
                tblVacunas.setDisable(false);
                btnHome.setDisable(false);
                imgEditar.setImage(new Image ("/org/alejandroecheverria/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/alejandroecheverria/image/IconReporte.png"));
                tipoDeOperacion = operaciones.NINGUNO;
                tblVacunas.getSelectionModel().clearSelection();
                
                break;
        }
    
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
    
    public void desactivarControles(){
        txtCodigoVacuna.setEditable(false);
        txtNombreVacuna.setEditable(false);
        txtDescripcion.setEditable(false);
        txtDosis.setEditable(false);
        txtFrecuenciaMeses.setEditable(false); 
    }
    
    public void activarControles(){
        txtCodigoVacuna.setEditable(false);
        txtNombreVacuna.setEditable(true);
        txtDescripcion.setEditable(true);
        txtDosis.setEditable(true);
        txtFrecuenciaMeses.setEditable(true); 
                
    }
    
    public void limpiarControles(){
        txtCodigoVacuna.clear();
        txtNombreVacuna.clear();
        txtDescripcion.clear();
        txtDosis.clear();
        txtFrecuenciaMeses.clear();
        
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
