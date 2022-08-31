package TVClasses;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Year {
    private Date dateY;
    private int nbDayYear;
    private int totalDurationY;
    private int avgDurationY;
    private int totalPointY;
    private int nbSessionsY;
    private String mainProjectY;
    private String GradeY;
    private ArrayList<Session> sessionYList =  new ArrayList<>();
    
    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
    Calendar cal = Calendar.getInstance();
    
    public Year(){
        this.dateY = null;
        this.nbDayYear = 365;
        this.totalDurationY = 0;
        this.avgDurationY = 0;
        this.totalPointY = 0;
        this.nbSessionsY = 0;
        this.mainProjectY = null;
        this.GradeY = null;
        this.sessionYList = null;
    }
    
    public Year(ArrayList<Session> sess){
        
        ArrayList<Integer> projectIds = Project.getProjectList();
        int[] projectCounts = new int[projectIds.size()];
        HashMap<Integer,Integer> ids = new HashMap<>();
        
        this.dateY = sess.get(0).getDateNF();
        cal.setTime(dateY);
        this.nbDayYear = cal.get(Calendar.DAY_OF_YEAR);
        this.nbSessionsY = sess.size();
        
        for(Session s : sess){
            this.totalDurationY += s.getDurationSession();
            this.totalPointY += s.getPointSession();
            this.sessionYList.add(s);
            int y=0;
            for(Integer i : projectIds){
                if(s.getIdProject()==i){
                    projectCounts[y] = projectCounts[y]+1;
                    ids.put(projectCounts[y], i);
                }
                y++;
            }
        }  
        
        Arrays.sort(projectCounts);
        int idMProject = ids.get(projectCounts[projectCounts.length-1]);
        this.mainProjectY = Project.getNameFromId(idMProject);
        
        this.avgDurationY = this.totalDurationY/this.nbDayYear;
        
        if(this.totalDurationY <= 28800){
            this.GradeY = "F";
        }else if(this.totalDurationY > 28800 && this.totalDurationY <= 57600){
            this.GradeY = "D";
        }else if(this.totalDurationY > 57600 && this.totalDurationY <= 86400){
            this.GradeY = "C";
        }else if(this.totalDurationY > 86400 && this.totalDurationY <= 115200){
            this.GradeY = "B";
        }else if(this.totalDurationY > 115200 && this.totalDurationY <= 144000){
            this.GradeY = "A";
        }else if(this.totalDurationY > 144000 && this.totalDurationY <= 172800){
            this.GradeY = "S";
        }else if(this.totalDurationY > 172800){
            this.GradeY = "Sigma";
        }
    }
    
    public Date getDateY(){
        return this.dateY;
    }
    public void setDateY(Date x){
        this.dateY = x;
    }
    
    public int getNbDayYear(){
        return this.nbDayYear;
    }
    
    public String getStringDate(){
        return sdf.format(getDateY());
    }
    
    public int getTotalDurationY(){
        return this.totalDurationY;
    }
    public void setTotalDurationY(int x){
        this.totalDurationY = x;
    }
    
    public int getAvgDurationY(){
        return this.avgDurationY;
    }
    
    public int getTotalPointY(){
        return this.totalPointY;
    }
    public void setTotalPointY(int x){
        this.totalPointY = x;
    }
    
    public int getNbSessionsY(){
        return this.nbSessionsY;
    }
    public void setNbSessionsY(int x){
        this.nbSessionsY = x;
    }
    
    public String getMainProjectY(){
        return this.mainProjectY;
    }
    public void setMainProjectY(String x){
        this.mainProjectY = x;
    }
    
    public String getGradeY(){
        return this.GradeY;
    }
    
    public ArrayList<Session> getSessionYList(){
        return this.sessionYList;
    }
}
