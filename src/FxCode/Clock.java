package FxCode;

import static FxCode.DashBoardFXMLController.yepClock;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.concurrent.Task;

public class Clock extends Task<String>{

    private String timeTmp;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH : mm : ss");
    @Override
    protected String call() throws Exception {
        while(yepClock){
            Thread.sleep(1000);
            timeTmp = sdf.format(new Date());
            updateValue(timeTmp);
        }
        return timeTmp;
    }
    
}
