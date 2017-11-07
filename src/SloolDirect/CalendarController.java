package SloolDirect;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.lang.reflect.Array;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

public class CalendarController implements Initializable{
    final private String[] monthList= {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private ToggleGroup addEventToggleGroup = new ToggleGroup();
    private ToggleGroup monthToggleGroup = new ToggleGroup();

    private ArrayList<ToggleButton> arrayMonthToggle = new ArrayList<>(12);
    private ArrayList<customVBox> calendarVBox = new ArrayList<>(42);
    private ArrayList<textExtends> arrayTextExtends = new ArrayList<>();

    private YearMonth yearMonthNow = YearMonth.now();
    private LocalDate localDateNow = LocalDate.now();
    private YearMonth yearMonthToday = YearMonth.now();
    private LocalDate localDateEventShow, localDateEventShow2;

    private String[] remove = new String[3];

    volatile HashMap<String, String> eventLong = new HashMap<>();
    volatile HashMap<String, String> eventShort = new HashMap<>();

    @FXML ToggleButton toggleButtonSchool, toggleButtonEntertainment, toggleButtonPersonal;
    @FXML Button monthIncrease;
    @FXML TextField dateChosed, removeThis, inputEvent;
    @FXML HBox monthHolder;
    @FXML VBox eventDisplayHolder;
    @FXML Text yearDisplay, bottomDayOfWeek, bottomDay, bottomSuffix, bottomYear, showingEventText;
    @FXML GridPane daysHolder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventShort.put("289,2017","S School starts,Egg throwing");
        SetUp setUpMethod = new SetUp();
        setUpMethod.startUp();
        updateView();
    }
    @FXML void changeMonthPlus(){
        yearMonthNow = yearMonthNow.plusMonths(1);
        updateView();
    }
    @FXML void changeMonthMinus(){
        yearMonthNow = yearMonthNow.minusMonths(1);
        updateView();
    }
    @FXML void changeYearPlus(){
        yearMonthNow = yearMonthNow.plusYears(1);
        updateView();
    }
    @FXML void changeYearMinus(){
        yearMonthNow = yearMonthNow.minusYears(1);
        updateView();
    }

    private void updateView(){
        LocalDate showingDate = LocalDate.of(yearMonthNow.getYear(), yearMonthNow.getMonth(), 1);
        populateCalendar(showingDate);
        updateToggleMonths();
        updateYearDisplay();
    }//Change main calendar display -- Calls everything
    private void updateToggleMonths(){
        for (ToggleButton button : arrayMonthToggle) {
            if (monthToInt(button.getText()) == yearMonthNow.getMonthValue()){
                button.setSelected(true);
            }
            if(yearMonthNow.getYear() == yearMonthToday.getYear() &&
               yearMonthToday.getMonthValue() == monthToInt(button.getText())){
               button.setTextFill(designColor("highlight"));
            }else{
                button.setTextFill(designColor("regular"));
            }
        }
    }//Changes which monthToggleButton is selected after changeMonthPlus()
    private void updateYearDisplay(){
        yearDisplay.setText(yearMonthNow.getYear()+"");
        if(yearMonthNow.getYear() == yearMonthToday.getYear()){
            yearDisplay.setFill(designColor("highlight"));
        }else{
            yearDisplay.setFill(designColor("regular"));
        }
    } //Highlights and updates the Year display
    private void populateCalendar(LocalDate localDate){
        while(!localDate.getDayOfWeek().toString().equals("SUNDAY")){
            localDate = localDate.minusDays(1);
        }
        for(customVBox vbox : calendarVBox){
            if(!vbox.getChildren().isEmpty()){
                vbox.getChildren().clear();
            }
            Text text = new Text(localDate.getDayOfMonth()+" ");
            text.setFill(designColor("regular"));
            if(localDateNow.equals(localDate)){
                text.setText(text.getText()+"- Today");
                text.setFill(designColor("highlight"));
            }
            vbox.getChildren().add(text);
            vbox.addDate(localDate);

            localDate = localDate.plusDays(1);
        }
    } //Add the day number (e.g. 10 - Today) && puts day number in
    private void addEventToCalendar(){
        for(customVBox vbox : calendarVBox){

        }
    }//Add the events to calendar and updates the VBOX to hold the events displayed
    class customVBox extends VBox {
        private HashMap<String, String> events = new HashMap<>();
        private LocalDate date;

        customVBox(){
            this.setOnMouseClicked(event -> {
                if(!event.isShiftDown()) showingEventText.setText("Event Details: "+dateToText(date));

            });
        }

        private String dateToText(LocalDate localDate){
            return localDate.getMonthValue()+"/"+localDate.getDayOfMonth();
        }
        public void addEvent(String date, String event){
            events.put(date, event);
        }
        public void addDate(LocalDate date){
            this.date = date;
        }
        public HashMap getEvent(){
            return events;
        }
        public boolean isBetween(LocalDate dateAfter, LocalDate dateBefore){
            return date.isBefore(dateBefore.minusDays(1)) && date.isAfter(dateAfter.plusDays(1));
        }

    }

    class textExtends extends Text{
        private String event;
        private String date;
        private String extra;

        public textExtends(){
            this.setOnMouseClicked(e->{

            });
        }

        public void setEvent(String event){
            this.event = event;
            this.setText(event);
        }
        public void setLocalDate(String date){
            this.date = date;
        }
        public void setExtra(String extra){
            this.extra = extra;
        }
        public String getEvent(){
            return event;
        }
    }
    @FXML private void removeEvent(){

    }


