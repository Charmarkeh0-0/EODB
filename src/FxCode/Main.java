package FxCode;

import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("DashBoardFXML.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Image icon = new Image("Images/Logo.png");
        stage.getIcons().add(icon);
        stage.setTitle("EODB");
        stage.show();
        
        stage.setOnCloseRequest(event -> {
            event.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Done ?");
            alert.setContentText("Do you really want to close the app ?");
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            Optional<ButtonType> clickedButton = alert.showAndWait();

            if(clickedButton.get() == ButtonType.OK){
                System.exit(0);
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
