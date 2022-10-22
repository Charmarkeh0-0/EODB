package TVClasses;

import FxCode.DBUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.sql.*;
import java.text.ParseException;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class Project {
    
    private int idProject;
    private String nameProject;
    private String startDateProject;
    private String endDateProject;
    private int durationProject;
    private String statusProject;
    
    DateFormat df = new SimpleDateFormat("YYYY-MM-dd");
    
    public Project(){
        this.idProject = 0;
        this.nameProject = null;
        this.startDateProject = null;
        this.endDateProject = null;
        this.durationProject = 0;
        this.statusProject = null;
    }
    
    public Project(int id , String name , Date startDate , Date endDate , int duration , String status){
        this.idProject = id;
        this.nameProject = name;
        this.startDateProject = df.format(startDate);
        if(endDate==null){
            this.endDateProject = "-";
        }else{
            this.endDateProject = df.format(endDate);
        }
        this.durationProject = duration;
        this.statusProject = status;
    }
    
    public int getIdProject(){
        return this.idProject;
    }
    public void setIdProject(int x){
        this.idProject = x;
    }
    
    public String getNameProject(){
        return this.nameProject;
    }
    public void setNameProject(String x){
        this.nameProject = x;
    }

    public static String getNameFromId(int id){
        String nameId = null;
        try{
            Connection con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Project WHERE IdProject=?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                nameId = rs.getString("NameProject");
            }
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("getNameFromId : SQLException");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return nameId;
    }
    
    public String getStartDateProject(){
        return this.startDateProject;
    }
    public Date getStartDateDateProject() throws ParseException{
        return df.parse(this.startDateProject);
    }
    public void setStartDateProject(Date x){
        this.startDateProject = df.format(x);
    }
    
    public String getEndDateProject(){
        return this.endDateProject;
    }
    public Date getEndDateDateProject() throws ParseException{
        return df.parse(this.endDateProject);
    }
    public void setEndDateProject(Date x){
        this.endDateProject = df.format(x);
    }
    
    public int getDurationProject(){
        return this.durationProject/60;
    }
    public void setDurationProject(int x){
        this.durationProject = x;
    }
    
    public String getStatusProject(){
        return this.statusProject;
    }
    public void setStatusProject(String x){
        this.statusProject = x;
    }
    
    public static ArrayList<Integer> getProjectList(){
        ArrayList<Integer> projectList = new ArrayList<>();
        try{
            Connection con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Project;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                projectList.add(rs.getInt("IdProject"));
            }
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("getProjectList : SQLException");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return projectList;
    }
    
    public static Project getProjectFromId(int id){
        Connection con;
        Project p = null;
        try{
            con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Project WHERE IdProject = ?;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                p = new Project(
                    rs.getInt("IdProject"),
                    rs.getString("NameProject"),
                    rs.getDate("StartDateProject"),
                    rs.getDate("EndDateProject"),
                    rs.getInt("DurationProject"),
                    rs.getString("StatusProject")
                );
            }
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("getProjectFromId method: SQLException");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return p;
    }
    
    public static int getIdFromName(String name){
        Connection con;
        int id = 0;
        try{
            con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Project WHERE NameProject = ?;");
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                id = rs.getInt("IdProject");
            }
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("getIdFromName method: SQLException");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return id;
    }
}
