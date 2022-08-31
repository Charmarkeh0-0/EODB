package FxCode;

import java.util.Date;
import javafx.concurrent.Task;
import java.sql.*;
import javafx.scene.control.Alert;

public class Timer extends Task<String>{

    private String currentTime;
    private boolean started = false;
    private double elapsedTime;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private String seconds_string = String.format("%02d", seconds);
    private String minutes_string = String.format("%02d", minutes);
    private String hours_string = String.format("%02d", hours);
    
    //Database
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    private int duration;
    private int idProject=1;
    private int points;
    private Date date;
    
    public Timer(){
        this.started = false;
    }
    
    public boolean getStarted(){
        return this.started;
    }
    
    public double getElapsedTime(){
        return this.elapsedTime / 60000;
    }
    public void setElapsedTime(double x){
        this.elapsedTime = x * 60000;
    }
    
    public int getDuration(){
        return this.duration;
    }
    public void setDuration(int x){
        this.duration = x;
    }

    public int getIdProject(){
        return this.idProject;
    }
    public void setIdProject(int x){
        this.idProject = x;
    }
    
    public int getPoints(){
        return this.points;
    }
    public void setPoints(int x){
        this.points = x;
    }
    
    public Date getDate(){
        return this.date;
    }
    
    public void play(){
        this.started = true;
    }
    public void pause(){
        this.started = false;
    }
    
    @Override
    protected String call() throws Exception {
        while(started){
            Thread.sleep(1000);
            
            elapsedTime=elapsedTime-1000;
            hours = (int) Math.floor(elapsedTime/3600000);
            minutes = (int) Math.floor(elapsedTime/60000) % 60;
            seconds = (int) Math.floor(elapsedTime/1000)%60;
            
            hours_string = String.format("%02d", hours);        
            minutes_string = String.format("%02d", minutes);
            seconds_string = String.format("%02d", seconds);
            currentTime = (hours_string+" : "+minutes_string+" : "+seconds_string);
            updateValue(currentTime);
            
            if(this.elapsedTime<=1){
                currentTime = ("00 : 00 : 00");
                
                //Insert session in the database
                try{
                    con = DBUtil.connection();
                    date = new Date();
                    java.sql.Date datePointsSQL = new java.sql.Date(date.getTime());
                    java.sql.Time timePointsSQL = new java.sql.Time(date.getTime());

                    ps = con.prepareStatement("INSERT INTO Session(DateSession , TimeSession , DurationSession , PointSession , IdProject) VALUES(?,?,?,?,?);");
                    ps.setDate(1, datePointsSQL);
                    ps.setTime(2, timePointsSQL);
                    ps.setInt(3, getDuration());
                    ps.setInt(4, points);
                    ps.setInt(5, idProject);

                    ps.executeUpdate();
                }catch(SQLException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("call method: SQLException");
                    alert.setContentText(e.getMessage());
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                    alert.showAndWait();
                }
                break;
            }
        }
        return currentTime;
    }
    
}
