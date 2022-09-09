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
    private int timeCache = 0;
    private int projectCache = 0;
    
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
    	
    	if(this.timeCache==0) {
    		this.elapsedTime = x * 60000;
    	}else {
    		this.elapsedTime = this.timeCache;
    	}
        
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
    	if(this.timeCache == 0) {
    		this.idProject = x;
    	}else {
    		this.idProject = this.projectCache;
    	}
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
    
    public void getCaches() {
    	try {
    		con = DBUtil.connection();
    		ps = con.prepareStatement("SELECT * FROM Cache;");
    		rs = ps.executeQuery();
    		while(rs.next()) {
    			timeCache = rs.getInt("TimeCache");
    			projectCache = rs.getInt("IdProject");
    		}
    	}catch(SQLException e) {
    		Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("getCaches method: SQLException");
            alert.setContentText(e.getMessage());
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
    	}
    }
    
    public void resetCaches() {
	    try {
			con = DBUtil.connection();
			ps = con.prepareStatement("UPDATE Cache SET TimeCache=? ,IdProject=? WHERE IdCache = 1");
			ps.setInt(1, 0);
			ps.setString(2, null);
			ps.executeUpdate();
		}catch(SQLException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setHeaderText("getCaches method: SQLException");
	        alert.setContentText(e.getMessage());
	        alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
	        alert.showAndWait();
		}
    }
    
    
    @Override
    protected String call() throws Exception {
    	int i = 0;
        while(started){
        	
            Thread.sleep(1000);
            elapsedTime=elapsedTime-1000;
            i++;
            hours = (int) Math.floor(elapsedTime/3600000);
            minutes = (int) Math.floor(elapsedTime/60000) % 60;
            seconds = (int) Math.floor(elapsedTime/1000)%60;
            
            hours_string = String.format("%02d", hours);        
            minutes_string = String.format("%02d", minutes);
            seconds_string = String.format("%02d", seconds);
            currentTime = (hours_string+" : "+minutes_string+" : "+seconds_string);
            updateValue(currentTime);
            
            if(i>=60){
            	con = DBUtil.connection();
            	ps = con.prepareStatement("UPDATE Cache SET TimeCache=? ,IdProject=? WHERE IdCache = 1");
            	ps.setInt(1, (int) elapsedTime);
            	ps.setInt(2, this.idProject);
            	ps.executeUpdate();
            	i = 0;
            }
            
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
                    
                    ps = con.prepareStatement("UPDATE Cache SET TimeCache=? , IdProject = ? WHERE IdCache=1");
                    ps.setInt(1, 0);
                    ps.setString(2, null);
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
