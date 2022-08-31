package FxCode;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreenFXMLController implements Initializable {

    @FXML
    private AnchorPane pane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        splash();
    }    
    
    private void splash(){
        new Thread(){
            public void run(){
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
                Platform.runLater(new Runnable(){
                    public void run(){
                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("DashBoardFXML.fxml"));
                            Stage stage = new Stage();
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource("dashboardfxml.css").toExternalForm());
                            Image icon = new Image("Images/Logo.png");
                            stage.getIcons().add(icon);
                            stage.setTitle("EODB");
                            stage.setScene(scene);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.show();
                            
                            pane.getScene().getWindow().hide();
                        } catch (IOException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("run method : IOException");
                            alert.setContentText(ex.getMessage());
                            alert.initStyle(StageStyle.UNDECORATED);
                            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                            alert.showAndWait();
                        }
                        
                    }
                });
            }
        }.start();
    }
}
