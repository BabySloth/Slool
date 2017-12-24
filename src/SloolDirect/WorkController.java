package SloolDirect;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public final class WorkController implements Initializable {
    private final Timer timer = new Timer();
    private boolean isStopWatchActive = false;
    private int[] stopwatchTime = new int[3]; //Hour minutes seconds


    @FXML //Display time for stopwatch
            Label displayStopWatch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startThreads();
        actionStopwatchReset(); //Sets displayStopwatch to 0:00:00
    }

    private void startThreads() {
        Thread thread = new Thread(stopwatch);
        thread.run();
        thread.setDaemon(true);
    }

    @FXML //Starts and stops stopwatch
    private void actionStopwatch(Event source) {
        Button buttonClicked = (Button) source.getSource();
        if (isStopWatchActive) { //Stops stopwatch, turns green
            buttonClicked.setTextFill(DesignEnum.GREEN.getColor());
            buttonClicked.setText("Start");
        } else { //Starts stopwatch, turns red
            buttonClicked.setTextFill(DesignEnum.RED.getColor());
            buttonClicked.setText("Stop");
        }
        isStopWatchActive = !isStopWatchActive;
    }

    @FXML //Makes time 0:00:00
    private void actionStopwatchReset() {
        for (int i = 0; i < stopwatchTime.length; i++) {
            stopwatchTime[i] = 0;
        }
        updateStopwatch();
    }

    //Thread runs every 1 second for stopwatch
    private Task stopwatch = new Task<Void>() {
        @Override
        public Void call() {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (isStopWatchActive) {
                            stopwatchTime[2]++; //Add seconds
                            if (stopwatchTime[2] == 60) { //60s = 1m
                                stopwatchTime[2] = 0;
                                stopwatchTime[1]++;
                            }
                            if (stopwatchTime[1] == 60) { //60m = 1h
                                stopwatchTime[1] = 0;
                                stopwatchTime[0]++;
                            }
                            try { //Allows for thread to be run if fxml is not shown
                                updateStopwatch();
                            } catch (Exception e) {
                                //Means the tab is not currently open
                            }
                        }
                    });
                }
            }, 1000, 1000);
            return null;
        }
    };

    //displayStopWatch text change
    private void updateStopwatch() {
        String format = "%d:";
        if (stopwatchTime[1] < 10) { //Formats so it isn't 0:00:010
            format += "0%d:";
        } else {
            format += "%d:";
        }
        if (stopwatchTime[2] < 10) { //Formats so it isn't 0:010:00
            format += "0%d";
        } else {
            format += "%d";
        }
        displayStopWatch.setText(String.format(format, stopwatchTime[0],
                stopwatchTime[1], stopwatchTime[2]));
    }

    //TODO: Create a enum separate class for all to read
}
