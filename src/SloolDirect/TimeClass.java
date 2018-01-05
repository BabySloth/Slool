package SloolDirect;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeClass {
    private static final SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EEEE");
    private static final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMMM");
    public static int hour = 0;
    public static int hourOfDay = 0;
    public static String minute = "00";
    public static int day = 1;
    public static String am_pm = "0"; //AM = 0 PM = 1
    public static int monthNumber = 1;
    public static int yearNum = 2017;
    public static String year = "17";
    public static String dayOfWeek = "M";
    public static String month = "J";

    public TimeClass() {
        setDesc();
        Thread thread = new Thread(timeLoop);
        thread.setDaemon(true);
        thread.start();
    }

    private static Timer timer = new Timer();

    private static Task timeLoop = new Task<Void>() {
        @Override
        public Void call() {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        setDesc();
                    });
                }
            }, 4 * 1000, 4 * 1000);
            return null;
        }
    };

    private static void setDesc() {
        Calendar now = Calendar.getInstance();
        hour = now.get(Calendar.HOUR);
        hourOfDay = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE) + "";
        day = now.get(Calendar.DATE);
        am_pm = now.get(Calendar.AM_PM) + "";
        monthNumber = now.get(Calendar.MONTH) + 1;
        yearNum = now.get(Calendar.YEAR);
        //Don't use date class because it uses year of
        //the week. Sunday 31st of December 2017 would
        //read 2018
        year = String.valueOf(yearNum).substring(2, 4);

        Date date = new Date();
        dayOfWeek = simpleDateFormat1.format(date);
        month = simpleDateFormat2.format(date);
        //Fixes minute so it would have the 0 in front of single digit
        //Adds am and pm
        fixer();
    }

    private static void fixer() {
        minute = Integer.parseInt(minute) < 10 ? "0" + minute : minute;
        //AM = 0 PM = 1
        am_pm = am_pm.equals("0") ? "AM" : "PM";
        hour = hour == 0 ? 12 : hour;
    }
}
