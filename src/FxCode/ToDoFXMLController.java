package FxCode;

import TVClasses.Project;
import TVClasses.TODO;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import java.sql.*;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;
import org.controlsfx.control.textfield.TextFields;


public class ToDoFXMLController implements Initializable {

    @FXML
    private Label lblTitle;
    @FXML
    private TreeView<String> trvToDo;
    @FXML
    private TextField txtNameTODO;
    @FXML
    private TextField txtProjectTODO;
    @FXML
    private Button btnCheckTODO;
    @FXML
    private Button btnUpdateTODO;
    @FXML
    private Button btnDeleteTODO;
    @FXML
    private Button btnAddTODO;
    @FXML
    private Button btnAddSubTODO;
    
    private ArrayList<TODO> listTODO = new ArrayList<>();
    private String currentNameTODO;
    private TODO todo = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Make project string list
        ArrayList<String> listProjectString = new ArrayList<>();
        for(Project p : DashBoardFXMLController.projectList){
            listProjectString.add(p.getNameProject());
        }
        TextFields.bindAutoCompletion(txtProjectTODO,listProjectString);
        updateTreeViewTD();
    }    

    @FXML
    private void contextTrv(ContextMenuEvent event) {
    }

    @FXML
    private void checkProject(MouseEvent event) {
        txtNameTODO.clear();
        txtProjectTODO.clear();
        txtProjectTODO.setDisable(false);
        btnAddSubTODO.setDisable(false);
        try{
            TreeItem<String> t = trvToDo.getSelectionModel().selectedItemProperty().get();
            todo = TODO.getTODOFromName(t.getValue()); 
            currentNameTODO = todo.getNameTODO();
            txtNameTODO.setText(currentNameTODO);
            Project p = Project.getProjectFromId(todo.getIdProject());
            txtProjectTODO.setText(p.getNameProject());
            if(todo.getParent()!=null){
                txtProjectTODO.setDisable(true);
                btnAddSubTODO.setDisable(true);
            }
        }catch(NullPointerException e){

        }
    }
    
    public void updateTODOList(){
        listTODO.clear();
        Connection con;
        try{
            con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM TODO ORDER BY IdTODO DESC;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                listTODO.add(new TODO(
                    rs.getInt("IdTODO"),
                    rs.getString("NameTODO"),
                    rs.getString("StatusTODO"),
                    rs.getInt("NBSubTODO"),
                    rs.getInt("IdProject"),
                    rs.getString("Parent")
                ));
            }
        }catch(SQLException | NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("updateTODOList method : Exception");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
    }
    
    public void verifyChecked(){
    }
    
    public void updateTreeViewTD(){
        updateTODOList();
        TreeItem<String> rootItem = new TreeItem<>("TODO");
        trvToDo.setRoot(rootItem);
        //Create branches 
        ArrayList<TreeItem<String>> branchItems = new ArrayList<>();
        for(TODO t : listTODO){
            if(t.getParent()==null){
                if(t.getStatusTODO().equals("On going")){
                    branchItems.add(new TreeItem<>(t.getNameTODO(),new ImageView(new Image("Images/circle-dot-solid.png"))));
                }else{
                    branchItems.add(new TreeItem<>(t.getNameTODO(),new ImageView(new Image("Images/circle-check-solid.png"))));
                }
            }
        }
        
        //Create leafs 
        for(TreeItem ti : branchItems){
            for(TODO t : listTODO){
                if(ti.getValue().equals(t.getParent())){
                    if(t.getStatusTODO().equals("On going")){
                        ti.getChildren().add(new TreeItem<>(t.getNameTODO(),new ImageView(new Image("Images/circle-dot-solid-bis.png"))));
                    }else{
                        ti.getChildren().add(new TreeItem<>(t.getNameTODO(),new ImageView(new Image("Images/circle-check-solid-bis.png"))));
                    }
                }
            }
        }
        rootItem.getChildren().addAll(branchItems);
        for(TreeItem ti : branchItems) {
        	ti.setExpanded(true);
        }
        rootItem.setExpanded(true);
        trvToDo.setShowRoot(false);
    }
    
    @FXML
    private void addTODO(ActionEvent event) {
        Connection con;
        try{
            con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO TODO (NameTODO,StatusTODO,NBSubTODO,IdProject,Parent) VALUES (?,?,?,?,?);");
            ps.setString(1, "new TODO");
            ps.setString(2, "On going");
            ps.setInt(3, 0);
            ps.setString(4, null);
            ps.setString(5, null);
            int x = ps.executeUpdate();
            if(x==0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("addTODO method : SQLException");
                alert.setContentText("Insertion error.");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.showAndWait();
            }
        }catch(SQLException | NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("addTODO method : Exception");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }finally{
            updateTreeViewTD();
        }
    }
    @FXML
    private void checkTODO(ActionEvent event) {
        Connection con;
        try{
            con = DBUtil.connection();
            int x = 0;
            if(todo.getStatusTODO().equals("On going")){
                PreparedStatement ps = con.prepareStatement("UPDATE TODO SET StatusTODO=? WHERE NameTODO=?;");
                ps.setString(1, "Finished");
                ps.setString(2, currentNameTODO);
                x = ps.executeUpdate();
            }else{
                PreparedStatement ps = con.prepareStatement("UPDATE TODO SET StatusTODO=? WHERE NameTODO=?;");
                ps.setString(1, "On going");
                ps.setString(2, currentNameTODO);
                x = ps.executeUpdate();
            }
            if(x==0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("checkTODO method : SQLException");
                alert.setContentText("Update error.");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                alert.showAndWait();
            }
        }catch(SQLException | NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("checkTODO method : Exception");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }finally{
            updateTreeViewTD();
        }
    }

    @FXML
    private void updateTODO(ActionEvent event) {
        int idTODO = todo.getIdTODO();
        String name = txtNameTODO.getText();
        String project = txtProjectTODO.getText();
        int idProject = Project.getIdFromName(project);
        Connection con;
        try{
            con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("UPDATE TODO SET NameTODO=? , IdProject=? WHERE IdTODO=?;");
            ps.setString(1, name);
            if(idProject == 0){
                ps.setString(2, null);
            }else{
                ps.setInt(2, idProject);
            }
            ps.setInt(3, idTODO);
            int x = ps.executeUpdate();
            if(x==0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("updateTODO method : SQLException");
                alert.setContentText("Update error.");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                alert.showAndWait();
            }
        }catch(SQLException | NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("updateTODO method : Exception");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }finally{
            updateTreeViewTD();
        }
    }

    @FXML
    private void deleteTODO(ActionEvent event) {
        Connection con;
        try{
            con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM TODO WHERE NameTODO=?;");
            ps.setString(1, currentNameTODO);
            int x = ps.executeUpdate();
            if(x==0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("deleteTODO method : SQLException");
                alert.setContentText("Deletion error.");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                alert.showAndWait();
            }
            if(todo.getParent()==null){
                ps = con.prepareStatement("DELETE FROM TODO WHERE Parent=?");
                ps.setString(1, currentNameTODO);
                int xx = ps.executeUpdate();
                if(xx==0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("deleteTODO method : SQLException");
                    alert.setContentText("Deletion error.");
                    alert.initStyle(StageStyle.UNDECORATED);
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                    alert.showAndWait();
                }
            }
        }catch(SQLException | NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("deleteTODO method : Exception");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }finally{
            updateTreeViewTD();
        }
    }

    @FXML
    private void addSubTODO(ActionEvent event) {
        Connection con;
        try{
            con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO TODO (NameTODO,StatusTODO,NBSubTODO,IdProject,Parent) VALUES (?,?,?,?,?);");
            ps.setString(1, "new SubTODO");
            ps.setString(2, "On going");
            ps.setInt(3, 0);
            ps.setInt(4, todo.getIdProject());
            ps.setString(5, currentNameTODO);
            int x = ps.executeUpdate();
            if(x==0){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("addSubTODO method : SQLException");
                alert.setContentText("Insertion error.");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                alert.showAndWait();
            }
        }catch(SQLException | NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("addSubTODO method : Exception");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }finally{
            updateTreeViewTD();
        }
    }
}
