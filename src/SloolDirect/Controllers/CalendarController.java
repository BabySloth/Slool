package SloolDirect.Controllers;

import SloolDirect.DataClasses.DesignEnum;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

import SloolDirect.DataClasses.CalendarData;

public final class CalendarController implements Initializable {
    private final String[] MONTHS =
            {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private ToggleGroup addEventToggleGroup = new ToggleGroup();
    private ToggleGroup monthToggleGroup = new ToggleGroup();

    private ArrayList<ToggleButton> arrayMonthToggle = new ArrayList<>(12);
    private ArrayList<dateBox> calendarVBox = new ArrayList<>(42);

    private YearMonth yearMonthNow = YearMonth.now();
    private LocalDate localDateNow = LocalDate.now();
    private YearMonth yearMonthToday = YearMonth.now();
    private LocalDate localDateEventShow, localDateEventShow2;

    volatile HashMap<String, String> eventLong = new HashMap<>();
    volatile HashMap<String, String> eventShort = new HashMap<>();

    private CalendarData calendarData = new CalendarData();

    @FXML
    ToggleButton toggleButtonSchool, toggleButtonEntertainment, toggleButtonPersonal;
    @FXML
    Button monthIncrease;
    @FXML
    TextField dateChose, inputEvent;
    @FXML
    HBox monthHolder;
    @FXML
    VBox eventDisplayHolder;
    @FXML
    Text yearDisplay, bottomDayOfWeek, bottomDay, bottomSuffix, bottomYear, showingEventText, daysApart;
    @FXML
    GridPane daysHolder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getData();
        new SetUp().startUp();
        updateView();
    }

    //Gets data from /SloolDirect/DataClasses/CalendarData
    private void getData(){

        eventShort = calendarData.getHashMapShortEvents();
        eventLong = calendarData.getHashMapLongEvents();
    }

    @FXML
    void changeMonthPlus() {
        yearMonthNow = yearMonthNow.plusMonths(1);
        updateView();
    }

    @FXML
    void changeMonthMinus() {
        yearMonthNow = yearMonthNow.minusMonths(1);
        updateView();
    }

    @FXML
    void changeYearPlus() {
        yearMonthNow = yearMonthNow.plusYears(1);
        updateView();
    }

    @FXML
    void changeYearMinus() {
        yearMonthNow = yearMonthNow.minusYears(1);
        updateView();
    }

    private void updateView() {
        LocalDate showingDate = LocalDate.of(yearMonthNow.getYear(), yearMonthNow.getMonth(), 1);
        populateCalendar(showingDate);
        updateToggleMonths();
        updateYearDisplay();
        addEventToCalendar();
    }//Change main calendar display -- Calls everything

    private void updateToggleMonths() {
        for (ToggleButton button : arrayMonthToggle) {
            if (monthToInt(button.getText()) == yearMonthNow.getMonthValue()) {
                button.setSelected(true);
            }
            if (yearMonthNow.getYear() == yearMonthToday.getYear() &&
                    yearMonthToday.getMonthValue() == monthToInt(button.getText())) {
                button.setTextFill(designColor("highlight"));
            } else {
                button.setTextFill(designColor("regular"));
            }
        }
    }//Changes which monthToggleButton is selected after changeMonthPlus()

    private void updateYearDisplay() {
        yearDisplay.setText(yearMonthNow.getYear() + "");
        if (yearMonthNow.getYear() == yearMonthToday.getYear()) {
            yearDisplay.setFill(designColor("highlight"));
        } else {
            yearDisplay.setFill(designColor("regular"));
        }
    } //Highlights and updates the Year display

    private void populateCalendar(LocalDate localDate) {
        while (!localDate.getDayOfWeek().toString().equals("SUNDAY")) {
            localDate = localDate.minusDays(1);
        }
        for (dateBox vbox : calendarVBox) {
            if (!vbox.getChildren().isEmpty()) {
                vbox.getChildren().clear();
                vbox.clear();
            }
            Text text = new Text(localDate.getDayOfMonth() + " ");
            text.setFill(designColor("regular"));
            if (localDateNow.equals(localDate)) {
                text.setText(text.getText() + "- Today");
                text.setFill(designColor("highlight"));
            }
            vbox.getChildren().add(text);
            vbox.addDate(localDate);

            localDate = localDate.plusDays(1);
        }
    } //Add the day number (e.g. 10 - Today) && puts day number in

