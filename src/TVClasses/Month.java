package TVClasses;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class Month {
    private Date dateM;
    private int totalDurationM;
    private int avgDurationM;
    private int totalPointM;
    private int nbSessionsM;
    private String mainProjectM;
    private String GradeM;
    private ArrayList<Session> sessionMList = new ArrayList<>();
    
    private int nbDayMonth;
    private int month;
    
    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
    
    public Month(){
        this.dateM = null;
        this.nbDayMonth = 30;
        this.totalDurationM = 0;
        this.avgDurationM = 0;
        this.totalPointM = 0;
        this.nbSessionsM = 0;
        this.mainProjectM = null;
        this.GradeM = null;
        this.sessionMList = null;
        this.month = 0;
    }
    
    public Month(ArrayList<Session> sess){
        
        ArrayList<Integer> projectIds = Project.getProjectList();
        int[] projectCounts = new int[projectIds.size()];
        HashMap<Integer,Integer> ids = new HashMap<>();
        
        this.dateM = sess.get(0).getDateNF();
        this.nbSessionsM = sess.size();
        
        for(Session s : sess){
            this.totalDurationM += s.getDurationSession();
            this.totalPointM += s.getPointSession();
            this.sessionMList.add(s);
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
        this.mainProjectM = Project.getNameFromId(idMProject);
        
        this.month = this.dateM.getMonth();
        if(this.month == 0 || this.month == 2 || this.month == 4 || this.month == 6 || this.month == 7 || this.month == 9 || this.month == 11){
            this.nbDayMonth = 31;
        }else if(this.month == 1){
            this.nbDayMonth = 28;
        }else{
            this.nbDayMonth = 30;
        }
        this.avgDurationM = this.totalDurationM/this.nbDayMonth;
        
        if(this.totalDurationM == 0){
            this.GradeM = "F";
        }else if(this.totalDurationM > 0 && this.totalDurationM <= 2400){
            this.GradeM = "D";
        }else if(this.totalDurationM > 2400 && this.totalDurationM <= 4800){
            this.GradeM = "C";
        }else if(this.totalDurationM > 4800 && this.totalDurationM <= 7200){
            this.GradeM = "B";
        }else if(this.totalDurationM > 7200 && this.totalDurationM <= 9600){
            this.GradeM = "A";
        }else if(this.totalDurationM > 9600 && this.totalDurationM <= 12000){
            this.GradeM = "S";
        }else if(this.totalDurationM > 12000){
            this.GradeM = "Sigma";
        }
    }
    
    
    public Date getDateM(){
        return this.dateM;
    }
    public void setDateM(Date x){
        this.dateM = x;
    }
    
    public int getMonth(){
        return this.month;
    }
    
    public int getNbDayMonth(){
        return this.nbDayMonth;
    }
    
    public String getStringDate(){
        return sdf.format(getDateM());
    }
    
    public int getTotalDurationM(){
        return this.totalDurationM;
    }
    public void setTotalDurationM(int x){
        this.totalDurationM = x;
    }
    
    public int getAvgDurationM(){
        return this.avgDurationM;
    }
    
    public int getTotalPointM(){
        return this.totalPointM;
    }
    public void setTotalPointM(int x){
        this.totalPointM = x;
    }
    
    public int getNbSessionsM(){
        return this.nbSessionsM;
    }
    public void setNbSessionsM(int x){
        this.nbSessionsM = x;
    }
    
    public String getMainProjectM(){
        return this.mainProjectM;
    }
    public void setMainProjectM(String x){
        this.mainProjectM = x;
    }
    
    public String getGradeM(){
        return this.GradeM;
    }
    
    public ArrayList<Session> getSessionMList(){
        return this.sessionMList;
    }
}