    @FXML private void newEvent(KeyEvent keyEvent){
        if(keyEvent.getCode() == KeyCode.ENTER){
            if(inputEvent.getText().length() == 0){
                return;
            }else{
                String type = "";
                if(toggleButtonSchool.isSelected()){
                    type = "S";
                }else if(toggleButtonEntertainment.isSelected()){
                    type = "E";
                }else if(toggleButtonPersonal.isSelected()){
                    type = "P";
                }
                addEvent(inputEvent.getText(), type);
            }
        }else if(keyEvent.getCode() == KeyCode.ESCAPE){
            inputEvent.setText("");
        }
    }
    private void addEvent(String event, String type){

    }

    @FXML private void changeDate(Event event){
        LocalDate localDateToday = LocalDate.now();
        localDateEventShow = localDateToday;
        localDateEventShow2 = null;
        updateView();
    }


    private Color eventColor(char eventType){
        switch(eventType){
            case 'W':
                return new Color(0.0784, 0.5843, 0.8784, 1);
            case 'E':
                return new Color(0.3255, 0.8667, 0.4235, 1);
            case 'P':
                return new Color(1, 0.2431, 0.2549, 1);
            default:
                return new Color(0.8196, 0.8078, 0.8118, 1);
        }
    }
    private Color designColor(String designChoice){
        switch(designChoice.toLowerCase()){
            case "regular":
                return new Color(0.8196, 0.8078, 0.8118, 1);
            case "highlight":
                return new Color(0.9765, 0.8824, 0.3255, 1);
            default:
                codeError("designColor");
                return new Color(0.8196, 0, 0.0784, 1);
        }
    }
    private LocalDate toLocalDate(String date){
        int[] dateParts = toIntArray(date.split(","));
        return LocalDate.ofYearDay(dateParts[1],dateParts[0]);
    }
    private String[] toStringLocalDate(LocalDate date){
        String[] localDateString = {date.getDayOfYear()+"",date.getYear()+""};
        return localDateString;
    }
    private int[] toIntArray(String[] stringOfInt){
        int[] intArray = new int[stringOfInt.length];
        for (int i = 0; i < stringOfInt.length; i++) {
            intArray[i] = Integer.parseInt(stringOfInt[i]);
        }
        return intArray;
    }
    private int monthToInt(String month){
        switch (month){
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
            default:
                codeError("monthToInt");
                return 12;
        }
    }
    private boolean isDuringRange(LocalDate thisDate, LocalDate date1, LocalDate date2){
        return ( thisDate.isAfter(date1.minusDays(1)) && thisDate.isBefore(date2.plusDays(1)) );
    }

    private void codeError(String objectName){
        System.out.println("Error Choosing "+objectName+"()");
    }
    class SetUp{
        private void startUp(){
            addToToggleGroup(addEventToggleGroup, toggleButtonSchool, toggleButtonPersonal, toggleButtonPersonal);
            bottomDaysText();
            setMonthDisplay();
            setDaysHolder();
        }//Basic design setUps
        private void bottomDaysText(){
            String bottomText = localDateNow.getDayOfWeek().toString().substring(0,1) + localDateNow.getDayOfWeek().toString().toLowerCase().substring(1, localDateNow.getDayOfWeek().toString().length());
            bottomDayOfWeek.setText(bottomText);
            bottomDay.setText(localDateNow.getDayOfMonth()+"");
            bottomYear.setText(localDateNow.getYear()+"");

            if(bottomDay.getText().endsWith("1")){
                bottomSuffix.setText("st");
            }else if(bottomDay.getText().endsWith("2")){
                bottomSuffix.setText("nd");
            }else if(bottomDay.getText().endsWith("3")){
                bottomSuffix.setText("rd");
            }else{
                bottomSuffix.setText("th");
            }
        }//Sets bottom days text
        private void addToToggleGroup(ToggleGroup toggleGroup, ToggleButton... toggleButtons){
            for (ToggleButton toggleButton : toggleButtons) {
                toggleButton.setToggleGroup(toggleGroup);
            }
        }//Adds toggleGroup to toggleButton
        private void setMonthDisplay(){
            monthHolder.getChildren().remove(monthIncrease);
            for (int i = 0; i < 12; i++) {
                ToggleButton button = new ToggleButton();
                button.setTextFill(designColor("regular"));
                if(i+1 == yearMonthNow.getMonthValue()){
                    button.setSelected(true);
                    button.setTextFill(designColor("highlight"));
                }
                button.setText(monthList[i]);
                button.setMinSize(60, 30);
                button.setAlignment(Pos.CENTER);
                button.setOnAction(e->changeMonth(button));
                button.setToggleGroup(monthToggleGroup);
                monthHolder.getChildren().add(button);
                button.getStyleClass().add("monthToggle");
                arrayMonthToggle.add(button);
            }
            monthHolder.getChildren().add(monthIncrease);
        }//Adds month buttons
        private void setDaysHolder(){
            for(int i = 1; i < 7; i++){
                for (int a = 0; a < 7; a++) {
                    customVBox vBox = new customVBox();
                    vBox.setPrefSize(100, 90);
                    vBox.setPrefSize(130, 90);
                    daysHolder.add(vBox, a, i);
                    calendarVBox.add(vBox);
                }
            }
        }//Adds vBox to the gridPane
        private void changeMonth(ToggleButton button) {
            yearMonthNow = YearMonth.of(yearMonthNow.getYear(), monthToInt(button.getText()));
            updateView();
        }//Change month by setMonthDisplay()
    }
}

/**TODO
 * Read and write data
 * Add data
 * Take care of multiple adding and efficient reading
 * Deleted asd file
 */