    //Add the events to the calendar
    private void addEventToCalendar() {
        ArrayList<String> longEventKeys = new ArrayList<>(1);
        String eventLongHolder = "";

        for (dateBox vbox : calendarVBox) {
            ArrayList<StackPane> eventPanes = new ArrayList<>(1);
            //Add long event first to look nice
            for (String key : eventLong.keySet()) {
                LocalDate[] rangeOfDates = toLocalDateRange(key);
                if (isDuringRange(vbox.date, rangeOfDates[0], rangeOfDates[1])) {
                    eventLongHolder = eventLong.get(key);
                    vbox.addLongEventKeys(key);
                    if (longEventKeys.contains(key)) {
                        //Name was already added
                        eventPanes.addAll(returnEventPanes(eventLongHolder, false));
                    } else {
                        //Name was not yet added
                        longEventKeys.add(key);
                        eventPanes.addAll(returnEventPanes(eventLongHolder, true));
                    }
                }
            }

            //Add one-day events
            for (String key : eventShort.keySet()) {
                if (vbox.date.isEqual(toLocalDate(key))) {
                    eventPanes.addAll(returnEventPanes(eventShort.get(key), true));
                }
            }
            for (StackPane stackPane : eventPanes) {
                vbox.getChildren().add(stackPane);
            }
            eventPanes.clear();
        }


    }

    private ArrayList<StackPane> returnEventPanes(String events, boolean nameIt) {
        ArrayList<StackPane> returnPanes = new ArrayList<>(1);
        String[] eventType = {"", "", ""};//Work, entertainment, personal
        String[] eventsDivided = events.split(";");

        for (int i = 0; i < eventsDivided.length; i++) {
            String eventName = eventsDivided[i];
            String eventShort = eventsDivided[i].substring(1);
            if (eventName.startsWith("W")) {
                eventType[0] += eventShort + ", ";
            } else if (eventName.startsWith("E")) {
                eventType[1] += eventShort + ", ";
            } else {//Starts with "P"
                eventType[2] += eventShort + ", ";
            }

        }

        for (int i = 0; i < 3; i++) {
            if (!eventType[i].isEmpty()) {
                Color color;

                StackPane stackPane = new StackPane();
                stackPane.setPrefSize(130, 20);
                Rectangle rectangle = new Rectangle(130, 20);
                rectangle.setFill(eventColor('z', i));

                if (nameIt) { //Create a text pane for it
                    Label label = new Label();
                    label.setTextFill(eventColor('n', 5));
                    label.setMaxWidth(130);
                    label.setText(eventType[i].substring(0, eventType[i].length() - 1));
                    stackPane.getChildren().setAll(rectangle, label);
                } else {
                    stackPane.getChildren().setAll(rectangle);
                }
                returnPanes.add(stackPane);
            }
        }

        return returnPanes;
    }

    private void updateEventDateShow() {
        String date1 = localDateEventShow.getMonthValue() + "/" + localDateEventShow.getDayOfMonth();
        if (localDateEventShow2 == null) {
            //Doing single dates
            dateChose.setText(date1);
        } else {
            dateChose.setText(date1 + " - " + localDateEventShow2.getMonthValue() + "/" + localDateEventShow2.getDayOfMonth());
        }
    }

    //Calendar boxes
    class dateBox extends VBox {
        private LocalDate date;
        private ArrayList<String> longEventKeys = new ArrayList<>(0);

        dateBox() {
            this.setOnMouseClicked(event -> {
                if (!event.isShiftDown()) {
                    localDateEventShow = date;
                    localDateEventShow2 = null;
                    showingEventText.setText("Event Details: " + dateToText(date));
                    eventDisplayHolder.getChildren().clear();

                    if (!longEventKeys.isEmpty()) {
                        for (String key : longEventKeys) {
                            String[] eventList = eventLong.get(key).split(";");
                            for (int i = 0; i < eventList.length; i++) {
                                textLists text = new textLists();
                                text.setLongDate(key);
                                text.setIsLongEvent(true);
                                text.setEvent(eventList[i]);
                                text.setFill(eventColor(eventList[i].charAt(0), 100));
                                eventDisplayHolder.getChildren().addAll(text);
                            }
                        }
                    }
                    String shortEvent = eventShort.get(date.getDayOfYear() + "," + date.getYear());
                    if (shortEvent != null) {
                        String[] eventArrays = shortEvent.split(";");
                        for (int b = 0; b < eventArrays.length; b++) {
                            textLists text = new textLists();
                            text.setEvent(eventArrays[b]);
                            text.setLocalDate(date);
                            text.setIsLongEvent(false);
                            text.setFill(eventColor(eventArrays[b].charAt(0), 100));
                            eventDisplayHolder.getChildren().addAll(text);
                        }
                    }
                } else if (date.isAfter(localDateEventShow)) {
                    localDateEventShow2 = date;
                }
                updateEventDateShow();
                updateDaysApart();
            });
        }

