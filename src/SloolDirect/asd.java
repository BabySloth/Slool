package SloolDirect;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class asd implements Initializable {
    final private String[] monthIndex= {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private YearMonth now = YearMonth.now();
    private ToggleGroup monthToggle = new ToggleGroup();
    private ArrayList<vboxPane> calendarDays = new ArrayList<>(42);
    private ArrayList<ToggleButton> monthButton = new ArrayList<>(12);
    private LocalDate nowDate = LocalDate.now();
    private ToggleGroup topicToggle = new ToggleGroup();
    private HashMap<String, String> dateEvent = new HashMap<>();
    private final File inputFile = new File("/Users/BabySloth/SloolData/Data2.xml");
    private String yearsLoaded="";
    private boolean firstTime = true;
    private String[] yearsArray;
    private int[] selectedDays = new int[4];
    private int yearWrite;
    private String keyRemove;
    @FXML GridPane monthHolderG;
    @FXML Text displayYear, bottomDayOfWeek, bottomDay, bottomSuffix, bottomYear;
    @FXML HBox monthHolder;
    @FXML VBox eventDisplayHolder;
    @FXML TextField firstDay, secondDay, inputEvent, removeThis;
    @FXML ToggleButton toggleSchool, toggleEntertainment, togglePersonal;
    @FXML Button monthDecrease, monthIncrease;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readData();
        updateView();
        firstDay.setText(nowDate.getMonthValue()+"."+nowDate.getDayOfMonth()+"."+nowDate.getYear());
        selectedDays[0] = nowDate.getDayOfYear();
        selectedDays[1] = nowDate.getYear();
        firstTime = false;
    }

    private void updateView(){
        LocalDate localDate = LocalDate.of(now.getYear(), now.getMonth(), 1);
        setTopDisplay(localDate);
        String repeatEvents = "";
        while(!localDate.getDayOfWeek().toString().equals("SUNDAY")){
            localDate = localDate.minusDays(1);
        }
        for(vboxPane vbox : calendarDays){
            Text text = new Text(localDate.getDayOfMonth()+"");

            vbox.getChildren().add(text);
            HashMap<String, String> addThese = new HashMap<>();
            for (String key : dateEvent.keySet()) {
                if(key.split(",").length == 4){
                    if(isBetweenDates(stringToInt(key), localDate)){
                        String[] diffEvents = dateEvent.get(key).split(";");
                        for (int i = 0; i < diffEvents.length; i++) {
                            if(!repeatEvents.contains(key)){
                                vbox.getChildren().add(returnStack(diffEvents[i], false));
                                repeatEvents += key;
                            }else{
                                vbox.getChildren().add(returnStack(diffEvents[i], true));
                            }
                        }
                        addThese.put(key, dateEvent.get(key));
                    }

                }
            }
            for (String key : dateEvent.keySet()) {
                if(key.split(",").length == 2){
                    if(isBetweenDates(stringToInt(key), localDate)){
                        String[] diffEvents = dateEvent.get(key).split(";");
                        for (int i = 0; i < diffEvents.length; i++) {
                            vbox.getChildren().add(returnStack(diffEvents[i], false));
                        }
                        addThese.put(key, dateEvent.get(key));
                    }
                }
            }
            vbox.setEventString(addThese);
            vbox.setDate(localDate);
            yearWrite = now.getYear();
            localDate = localDate.plusDays(1);
        }
    }
    private void setTopDisplay(LocalDate localDate){
        //Setting month display
        for(ToggleButton toggleButton : monthButton){
            if(localDate.getYear() == nowDate.getYear()){
                if(toggleButton.getText().equals(monthIndex[nowDate.getMonthValue()-1])){
                    toggleButton.setStyle("-fx-text-fill: #F9E153");

                }
            }else{
                toggleButton.setStyle("-fx-text-fill: #c0c0c0");
            }
        }
        //Set Year Displaying
        displayYear.setText(now.getYear()+"");
        if(now.getYear() == nowDate.getYear()){
            displayYear.setFill(new Color(0.9373, 0.9098, 0.1294, 1));
        }else{
            displayYear.setFill(new Color(0.7529, 0.7529, 0.7529, 1));
        }
    }

    @FXML private void removeEvent(){
        String[] removePart = dateEvent.get(keyRemove).split(";");
        String addBack = "";
        if(removeThis.getText().isEmpty()) return;
        if(removePart.length == 1){
            dateEvent.remove(keyRemove);
        }else{
            for (int i = 0; i < removePart.length; i++) {
                if(removePart[i].contains(removeThis.getText())){
                    removePart[i] = "";
                }
                addBack += removePart[i];
            }
            dateEvent.put(keyRemove, addBack);
        }
        removeThis.setText("");
        writeData();
        updateView();
    }

    @FXML private void newEvent(KeyEvent kEvent){
        if(kEvent.getCode().equals(KeyCode.ENTER)){
            addEvent(inputEvent.getText());
            inputEvent.setText("");
            updateView();
        }else if(kEvent.getCode().equals(KeyCode.ESCAPE)){
            inputEvent.setText("");
        }
    }
    private void addEvent(String event){
        String eventString = event;
        String key1 = selectedDays[0]+","+selectedDays[1];
        String key2 = key1+","+selectedDays[2]+","+selectedDays[3];
        if(toggleSchool.isSelected()){
            eventString = "S"+eventString;
        }else if(toggleEntertainment.isSelected()){
            eventString = "E"+eventString;
        }else if(togglePersonal.isSelected()){
            eventString = "P"+eventString;
        }
        System.out.println("Inside before: " + dateEvent.get(key1));
        if(secondDay.getText().isEmpty()){
            if (dateEvent.get(key1) != null) {
                dateEvent.put(key1, dateEvent.get(key1)+";"+eventString);
            }else{
                dateEvent.put(key1, eventString+";");
            }
        }else{
            if (dateEvent.get(key2) != null) {
                dateEvent.put(key2, dateEvent.get(key2)+";"+eventString);
            }else{
                dateEvent.put(key2, eventString+";");
            }
            if(selectedDays[3]>selectedDays[1]){
                yearWrite = selectedDays[1];
            }
        }
        toggleSchool.setSelected(true);
        yearWrite = selectedDays[1];
        updateView();
        writeData();
    }
    @FXML private void changeDate(Event event){
        if(event.getSource().equals(firstDay)){
            selectedDays[0] = nowDate.getDayOfYear();
            selectedDays[1] = nowDate.getYear();
            firstDay.setText(nowDate.getMonthValue()+"."+nowDate.getDayOfMonth()+"."+nowDate.getYear());
        }else{
            secondDay.setText("");
        }
    }

    //Takes string returns boolean if equal/between 2 dates(string)
    private boolean isBetweenDates(int[] dateRange, LocalDate compareThis){
        LocalDate compareDate1 = LocalDate.ofYearDay(dateRange[1], dateRange[0]);
        LocalDate compareDate2 = null;
        if(dateRange.length == 4) compareDate2 = LocalDate.ofYearDay(dateRange[3], dateRange[2]);
        if(compareThis.equals(compareDate1)){
            return true;
        }else if(compareDate2 != null){
            return (compareThis.isAfter(compareDate1) && compareThis.isBefore(compareDate2.plusDays(1)));
        }
        return false;
    }
    //Takes string array and returns int array
    private int[] stringToInt(String compare){
        int[] newIntArray;
        String[] stringArray = compare.split(",");
        if(stringArray.length == 2){
            newIntArray = new int[2];
            newIntArray[0] = Integer.parseInt(stringArray[0]);
            newIntArray[1] = Integer.parseInt(stringArray[1]);
        }else{
            newIntArray = new int[4];
            for (int i = 0; i < 4; i++) {
                newIntArray[i] = Integer.parseInt(stringArray[i]);
            }
        }
        return newIntArray;
    }
    //Read and write data
    private void writeData(){
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(inputFile);
            Node Events = doc.getFirstChild();
            NodeList list = Events.getChildNodes();
            for(int i = 0; i < list.getLength(); i++){
                Node node = list.item(i);
                if(node.getNodeName().equals("Year")){
                    Element element = (Element) node;
                    if(element.getAttribute("id").equals(yearWrite+"")){
                        element.setTextContent(updateXmlString());
                    }
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(inputFile);
            transformer.transform(source, result);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private String updateXmlString(){
        String writeThis = "";
        for(String key : dateEvent.keySet()){
            if(key.split(",")[1].equals(yearWrite+"")){
                writeThis += key+"|"+dateEvent.get(key)+"||";
            }
        }
        return writeThis;
    } //FIX
    private void readData(){
        try{
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            UserHandler userHandler = new UserHandler();
            saxParser.parse(inputFile, userHandler);
        }catch (Exception e){e.printStackTrace();}
        yearsArray = yearsLoaded.split(" ");

    }
    class UserHandler extends DefaultHandler {
        int yearRead;
        boolean bYear = false;
        boolean matched;
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(qName.equalsIgnoreCase("Year")){
                yearRead = Integer.parseInt(attributes.getValue("id"));
                if(firstTime){
                    if(Math.abs(yearRead-now.getYear())<2){
                        yearsLoaded += yearRead+" ";
                        bYear = true;
                    }
                }else{
                    for(int i = 0; i < yearsArray.length; i++){
                        if(yearRead == Integer.parseInt(yearsArray[i])){
                            matched = true;
                        }
                    }
                    if(matched){
                        matched = false;
                    }else{
                        if(Math.abs(yearRead-now.getYear())<2){
                            bYear = true;
                            yearsLoaded += yearRead+" ";
                        }
                    }
                }
            }
        }
        @Override
        public void characters(char ch[], int start, int length) throws SAXException {
            if(bYear){
                inputToMap(new String(ch, start, length));
                bYear = false;
            }
        }

        private void inputToMap(String input){
            if(input.trim().isEmpty()){
                return;
            }
            String[] arrayPair = input.split("\\|\\|");
            for(int i = 0; i<arrayPair.length; i++){
                String[] arrayKeyValue = arrayPair[i].split("\\|");
                dateEvent.put(arrayKeyValue[0], arrayKeyValue[1]);
            }
        }
    }

    class vboxPane extends VBox {
        private LocalDate date;
        private HashMap<String, String> tempHash;
        public vboxPane(){
            this.setOnMouseClicked(e->{
                removeThis.setText("");
                if(e.isShiftDown()){ //shift click 2,3
                    if(date.getDayOfYear()>selectedDays[0] || date.getYear() > selectedDays[1]){
                        selectedDays[2] = date.getDayOfYear();
                        selectedDays[3] = date.getYear();
                        secondDay.setText(date.getMonthValue()+"."+date.getDayOfMonth()+"."+String.valueOf(date.getYear()));
                    }
                }else{ //regular click 0,1
                    firstDay.setText(date.getMonthValue()+"."+date.getDayOfMonth()+"."+String.valueOf(date.getYear()));
                    selectedDays[0] = date.getDayOfYear();
                    selectedDays[1] = date.getYear();
                }
                eventDisplayHolder.getChildren().clear();
                if(!tempHash.isEmpty()){
                    for(String key: tempHash.keySet()){
                        String[] eventArray = tempHash.get(key).split(";");
                        for (int i = 0; i < eventArray.length; i++) {
                            textExtends text = new textExtends(eventArray[i].substring(1, eventArray[i].length()), key);
                            text.setFont(Font.font("Arial", 15));
                            text.setFill(setColor(eventArray[i]));
                            eventDisplayHolder.getChildren().add(text);
                        }
                    }
                }
            });
        }

        protected void setDate(LocalDate localDate){
            date = localDate;
        }
        protected void setEventString(HashMap hash){
            tempHash = new HashMap<>();
            tempHash = hash;
        }

    }
    private Color setColor(String string){
        if(string.startsWith("S")){
            return new Color(0.0784, 0.5843, 0.8784, 1);
        }else if(string.startsWith("E")){
            return new Color(0.3255, 0.8667, 0.4235, 1);
        }else{//p
            return new Color(1, 0.2431, 0.2549, 1);
        }
    }
    class textExtends extends Text{
        public textExtends(String string, String key){
            this.setText(string);
            this.setOnMouseClicked(e->{
                removeThis.setText(this.getText());
                keyRemove = key;
            });
        }
    }


    private StackPane returnStack (String string, boolean showed){
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(130, 20);
        Rectangle rectangle = new Rectangle(130, 20);
        rectangle.setFill(setColor(string));
        if(showed){
            stackPane.getChildren().add(rectangle);
        }else{
            Text text = new Text(string.substring(1, string.length()));
            text.setFill(new Color(0, 0, 0, 1));
            stackPane.getChildren().setAll(rectangle, text);
        }
        return stackPane;
    }
}
