package FxCode;

import TVClasses.Month;
import TVClasses.Project;
import TVClasses.Session;
import TVClasses.Week;
import TVClasses.Year;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;

public class DashBoardFXMLController implements Initializable {
    
    //Clock 
    
    private Clock clock;
    private Thread th1;
    public static boolean yepClock = true;
    
    @FXML
    private AnchorPane paneDashBoard;
    @FXML
    private HBox topPane;
    @FXML
    private Label lblClock;
    
    //Timer 
    
    private Timer timer;
    private Thread th2;
    private double currentTimeElapsed = 0;
    
    @FXML
    private Slider sliderTimer;
    @FXML
    private Label lblTimer;
    @FXML
    private StackPane bottomPane;
    @FXML
    private HBox hBoxBtn;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnPause;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnToDo;
    
    @FXML
    private void startTimer(ActionEvent event) {
        boolean resume = true;
        try{
            if(!sliderTimer.isDisable()){
                timer = new Timer();
                timer.setElapsedTime(sliderTimer.getValue());
                timer.setDuration((int) sliderTimer.getValue());
                timer.setPoints((int) (sliderTimer.getValue()*100));
                timer.setIdProject(projectHM.get(cbProject.getValue()));
                timer.play();
            }else{
                timer = new Timer();
                timer.setElapsedTime(currentTimeElapsed);
                timer.play();
            }
        }catch(NullPointerException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("StartTimer method : NullPointerException");
            alert.setContentText("You need to select a project.");
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
            resume = false;
        }

        timer.valueProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
               currentTimeElapsed = timer.getElapsedTime();
               lblTimer.setText(t1);
               if(timer.getElapsedTime()*60000<=1){
                    timer.setElapsedTime(0);
                    btnStart.setDisable(false);
                    sliderTimer.setDisable(false);
                    btnPause.setDisable(true);
                    btnReset.setDisable(true);
                    
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText("Session finished.");
                    alert.setContentText("Well done! You finished a session.\n Keep on going.");
                    alert.initStyle(StageStyle.UNDECORATED);
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                    alert.showAndWait();   
                    
                    try{
                        Connection con;
                        PreparedStatement ps;
                        ResultSet rs;

                        con = DBUtil.connection();
                        ps = con.prepareStatement("SELECT * FROM Project WHERE IdProject = ?;");
                        ps.setInt(1, projectHM.get(cbProject.getValue()));
                        rs = ps.executeQuery();
                        int projectDuration = 0;
                        while(rs.next()){
                            projectDuration = rs.getInt("DurationProject");//rs.getInt("DurationSession");
                        }
                        projectDuration = (int) (projectDuration + timer.getDuration());
                        ps = con.prepareStatement("UPDATE Project SET DurationProject = ? WHERE IdProject = ?;");
                        ps.setInt(1, projectDuration);
                        ps.setInt(2, projectHM.get(cbProject.getValue()));

                        ps.executeUpdate();
                    }catch(SQLException e){
                        Alert alert2 = new Alert(AlertType.ERROR);
                        alert2.setHeaderText("StartTimer method : SQLException");
                        alert2.setContentText(e.getMessage());
                        alert2.initStyle(StageStyle.UNDECORATED);
                        alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                        alert2.showAndWait();
                    }catch(NullPointerException e){

                    }
                    updateComboBox();
                    updateTableViewProject();
                    
               }
            } 
        });
        
        if(resume){
            btnStart.setDisable(true);
            sliderTimer.setDisable(true);
            btnPause.setDisable(false);
            btnReset.setDisable(false);

            th2 = new Thread(timer);
            th2.setDaemon(true);
            th2.start();
        }     
    }

    @FXML
    private void pauseTimer(ActionEvent event) {
        timer.pause();
        btnStart.setDisable(false);
        btnPause.setDisable(true);
        
    }

    @FXML
    private void resetTimer(ActionEvent event) {
        timer.pause();
        sliderTimer.setDisable(false);
        btnStart.setDisable(false);
        btnPause.setDisable(true);
        btnReset.setDisable(true);
    }
    
    //Project
    
    public static ArrayList<Project> projectList = new ArrayList<>();
    private HashMap<String,Integer> projectHM = new HashMap<>();
    public static Project p;
    
    @FXML
    private Button btnAddProject;
    @FXML
    private ComboBox<String> cbProject;
    @FXML
    private TableView<Project> tvProject;
    
    private void updateComboBox(){
        
        //Select all the projects from database
        projectHM.clear();
        projectList.clear();
        try{
            Connection con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Project");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                projectList.add(new Project(
                        rs.getInt("IdProject"),
                        rs.getString("NameProject"),
                        rs.getDate("StartDateProject"),
                        rs.getDate("EndDateProject"),
                        rs.getInt("DurationProject"),
                        rs.getString("StatusProject")
                ));
                if("In progress".equals(rs.getString("StatusProject"))){
                    projectHM.put(rs.getString("NameProject") , rs.getInt("IdProject"));
                }
            }
        }catch(SQLException e){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("updateComboBox method : SQLException");
            alert.setContentText(e.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
        
        //Put the projects in the combobox

        cbProject.setItems(FXCollections.observableArrayList(new ArrayList<>(projectHM.keySet())));
    }

    @FXML
    private void createProject(ActionEvent event) {
        //Load the fxml and create a new popup dialog
        FXMLLoader fxmlloader = new FXMLLoader();
        fxmlloader.setLocation(getClass().getResource("AddProject.fxml"));
        DialogPane addProjectDP = null;
        try {
            addProjectDP = fxmlloader.load();
        } catch (IOException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Line 210 : IOException");
            alert.setContentText(ex.getMessage());
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
        
        //Get the student controller associated with the view 
        AddProjectController projectController = fxmlloader.getController();
        
        //Show the dialog pane
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(addProjectDP);
        dialog.initStyle(StageStyle.UNDECORATED);
        Optional<ButtonType> clickedButton = dialog.showAndWait();
        
        //When the ok button is pressed
        if(clickedButton.get() == ButtonType.OK){
            String name = projectController.txtName.getText();
            java.sql.Date startDate = new java.sql.Date(new Date().getTime());
            int duration = 0;
            String status = "In progress";
            
            try{
                Connection con = DBUtil.connection();
                PreparedStatement ps = con.prepareStatement("INSERT INTO Project(NameProject,StartDateProject,EndDateProject,DurationProject,StatusProject) VALUES (?,?,?,?,?);");
                ps.setString(1, name);
                ps.setDate(2, startDate);
                ps.setDate(3, null);
                ps.setInt(4, duration);
                ps.setString(5,status);
                int submited = ps.executeUpdate();
                
                if(submited==0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initStyle(StageStyle.UNDECORATED);
                    alert.setHeaderText("Line 250 : SQLException");
                    alert.setContentText("The value was not submited.");
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                    alert.showAndWait();
                }else{
                    updateComboBox();
                    updateTableViewProject();
                } 
            }catch(SQLException e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initStyle(StageStyle.UNDECORATED);
                alert.setHeaderText("Line 259 : SQLException");
                alert.setContentText(e.getMessage());
                alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
                alert.showAndWait();
            }
        }
    }
    
    @FXML
    private TableColumn<Project , String> columnName;
    @FXML
    private TableColumn<Project , String> columnStartDate;
    @FXML
    private TableColumn<Project , String> columnEndDate;
    @FXML
    private TableColumn<Project , Integer> columnTime;
    @FXML
    private TableColumn<Project , String> columnStatus;
    
    public void updateTableViewProject(){
        tvProject.getItems().clear();
        for(Project pTmp : projectList){
            tvProject.getItems().add(pTmp);
        }
        tvProject.refresh();
    }
    
    @FXML
    private void clickedProject(MouseEvent event) {
        p = tvProject.getSelectionModel().selectedItemProperty().get();
        //Load the fxml and create a new popup dialog
        FXMLLoader fxmlloader = new FXMLLoader();
        fxmlloader.setLocation(getClass().getResource("ProjectSelectionFXML.fxml"));
        DialogPane projectDP = null;
        try {
            projectDP = fxmlloader.load();
            //Get the student controller associated with the view 
            ProjectSelectionFXMLController projectController = fxmlloader.getController();

            //Show the dialog pane
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(projectDP);
            dialog.initStyle(StageStyle.UNDECORATED);
            Optional<ButtonType> clickedButton = dialog.showAndWait();

            //When the ok button is pressed
            if(clickedButton.get() == ButtonType.CLOSE || clickedButton.get() == ButtonType.CANCEL){
                updateComboBox();
                updateTableViewProject();
            }
        } catch (IOException ex) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("Empty row");
            alert.setContentText("You need to select a project.");
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
    }
    //Sessions
    
    private ArrayList<Session> sessionList = new ArrayList<>();
    
    @FXML
    private TabPane tabSession;
    
    private void updateSession(){
        sessionList.clear();
        try{
            Connection con = DBUtil.connection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Session;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                sessionList.add(
                    new Session(
                        rs.getInt("IdSession"),
                        rs.getDate("DateSession"),
                        rs.getInt("DurationSession"),
                        rs.getInt("PointSession"),
                        rs.getInt("IdProject")
                    )
                );
            }
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setHeaderText("updateSession : SQLException");
            alert.setContentText(e.getMessage());
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
    }
    
    //Week 
    
    @FXML
    private Tab tabWeek;
    @FXML
    private TableView<Week> tvSessionWeek;
    @FXML
    private TableColumn<Week , String> columnDateW;
    @FXML
    private TableColumn<Week , Integer> columnDurationW;
    @FXML
    private TableColumn<Week , Integer> columnAvgDurationW;
    @FXML
    private TableColumn<Week , Integer> columnPointW;
    @FXML
    private TableColumn<Week , Integer> columnNbSessionW;
    @FXML
    private TableColumn<Week , String> columnMainProjectW;
    @FXML
    private TableColumn<Week , String> columnGradeW;
    
    private void updateTVWeek(){
        
        ArrayList<Week> weekList = new ArrayList<>();
        //get the weekList
        Date dateTmp = new Date();
        ArrayList<Session> weeks = new ArrayList<>();
        for(int i=2022 ; i<=(dateTmp.getYear()+1900) ; i++){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR , 2022+i);
            int weekInYear = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
            
            for(int j=1 ; j<=weekInYear ; j++){
                weeks.clear();
                for(Session s : sessionList){
                    if(s.getYearSession()== i-1900 && s.getWeekSession()==j){
                        weeks.add(s);
                    }
                }
                try{
                    weekList.add(new Week(weeks));
                }catch(Exception e){
                    
                }
            }
        }
        
        //Put the week list on the table view
        
        tvSessionWeek.getItems().clear();
        for(Week wTmp : weekList){
            tvSessionWeek.getItems().add(wTmp);
        }
        tvSessionWeek.refresh();
    }
    
    @FXML
    private LineChart<String , Integer> lcWeek;
    
    private String dayConverter(int x){
        String day;
        switch(x){
            case (1) : day = "Sunday";
                    break;
            case (2) : day = "Monday";
                    break;
            case (3) : day = "Thuesday";
                    break;
            case (4) : day = "Wednesday";
                    break;
            case (5) : day = "Thursday";
                    break;
            case (6) : day = "Friday";
                    break;
            default : day = "Saturday";
        }
        return day;
    }
    
    @FXML
    private void rowClickedWeek(MouseEvent event) {
        lcWeek.getData().clear();
        try{
            Week weekTmp = tvSessionWeek.getSelectionModel().selectedItemProperty().get();
            ArrayList<Session> sessionWListTmp = weekTmp.getSessionWList();
            int[] dayDuration = new int[7];

            //get the session in the serie
            XYChart.Series seriesW = new XYChart.Series();
            for(Session sTmp : sessionWListTmp){
                for(int i=0 ; i<7 ; i++){
                   if(sTmp.getDayWeek()==(i+1)){
                    dayDuration[i] += sTmp.getDurationSession();
                    } 
                }
            }

            for(int i = 0 ; i<7 ; i++){
                seriesW.getData().add(new XYChart.Data(dayConverter((i+1)),dayDuration[i]));
            }

            //get the serie in the line chart
            lcWeek.getData().add(seriesW);
        }catch(NullPointerException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("Empty row");
            alert.setContentText("You need to select a week.");
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
    }
    
    //Month
    
    @FXML
    private Tab tabMonth;
    @FXML
    private TableView<Month> tvSessionMonth;
    @FXML
    private TableColumn<Month , String > columnDateM;
    @FXML
    private TableColumn<Month , Integer> columnDurationM;
    @FXML
    private TableColumn<Month , Integer> columnAvgDurationM;
    @FXML
    private TableColumn<Month , Integer> columnPointM;
    @FXML
    private TableColumn<Month , Integer> columnNbSessionM;
    @FXML
    private TableColumn<Month , String> columnMainProjectM;
    @FXML
    private TableColumn<Month , String> columnGradeM;
    
    private void updateTVMonth(){
        
        ArrayList<Month> monthList = new ArrayList<>();
        //get the monthList
        Date dateTmp = new Date();
        ArrayList<Session> months = new ArrayList<>();
        for(int i=2022 ; i<=(dateTmp.getYear()+1900) ; i++){
            for(int j=1 ; j<=12 ; j++){
                months.clear();
                for(Session s : sessionList){
                    if(s.getYearSession()== i-1900 && s.getMonthSession()==j){
                        months.add(s);
                    }
                }
                try{
                    monthList.add(new Month(months));
                }catch(Exception e){
                    
                }
            }
        }
        
        //Put the month list on the table view
        
        tvSessionMonth.getItems().clear();
        for(Month mTmp : monthList){
            tvSessionMonth.getItems().add(mTmp);
        }
        tvSessionMonth.refresh();
    }
    
    @FXML
    private LineChart<String , Integer> lcMonth;
    private Calendar cal = Calendar.getInstance();
    
    @FXML
    private void rowClickedMonth(MouseEvent event) {
        lcMonth.getData().clear();
        cal.setTime(new Date());
        int startDay;
        try{
            Month monthTmp = tvSessionMonth.getSelectionModel().selectedItemProperty().get();
            ArrayList<Session> sessionMListTmp = monthTmp.getSessionMList();
            int[] dayDuration = new int[monthTmp.getNbDayMonth()];
            for(Integer i : dayDuration){
                dayDuration[i]=0;
            }
            //get the session in the serie
            XYChart.Series seriesM = new XYChart.Series();
            for(Session sTmp : sessionMListTmp){
                if(monthTmp.getMonth()==6){
                    startDay = 25;//cal.get(Calendar.DAY_OF_MONTH);
                }else{
                    startDay = 0;
                }
                for(int i=startDay ; i<dayDuration.length ; i++){
                    if(sTmp.getDaySession()==(i+1)){
                        dayDuration[i] += sTmp.getDurationSession();
                    } 
                }
            }

            for(int i = 0 ; i<dayDuration.length ; i++){
                seriesM.getData().add(new XYChart.Data((""+(i+1)),dayDuration[i]));
            }

            //get the serie in the line chart
            lcMonth.getData().add(seriesM);
        }catch(NullPointerException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("Empty row");
            alert.setContentText("You need to select a month.");
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
    }
    
    //Year
    
    @FXML
    private Tab tabYear;
    @FXML
    private TableView<Year> tvSessionYear;
    @FXML
    private TableColumn<Year , String> columnDateY;
    @FXML
    private TableColumn<Year , Integer> columnDurationY;
    @FXML
    private TableColumn<Year , Integer> columnAvgDurationY;
    @FXML
    private TableColumn<Year , Integer> columnPointY;
    @FXML
    private TableColumn<Year , Integer> columnNbSessionY;
    @FXML
    private TableColumn<Year , String> columnMainProjectY;
    @FXML
    private TableColumn<Year , String> columnGradeY;

    private void updateTVYear(){
        
        ArrayList<Year> yearList = new ArrayList<>();
        //get the yearList
        Date dateTmp = new Date();
        ArrayList<Session> years = new ArrayList<>();
        for(int i=2022 ; i<=(dateTmp.getYear()+1900) ; i++){
            years.clear();
            for(Session s : sessionList){
                if(s.getYearSession()== i-1900){
                    years.add(s);
                }
            }
            try{
                yearList.add(new Year(years));
            }catch(Exception e){

            }
        }
        
        //Put the year list on the table view
        
        tvSessionYear.getItems().clear();
        for(Year yTmp : yearList){
            tvSessionYear.getItems().add(yTmp);
        }
        tvSessionYear.refresh();
    }
    
    @FXML
    private LineChart<String , Integer> lcYear;
    
    private String monthConverter(int x){
        String month;
        switch(x){
            case (0) : month = "January";
                    break;
            case (1) : month = "February";
                    break;
            case (2) : month = "March";
                    break;
            case (3) : month = "April";
                    break;
            case (4) : month = "May";
                    break;
            case (5) : month = "June";
                    break;
            case (6) : month = "July";
                    break;
            case (7) : month = "August";
                    break;
            case (8) : month = "September";
                    break;
            case (9) : month = "October";
                    break;
            case (10) : month = "November";
                    break;
            default : month = "December";
        }
        return month;
    }
    
    @FXML
    private void rowClickedYear(MouseEvent event) {
        lcYear.getData().clear();
        try{
            Year yearTmp = tvSessionYear.getSelectionModel().selectedItemProperty().get();
            ArrayList<Session> sessionYListTmp = yearTmp.getSessionYList();
            int[] dayDuration = new int[12];

            //get the session in the serie
            XYChart.Series seriesY = new XYChart.Series();
            for(Session sTmp : sessionYListTmp){
                for(int i=0 ; i<dayDuration.length ; i++){
                   if(sTmp.getMonthSession()==i){
                    dayDuration[i] += sTmp.getDurationSession();
                    } 
                }
            }

            for(int i = 0 ; i<dayDuration.length ; i++){
                seriesY.getData().add(new XYChart.Data(monthConverter(i),dayDuration[i]));
            }

            //get the serie in the line chart
            lcYear.getData().add(seriesY);
        }catch(NullPointerException e){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("Empty row");
            alert.setContentText("You need to select a year.");
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
            alert.showAndWait();
        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Clock
        
        clock = new Clock();
        clock.valueProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
               lblClock.setText(t1);
            } 
        });
        th1 = new Thread(clock);
        th1.setDaemon(true);
        th1.start();
        
        //Timer
        
        lblTimer.setText("00 : 00 : 00");
        btnPause.setDisable(true);
        btnReset.setDisable(true);
        
        //Project
        
        updateComboBox();
        columnName.setCellValueFactory(new PropertyValueFactory<>("nameProject"));
        columnStartDate.setCellValueFactory(new PropertyValueFactory<>("startDateProject"));
        columnEndDate.setCellValueFactory(new PropertyValueFactory<>("endDateProject"));
        columnTime.setCellValueFactory(new PropertyValueFactory<>("durationProject"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("statusProject"));
        updateTableViewProject();
        
        //Sessions 
        
        updateSession();
        
        
        //Week 
        
        columnDateW.setCellValueFactory(new PropertyValueFactory<>("dateW"));
        columnDurationW.setCellValueFactory(new PropertyValueFactory<>("totalDurationW"));
        columnAvgDurationW.setCellValueFactory(new PropertyValueFactory<>("avgDurationW"));
        columnPointW.setCellValueFactory(new PropertyValueFactory<>("totalPointW"));
        columnNbSessionW.setCellValueFactory(new PropertyValueFactory<>("nbSessionsW"));
        columnMainProjectW.setCellValueFactory(new PropertyValueFactory<>("mainProjectW"));
        columnGradeW.setCellValueFactory(new PropertyValueFactory<>("GradeW"));
        updateTVWeek();
        
        //Month
        
        columnDateM.setCellValueFactory(new PropertyValueFactory<>("dateM"));
        columnDurationM.setCellValueFactory(new PropertyValueFactory<>("totalDurationM"));
        columnAvgDurationM.setCellValueFactory(new PropertyValueFactory<>("avgDurationM"));
        columnPointM.setCellValueFactory(new PropertyValueFactory<>("totalPointM"));
        columnNbSessionM.setCellValueFactory(new PropertyValueFactory<>("nbSessionsM"));
        columnMainProjectM.setCellValueFactory(new PropertyValueFactory<>("mainProjectM"));
        columnGradeM.setCellValueFactory(new PropertyValueFactory<>("GradeM"));
        updateTVMonth();
        
        //Year
        
        columnDateY.setCellValueFactory(new PropertyValueFactory<>("dateY"));
        columnDurationY.setCellValueFactory(new PropertyValueFactory<>("totalDurationY"));
        columnAvgDurationY.setCellValueFactory(new PropertyValueFactory<>("avgDurationY"));
        columnPointY.setCellValueFactory(new PropertyValueFactory<>("totalPointY"));
        columnNbSessionY.setCellValueFactory(new PropertyValueFactory<>("nbSessionsY"));
        columnMainProjectY.setCellValueFactory(new PropertyValueFactory<>("mainProjectY"));
        columnGradeY.setCellValueFactory(new PropertyValueFactory<>("GradeY"));
        updateTVYear();
        
    }    

    @FXML
    private Button btnOff;

    @FXML
    private void closeApp(ActionEvent event) {
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("Done ?");
        alert.setContentText("Do you really want to close the app ?");
        alert.initStyle(StageStyle.UNDECORATED);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("alert.css").toString());
        Optional<ButtonType> clickedButton = alert.showAndWait();
        
        if(clickedButton.get() == ButtonType.OK){
            yepClock = false;
            System.exit(0);
        }
    }  

    @FXML
    private void OpenToDo(ActionEvent event) throws IOException {
        //Load the fxml and create a new popup dialog
        FXMLLoader fxmlloader = new FXMLLoader();
        fxmlloader.setLocation(getClass().getResource("ToDoFXML.fxml"));
        DialogPane projectDP = null;
        projectDP = fxmlloader.load();
        

        //Show the dialog pane
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(projectDP);
        dialog.initStyle(StageStyle.UNDECORATED);
        Optional<ButtonType> clickedButton = dialog.showAndWait();

        //When the ok button is pressed
        if(clickedButton.get() == ButtonType.CLOSE){
            updateComboBox();
            updateTableViewProject();
        }
    }
}