        private String dateToText(LocalDate localDate) {
            return localDate.getMonthValue() + "/" + localDate.getDayOfMonth();
        }

        public void addDate(LocalDate date) {
            this.date = date;
        }

        public void addLongEventKeys(String key) {
            this.longEventKeys.add(key);
        }

        public void clear() {
            longEventKeys.clear();
        }
    }

    class textLists extends Text {
        private String event;
        private String longDate;
        private LocalDate date;
        private boolean isLongEvent = false;

        public textLists() {
            this.setOnMouseClicked(e -> {
                this.setFill(eventColor(event.charAt(0), 100));
                if (e.isShortcutDown()) {
                    String original;
                    if (isLongEvent) {
                        original = eventLong.get(longDate);
                        eventLong.put(longDate, original.replace(event, ""));
                        if (eventLong.get(longDate).isEmpty()) {
                            eventLong.remove(longDate);
                        }
                    } else {
                        original = eventShort.get(toStringLocalDate(date));
                        eventShort.put(toStringLocalDate(date), original.replace(event + ";", ""));
                        if (eventShort.get(toStringLocalDate(date)).isEmpty()) {
                            eventShort.remove(toStringLocalDate(date));
                        }
                    }
                    calendarData.setNecessaryToWrite(eventShort, eventLong);
                    updateView();
                    eventDisplayHolder.getChildren().remove(this);
                }
            });
        }

        public void setEvent(String event) {
            this.event = event;
            this.setText(event.substring(1));
        }

        public void setLocalDate(LocalDate date) {
            this.date = date;
        }

        public void setIsLongEvent(boolean isLongEvent) {
            this.isLongEvent = isLongEvent;
        }

        public void setLongDate(String date) {
            this.longDate = date;
        }
    }

    private void updateDaysApart() {
        LocalDate today = LocalDate.now();
        if (localDateEventShow2 == null) {
            //Today to clicked date
            daysApart.setText(today.until(localDateEventShow, ChronoUnit.DAYS) + "");
        } else {
            daysApart.setText(localDateEventShow.until(localDateEventShow2, ChronoUnit.DAYS) + "");
        }
    }

