package SloolDirect;

import javafx.application.Platform;
import javafx.concurrent.Task;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeClass {
    private final SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EEEE");
    private final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMMM");
    private final SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("YY");
    protected int hour = 0;
    protected int hourOfDay = 0;
    protected String minute = "00";
    protected int day = 1;
    protected String am_pm = "0"; //AM = 0 PM = 1
    protected int monthNumber = 1;
    protected int yearNum = 2017;
    protected String year = "17";
    protected String dayOfWeek = "M";
    protected String month = "J";

    protected TimeClass(){
        setDesc();
        Thread thread = new Thread(timeLoop);
        thread.setDaemon(true);
        thread.start();
    }

    private Timer timer = new Timer();

    private Task timeLoop = new Task<Void>(){
        @Override
        public Void call() {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                       setDesc();
                    });
                }
            }, 4*1000, 4*1000);
            return null;
        }
    };

    private void setDesc(){
        Calendar now = Calendar.getInstance();
        hour = now.get(Calendar.HOUR);
        hourOfDay = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE)+"";
        day = now.get(Calendar.DATE);
        am_pm = now.get(Calendar.AM_PM)+"";
        monthNumber = now.get(Calendar.MONTH)+1;
        yearNum = now.get(Calendar.YEAR);
        Date date = new Date();
        dayOfWeek = simpleDateFormat1.format(date);
        month = simpleDateFormat2.format(date);
        year = simpleDateFormat3.format(date);
        fixer();
    }

    private void fixer(){
        minute = Integer.parseInt(minute) < 10 ? "0"+minute : minute;
        //AM = 0 PM = 1
        am_pm = am_pm.equals("0") ? "AM" : "PM";
        hour = hour==0 ? 12 : hour;
    }
}
