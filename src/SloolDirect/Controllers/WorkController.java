package SloolDirect.Controllers;

import SloolDirect.DataClasses.DesignEnum;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.*;

import SloolDirect.DataClasses.WorkData;

public final class WorkController implements Initializable {
    private final Timer timer = new Timer(); //for stopwatch thread
    private boolean isStopWatchActive = false;
    private int[] stopwatchTime = new int[3]; //Hour minutes seconds

    private TerminalClass terminalClass = new TerminalClass();
    private WorkData workData = new WorkData();

    //Display time for stopwatch
    @FXML
    Label displayStopWatch;
    @FXML
    ListView toDoListView;
    @FXML
    TextField inputToDo, terminalInput;
    @FXML
    TextArea terminalOutput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Read data and outputs to show the user
        readDataInit();

        startThreads();
        actionStopwatchReset(); //Sets displayStopwatch to 0:00:00
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    //Read data for toDoList
    private void readDataInit() {
        try {
            toDoListView.getItems().setAll(workData.getToDoList());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("---ERROR IN READING TODO DATA---");
        }
    }

    //Starts the stopwatch thread
    private void startThreads() {
        Thread thread = new Thread(stopwatch);
        thread.run();
        thread.setDaemon(true);
    }

    //Starts and stops stopwatch
    @FXML
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

    //Makes time 0:00:00
    @FXML
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

    //
    //End of stopwatch commands
    //
    //Start of toDos commands
    //

    //Removes an entry from the toDoListView
    @FXML
    private void removeToDoEntry() {
        //Checks if something was actually selected
        if (!toDoListView.getSelectionModel().isEmpty()) {
            toDoListView.getItems().removeAll(toDoListView.getSelectionModel().getSelectedItems());
            toDoListView.getSelectionModel().clearSelection();
            updateToDoList();
        }
    }

    //Adds an entry to the toDoListView
    @FXML
    private void addToDoEntry(KeyEvent keyPressed) {
        if (keyPressed.getCode() == KeyCode.ENTER && !inputToDo.getText().isEmpty()) {
            String addedEvent = String.format("- %s", inputToDo.getText());

            //Checks if what the user wants to add was already added
            if (!toDoListView.getItems().contains(addedEvent)) {
                toDoListView.getItems().add(addedEvent);
            }

            //Clears what was input
            inputToDo.setText("");

            updateToDoList();
        }
    }

    //Moves item upwards
    @FXML
    private void moveUp(){
        //Makes sure that only one item is being move up
        if(!(toDoListView.getSelectionModel().getSelectedItems().size() > 1) &&
                toDoListView.getSelectionModel().getSelectedIndex() != 0){
            //Have to make ObservableList a observableArrayList to allow it to
            //be passed into a listView (which only accepts this)
            ObservableList<String> list = FXCollections.observableArrayList(toDoListView.getItems());
            int movingIndex = toDoListView.getSelectionModel().getSelectedIndex();
            String selectedItem = toDoListView.getSelectionModel().getSelectedItem().toString();

            //Copies the index - 1 String and move it to one position down then
            //moves the selected item up
            String upPosition = list.get(movingIndex - 1);

            list.set(movingIndex, upPosition);
            list.set(movingIndex - 1, selectedItem);

            //updates
            toDoListView.getItems().setAll(list);
            updateToDoList();

            //Selects the item previously selected before the moving
            //Has to be after updates because you want the change to occur already
            toDoListView.getSelectionModel().select(movingIndex - 1);

        }
    }

    //Tells program to write data and gives the required info to write data
    private void updateToDoList() {
        workData.setNecessaryToWrite(toDoListView.getItems());
    }

    //Start of terminal commands
    //