    @FXML
    private void newEvent(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (inputEvent.getText().isEmpty()) {
                return;
            } else {
                char type = 'Z';
                if (toggleButtonSchool.isSelected()) {
                    type = 'W';
                } else if (toggleButtonEntertainment.isSelected()) {
                    type = 'E';
                } else if (toggleButtonPersonal.isSelected()) {
                    type = 'P';
                }
                addEvent(inputEvent.getText(), type);
            }
            inputEvent.setText("");
            calendarData.setNecessaryToWrite(eventShort, eventLong);
        } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
            inputEvent.setText("");
        }
    }

    private void addEvent(String event, char type) {
        if (localDateEventShow2 != null) {
            //This is an LongEvent type
            String longDateKey = toStringLocalDate(localDateEventShow) + "," + toStringLocalDate(localDateEventShow2);
            if (eventLong.get(longDateKey) != null) {
                eventLong.put(longDateKey, eventLong.get(longDateKey) + type + event + ";");
            } else {
                eventLong.put(longDateKey, type + event + ";");
            }
        } else {
            String shortDateKey = toStringLocalDate(localDateEventShow);
            if (eventShort.get(shortDateKey) != null) {
                eventShort.put(shortDateKey, eventShort.get(shortDateKey) + type + event + ";");
            } else {
                eventShort.put(shortDateKey, type + event + ";");
            }
        }

        updateView();
    }

    @FXML
    private void changeDate() {
        LocalDate localDateToday = LocalDate.now();
        localDateEventShow = localDateToday;
        localDateEventShow2 = null;
        updateView();
    }

    private Color eventColor(char eventType, int arraySpot) {
        if (eventType == 'W' || arraySpot == 0) {
            return DesignEnum.BLUE.getColor();
        } else if (eventType == 'E' || arraySpot == 1) {
            return DesignEnum.GREEN.getColor();
        } else if (eventType == 'P' || arraySpot == 2) {
            return DesignEnum.RED.getColor();
        } else {
            return DesignEnum.GREY.getColor();
        }
    }

    private Color designColor(String designChoice) {
        switch (designChoice.toLowerCase()) {
            case "regular":
                return DesignEnum.GREY.getColor();
            case "highlight":
                return DesignEnum.YELLOW.getColor();
            default:
                codeError("designColor");
                return DesignEnum.DARK_RED.getColor();
        }
    }

    private LocalDate toLocalDate(String date) {
        int[] dateParts = toIntArray(date.split(","));
        return LocalDate.ofYearDay(dateParts[1], dateParts[0]);
    }

    private LocalDate[] toLocalDateRange(String dates) {
        String[] dateArray = dates.split(",");
        LocalDate[] localDates = {toLocalDate(dateArray[0] + "," + dateArray[1]), toLocalDate(dateArray[2] + "," + dateArray[3])};
        return localDates;
    }

    private String toStringLocalDate(LocalDate date) {
        String localDateString = date.getDayOfYear() + "," + date.getYear();
        return localDateString;
    }

    private int[] toIntArray(String[] stringOfInt) {
        int[] intArray = new int[stringOfInt.length];
        for (int i = 0; i < stringOfInt.length; i++) {
            intArray[i] = Integer.parseInt(stringOfInt[i]);
        }
        return intArray;
    }

    private int monthToInt(String month) {
        switch (month) {
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

    private boolean isDuringRange(LocalDate thisDate, LocalDate date1, LocalDate date2) {
        return (thisDate.isAfter(date1.minusDays(1)) && thisDate.isBefore(date2.plusDays(1)));
    }

    private void codeError(String objectName) {
        System.out.println("Error Choosing " + objectName + "()");
    }

    class SetUp {
        private void startUp() {
            addToToggleGroup(addEventToggleGroup, toggleButtonSchool, toggleButtonEntertainment, toggleButtonPersonal);
            bottomDaysText();
            setMonthDisplay();
            setDaysHolder();
            setDefaults();
        }//Basic design setUps

        private void setDefaults() {
            localDateEventShow = LocalDate.now();
            dateChose.setText(localDateEventShow.getMonthValue() + "/" + localDateEventShow.getDayOfMonth());
        }

        private void bottomDaysText() {
            String bottomText = localDateNow.getDayOfWeek().toString().substring(0, 1) +
                    localDateNow.getDayOfWeek().toString().toLowerCase().substring(1,
                            localDateNow.getDayOfWeek().toString().length());
            bottomDayOfWeek.setText(bottomText);
            bottomDay.setText(localDateNow.getDayOfMonth() + "");
            bottomYear.setText(localDateNow.getYear() + "");

            if (bottomDay.getText().endsWith("1")) {
                bottomSuffix.setText("st");
            } else if (bottomDay.getText().endsWith("2")) {
                bottomSuffix.setText("nd");
            } else if (bottomDay.getText().endsWith("3")) {
                bottomSuffix.setText("rd");
            } else {
                bottomSuffix.setText("th");
            }
        }//Sets bottom days text

        private void addToToggleGroup(ToggleGroup toggleGroup, ToggleButton... toggleButtons) {
            for (ToggleButton toggleButton : toggleButtons) {
                toggleButton.setToggleGroup(toggleGroup);
            }
        }//Adds toggleGroup to toggleButton

        private void setMonthDisplay() {

            monthHolder.getChildren().remove(monthIncrease);
            for (int i = 0; i < 12; i++) {
                ToggleButton button = new ToggleButton();
                button.setTextFill(designColor("regular"));
                if (i + 1 == yearMonthNow.getMonthValue()) {
                    button.setSelected(true);
                    button.setTextFill(designColor("highlight"));
                }
                button.setText(MONTHS[i]);
                button.setMinSize(60, 30);
                button.setAlignment(Pos.CENTER);
                button.setOnAction(e -> changeMonth(button));
                button.setToggleGroup(monthToggleGroup);
                monthHolder.getChildren().add(button);
                button.getStyleClass().add("monthToggle");
                arrayMonthToggle.add(button);
            }
            monthHolder.getChildren().add(monthIncrease);
        }//Adds month buttons

        private void setDaysHolder() {
            for (int i = 1; i < 7; i++) {
                for (int a = 0; a < 7; a++) {
                    dateBox vBox = new dateBox();
                    vBox.setPrefSize(100, 90);
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