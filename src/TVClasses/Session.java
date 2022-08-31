package TVClasses;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Session {
    
    private int idSession;
    private Date dateNF;
    private String dateSession;
    private String timeSession;
    private int durationSession;
    private int pointSession;
    private int idProject;
    private int daySession;
    private int dayInWeek;
    private int dayInYear;
    private int yearSession;
    private int weekSession;
    private int monthSession;
   
    
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH-mm-ss");
    Calendar cal = Calendar.getInstance();
    
    public Session (){
        this.idSession = 0;
        this.dateNF = null;
        this.dateSession = null;
        this.timeSession = null;
        this.durationSession = 0;
        this.pointSession = 0;
        this.idProject = 0;
        this.yearSession = 0;
        this.dayInYear = 0;
        this.weekSession = 0;
        this.dayInWeek = 0;
        this.monthSession = 0;
    }
    
    public Session (int id , Date date , int duration , int point , int idP){
        this.idSession = id;
        this.dateNF = date;
        this.dateSession = sdfDate.format(date);
        this.timeSession = sdfTime.format(date);
        this.durationSession = duration;
        this.pointSession = point;
        this.idProject = idP;
        
        this.yearSession = date.getYear();
        this.monthSession = date.getMonth();
        cal.set(Calendar.WEEK_OF_YEAR, this.yearSession);
        cal.setTime(this.dateNF);
        this.dayInWeek = cal.get(Calendar.DAY_OF_WEEK);
        this.dayInYear = cal.get(Calendar.DAY_OF_YEAR);
        this.daySession = cal.get(Calendar.DAY_OF_MONTH);
        this.weekSession = cal.get(Calendar.WEEK_OF_YEAR);
    }
    
    public int getIdSession(){
        return this.idSession;
    }
    public void setIdSession(int x){
        this.idSession = x;
    }
    
    public Date getDateNF(){
        return this.dateNF;
    }
    public void setDateNF(Date x){
        this.dateNF = x;
    }
    
    public String getDateSession(){
        return this.dateSession;
    }
    public String getTimeSession(){
        return this.timeSession;
    }
    
    public int getDurationSession(){
        return this.durationSession;
    }
    public void setDurationSession(int x){
        this.durationSession = x;
    }
    
    public int getPointSession(){
        return this.pointSession;
    }
    public void setPointSession(int x){
        this.pointSession = x;          
    }
    
    public int getIdProject(){
        return this.idProject;
    }
    public void setIdProject(int x){
        this.idProject = x;
    }
    
    public int getYearSession(){
        return this.yearSession;
    }
    
    public int getMonthSession(){
        return this.monthSession;
    }
    
    public int getWeekSession(){
        return this.weekSession;
    }
    public int getDaySession(){
        return this.daySession;
    }
    public int getDayWeek(){
        return this.dayInWeek;
    }
    public int getDayYear(){
        return this.dayInYear;
    }
}
