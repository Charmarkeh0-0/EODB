package TVClasses;

import FxCode.DBUtil;
import java.util.Date;
import java.sql.*;
import javafx.scene.control.Alert;

public class TODO {
    
    private int idTODO;
    private String nameTODO;
    private String statusTODO;
    private int nbSubTODO;
    private int idProject;
    private String parent;
    
    public TODO(int id , String name , String status , int nbSub , int idProject , String parent){
        this.idTODO = id;
        this.nameTODO = name;
        this.statusTODO = status;
        this.nbSubTODO = nbSub;
        this.idProject = idProject;
        this.parent = parent;
    }

    public int getIdTODO() {
        return idTODO;
    }

    public void setIdTODO(int idTODO) {
        this.idTODO = idTODO;
    }

    public String getNameTODO() {
        return nameTODO;
    }

    public void setNameTODO(String nameTODO) {
        this.nameTODO = nameTODO;
    }

    public String getStatusTODO() {
        return statusTODO;
    }

    public void setStatusTODO(String statusTODO) {
        this.statusTODO = statusTODO;
    }

    public int getNbSubTODO() {
        return nbSubTODO;
    }

    public void setNbSubTODO(int nbSubTODO) {
        this.nbSubTODO = nbSubTODO;
    }

    public int getIdProject() {
        return idProject;
    }

    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }
    
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
    
    public static TODO getTODOFromName(String name){
        Connection con;
        TODO s = null;
        try{
            con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM TODO WHERE NameTODO = ?;");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                s = new TODO(
                    rs.getInt("IdTODO"),
                    rs.getString("NameTODO"),
                    rs.getString("StatusTODO"),
                    rs.getInt("NBSubTODO"),
                    rs.getInt("IdProject"),
                    rs.getString("Parent")
                );
            }
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("getTODOFromId method: SQLException");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return s;
    }
    
    
}
