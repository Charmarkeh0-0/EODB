package TVClasses;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class Week {
    private Date dateW;
    private int totalDurationW;
    private int avgDurationW;
    private int totalPointW;
    private int nbSessionsW;
    private String mainProjectW;
    private String GradeW;
    private ArrayList<Session> sessionWList = new ArrayList<>();
    
    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
    
    public Week(){
        this.dateW = null;
        this.totalDurationW = 0;
        this.avgDurationW = 0;
        this.totalPointW = 0;
        this.nbSessionsW = 0;
        this.mainProjectW = null;
        this.GradeW = null;
        sessionWList = null;
    }
    
    public Week(ArrayList<Session> sess){
        
        ArrayList<Integer> projectIds = Project.getProjectList();
        int[] projectCounts = new int[projectIds.size()];
        HashMap<Integer,Integer> ids = new HashMap<>();
        
        this.dateW = sess.get(0).getDateNF();
        this.nbSessionsW = sess.size();
        
        for(Session s : sess){
            this.totalDurationW += s.getDurationSession();
            this.totalPointW += s.getPointSession();
            this.sessionWList.add(s);
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
        
        this.mainProjectW = Project.getNameFromId(idMProject);

        this.avgDurationW = this.totalDurationW/7;
        
        if(this.totalDurationW == 0){
            this.GradeW = "F";
        }else if(this.totalDurationW > 0 && this.totalDurationW <= 600){
            this.GradeW = "D";
        }else if(this.totalDurationW > 600 && this.totalDurationW <= 1200){
            this.GradeW = "C";
        }else if(this.totalDurationW > 1200 && this.totalDurationW <= 1800){
            this.GradeW = "B";
        }else if(this.totalDurationW > 1800 && this.totalDurationW <= 2400){
            this.GradeW = "A";
        }else if(this.totalDurationW > 2400 && this.totalDurationW <= 3000){
            this.GradeW = "S";
        }else if(this.totalDurationW >3000){
            this.GradeW = "Sigma";
        }
    }
 
    public Date getDateW(){
        return this.dateW;
    }
    public void setDateW(Date x){
        this.dateW = x;
    }
    
    public String getStringDate(){
        return sdf.format(getDateW());
    }
    
    public int getTotalDurationW(){
        return this.totalDurationW/60;
    }
    public void setTotalDurationW(int x){
        this.totalDurationW = x;
    }
    
    public int getAvgDurationW(){
        return this.avgDurationW;
    }
    
    public int getTotalPointW(){
        return this.totalPointW;
    }
    public void setTotalPointW(int x){
        this.totalPointW = x;
    }
    
    public int getNbSessionsW(){
        return this.nbSessionsW;
    }
    public void setNbSessionsW(int x){
        this.nbSessionsW = x;
    }
    
    public String getMainProjectW(){
        return this.mainProjectW;
    }
    public void setMainProjectW(String x){
        this.mainProjectW = x;
    }
    
    public String getGradeW(){
        return this.GradeW;
    }
    
    public ArrayList<Session> getSessionWList(){
        return this.sessionWList;
    }
}
