package FxCode;

import static FxCode.DashBoardFXMLController.p;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.sql.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;

public class ProjectSelectionFXMLController implements Initializable {

    @FXML
    private DialogPane dpProject;
    @FXML
    private Label lblHeader;
    @FXML
    private HBox hboxBtn;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnGiveUp;
    @FXML
    private Button btnFinish;

    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    
    private String statusTmp;
    private Date date;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblHeader.setText("Project: "+p.getNameProject());
        date = new Date();
        try{
            con = DBUtil.connection();
            ps = con.prepareStatement("SELECT * FROM Project WHERE IdProject=?;");
            ps.setInt(1, p.getIdProject());
            rs = ps.executeQuery();
            while(rs.next()){
                statusTmp = rs.getString("StatusProject");
            }
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("initialize method: SQLException");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
             alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
        
        if("In progress".equals(statusTmp)){
            btnGiveUp.setText("Give up");
            btnFinish.setText("Finish");
        }else if("Finished".equals(statusTmp)){
            btnGiveUp.setText("Give up");
            btnGiveUp.setDisable(true);
            btnFinish.setText("Continue");
        }else{
            btnGiveUp.setText("Continue");
            btnFinish.setText("Finish");
            btnFinish.setDisable(true);
        }
    }    

    @FXML
    private void deleteProject(ActionEvent event) {
        try{
            con = DBUtil.connection();
            //Delete all the session from that project
            ps = con.prepareStatement("DELETE FROM Session WHERE IdProject = ?;");
            ps.setInt(1, p.getIdProject());
            ps.executeUpdate();
            ps = con.prepareStatement("DELETE FROM Project WHERE IdProject=?;");
            ps.setInt(1, p.getIdProject());
            int i = ps.executeUpdate();
            if(i!=0){
                lblHeader.setText("DELETED !");
                btnDelete.setDisable(true);
                btnGiveUp.setDisable(true);
                btnFinish.setDisable(true);
            }
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("deleteProject method: SQLException");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
    }

    @FXML
    private void giveUpProject(ActionEvent event) {
        try{
            con = DBUtil.connection();
            ps = con.prepareStatement("UPDATE Project SET EndDateProject = ? , StatusProject=? WHERE IdProject=?;");
            if(statusTmp.equals("In progress")){
                java.sql.Date datePointsSQL = new java.sql.Date(date.getTime());
                ps.setDate(1, datePointsSQL);
                ps.setString(2, "Abandonned");
                btnGiveUp.setText("Continue");
                btnFinish.setDisable(true);
                ps.setInt(3, p.getIdProject());
                int i = ps.executeUpdate();
                if(i!=0){
                    lblHeader.setText("Project abandonned!");
                    btnDelete.setDisable(false);
                    btnGiveUp.setDisable(false);
                    btnFinish.setDisable(true);
                }
            }else{
                ps.setDate(1 , null);
                ps.setString(2, "In progress");
                btnGiveUp.setText("Give up");
                btnFinish.setDisable(false);
                ps.setInt(3, p.getIdProject());
                int i = ps.executeUpdate();
                if(i!=0){
                    lblHeader.setText("Project continued!");
                    btnDelete.setDisable(false);
                    btnGiveUp.setDisable(false);
                    btnFinish.setDisable(false);
                }
            }
            
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("giveUpProject method: SQLException");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
    }

    @FXML
    private void finishProject(ActionEvent event) {
        try{
            con = DBUtil.connection();
            ps = con.prepareStatement("UPDATE Project SET EndDateProject = ? , StatusProject=? WHERE IdProject=?;");
            if(statusTmp.equals("In progress")){
                java.sql.Date datePointsSQL = new java.sql.Date(date.getTime());
                ps.setDate(1, datePointsSQL);
                ps.setString(2, "Finished");
                btnFinish.setText("Continue");
                btnGiveUp.setDisable(true);
                ps.setInt(3, p.getIdProject());
                int i = ps.executeUpdate();
                if(i!=0){
                    lblHeader.setText("Project finished!");
                    btnDelete.setDisable(false);
                    btnGiveUp.setDisable(true);
                    btnFinish.setDisable(false);
                }
            }else{
                ps.setDate(1 , null);
                ps.setString(2, "In progress");
                btnFinish.setText("Finish");
                btnGiveUp.setDisable(false);
                ps.setInt(3, p.getIdProject());
                int i = ps.executeUpdate();
                if(i!=0){
                    lblHeader.setText("Project continued!");
                    btnDelete.setDisable(false);
                    btnGiveUp.setDisable(false);
                    btnFinish.setDisable(false);
                }
            }
            
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("finishProject method: SQLException");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
    }
    
}
