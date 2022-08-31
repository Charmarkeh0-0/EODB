package FxCode;

import java.sql.*;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class DBUtil {
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/EODB";
    private static String username = "Bean";
    private static String password = "1A?0L2I9f?";
    private static String db = "EODB";
    
    public static Connection connection(){
        Connection con = null;
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url,username,password);
        }catch(ClassNotFoundException | SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("connection method : SQLException");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.showAndWait();
        }
        return con;
    }
}