    @FXML
    private void enterTerminal(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER && !terminalInput.getText().isEmpty()) {

            //clear
            if (terminalInput.getText().equals("clear")) {
                terminalOutput.setText("");
                terminalInput.setText("");
                return;
            }

            //Format by previous lines, input lines, processed lines
            //If terminalOutput is empty, don't skip a line
            String outputText;
            if (terminalOutput.getText().isEmpty()){
                outputText = String.format("%s \n %s", terminalInput.getText(),
                        terminalClass.processTerminal(terminalInput.getText()));
            }else{
                outputText = String.format( "%s %s \n %s",
                        terminalOutput.getText(),
                        terminalInput.getText(),
                        terminalClass.processTerminal(terminalInput.getText()));
            }

            //Updates the view for user
            terminalOutput.setText(outputText);

            //Clears input textField
            terminalInput.setText("");
        }
    }

    //Processes terminal commands
    class TerminalClass {
        String inputs;
        double[] doubleInputs;

        public String processTerminal(String command) {
            String outputMessage = "";
            String prefix = command.split(" ")[0];

            inputs = command.replace(prefix + " ", "");
            doubleInputs = toIntegers(inputs.split("[,\\s]+"));

            switch (prefix) {
                //Addition
                case "a":
                case "add":
                    outputMessage = add();
                    break;
                //Subtraction
                case "s":
                case "minus":
                case "subtract":
                    outputMessage = subtract();
                    break;
                //Multiplication
                case "m":
                case "multiply":
                    outputMessage = multiplication();
                    break;
                //Division
                case "d":
                case "divide":
                    outputMessage = division();
                    break;
                //Exponent
                case "exp":
                    outputMessage = exponent();
                    break;
                //Average
                case "average":
                case "avg":
                    outputMessage = average();
                    break;
                //Median
                case "median":
                    outputMessage = median();
                    break;
                case "process":
                    outputMessage = String.format("%s \n %s", average(), median());
                    break;
                case "coin":
                    outputMessage = coinFiftyFifty();
                    break;
                default:
                    outputMessage = "Could not register the command";
            }
            return outputMessage;
        }

        private String add() {
            if (doubleInputs == null) {
                return "Inputs are not all numbers";
            } else {
                double sum = 0;
                for (int i = 0; i < doubleInputs.length; i++) {
                    sum += doubleInputs[i];
                }

                //Sum: 14
                return String.format("Sum: %f", sum);
            }
        }

        private String subtract() {
            if (doubleInputs == null) {
                return "Inputs are not all numbers";
            } else {
                double subtracted = doubleInputs[0];
                for (int i = 1; i < doubleInputs.length; i++) {
                    subtracted -= doubleInputs[i];
                }

                //Difference: -14
                return String.format("Difference: %f", subtracted);
            }
        }

        private String multiplication() {
            if (doubleInputs == null) {
                return "Inputs are not all numbers";
            } else {
                double multiplied = doubleInputs[0];
                for (int i = 1; i < doubleInputs.length; i++) {
                    multiplied -= doubleInputs[i];
                }

                //Product: 144
                return String.format("Product: %f", multiplied);
            }
        }

        private String division() {
            if (doubleInputs == null) {
                return "Inputs are not all numbers";
            } else {
                double divided = doubleInputs[0];
                for (int i = 1; i < doubleInputs.length; i++) {
                    divided /= doubleInputs[i];
                }

                //Dividend: 14.4
                return String.format("Dividend: %f", divided);
            }
        }

        private String exponent() {
            if (doubleInputs == null) {
                return "Inputs are not all numbers";
            } else {
                double exponentNumber = Math.pow(doubleInputs[0], doubleInputs[1]);

                //Raised: 32
                return String.format("Raised: %f", exponentNumber);
            }
        }

        private String average() {
            if (doubleInputs == null) {
                return "Inputs are not all numbers";
            } else {
                double sum = toWorkingDouble(add());
                double averageDouble = sum / doubleInputs.length;

                //Average: 3
                return String.format("Average: %f", averageDouble);
            }
        }

        private String median() {
            if (doubleInputs == null) {
                return "Inputs are not all numbers";
            } else {
                Arrays.sort(doubleInputs);
                double medianNumber;
                if (doubleInputs.length % 2 == 0) {
                    //even number, must get sum of two middle
                    int positionFirst = doubleInputs.length / 2;
                    medianNumber = (doubleInputs[positionFirst] + doubleInputs[positionFirst - 1]) / 2;
                } else {
                    medianNumber = doubleInputs[(doubleInputs.length - 1) / 2];
                }
                return String.format("Median: %f", medianNumber);
            }
        }

        private String coinFiftyFifty(){
            Random randomGenerator = new Random();
            return randomGenerator.nextBoolean()+"";
        }

        //Allows for calling other methods by removing first part
        private double toWorkingDouble(String convert) {
            return Double.parseDouble(convert.split(" ")[1]);
        }

        //Converts string[] to double[] with try catch
        private double[] toIntegers(String[] stringNumberArray) {
            //Makes sure that all string values are actually numbers
            try {
                double[] doubleArray = new double[stringNumberArray.length];
                for (int i = 0; i < doubleArray.length; i++) {
                    doubleArray[i] = Double.parseDouble(stringNumberArray[i]);
                }
                return doubleArray;
            } catch (Exception e) {
                //Cannot turn numbers
                return null;
            }
        }
    }

}
