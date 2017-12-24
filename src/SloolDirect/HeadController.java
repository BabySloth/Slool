package SloolDirect;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public final class HeadController implements Initializable {
    private FXMLLoader loader;
    private TimeClass timeClass;
    private final Timer timer = new Timer();
    private HashMap<String, Node> cache = new HashMap<>();
    private String active;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private CalendarController calendarController;
    private WorkController workController;
    @FXML
    private HBox holder;
    @FXML
    private VBox vboxChange;
    @FXML
    private TextField topTime;
    @FXML
    private Button closeButton, minimizeButton, fullscreenButton;
    @FXML
    private ToggleButton mainChange, dashChange, calendarChange, schoolChange, funChange;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMethods();
        setThreads();
        setVariables();
        buttonSetUp();
        toggleButtonSetUp();
    }

    @FXML
    private void actionClose() {
        try {
            if (calendarController.writeData) {
                writeCalendarData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    private void actionFullscreen() {
        Stage stage = (Stage) vboxChange.getScene().getWindow();
        if (stage.isFullScreen()) {
            fullscreenButton.setText("Fullscreen");
            stage.setFullScreen(false);
        } else {
            fullscreenButton.setText("Minimize");
            stage.setFullScreen(true);
        }
    }

    @FXML
    private void actionMinimize() {
        Stage stage = (Stage) vboxChange.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void actionCalendarChange() throws IOException {
        loadFXML("calendar", "calendarFX.fxml");
        if (calendarController == null) {
            calendarController = loader.getController();
        }
    }

    @FXML
    private void actionMainChange() throws IOException {
        loadFXML("main", "mainFX.fxml");
    }

    @FXML
    private void actionDashChange() throws IOException {
        loadFXML("dash", "dashFX.fxml");
    }

    @FXML
    private void workChange() throws IOException {
        loadFXML("work", "workFX.fxml");
        if (workController == null) {
            workController = loader.getController();
        }
    }

    private void loadFXML(String name, String fxmlName) throws IOException {
        Node node;
        node = cache.get(name);
        if (!active.equals(name)) {
            removeActive();
            if (node == null) {
                loader = new FXMLLoader(getClass().getResource("sceneFXML/" + fxmlName));
                node = loader.load();
                cache.put(name, node);
            }
            holder.getChildren().add(node);
            active = name;
        }
    }

    private void buttonSetUp() {
        double radius = 7;
        closeButton.setShape(new Circle(radius));
        closeButton.setMinSize(radius * 2, radius * 2);
        closeButton.setMaxSize(radius * 2, radius * 2);
        minimizeButton.setShape(new Circle(radius));
        minimizeButton.setMinSize(radius * 2, radius * 2);
        minimizeButton.setMaxSize(radius * 2, radius * 2);
        fullscreenButton.setShape(new Circle(radius));
        fullscreenButton.setMinSize(radius * 2, radius * 2);
        fullscreenButton.setMaxSize(radius * 2, radius * 2);

    }

    private void setTopTime() {
        topTime.setText(timeClass.dayOfWeek + " " + timeClass.monthNumber + "/" + timeClass.day + "/" + timeClass.year + " | " + timeClass.hour + ":" + timeClass.minute + " " + timeClass.am_pm);
    }

    private void removeActive() {
        holder.getChildren().clear();
        holder.getChildren().add(vboxChange);
    }

    private void toggleButtonSetUp() {
        mainChange.setToggleGroup(toggleGroup);
        dashChange.setToggleGroup(toggleGroup);
        calendarChange.setToggleGroup(toggleGroup);
        schoolChange.setToggleGroup(toggleGroup);
        funChange.setToggleGroup(toggleGroup);
    }

    //Set up
    private void setThreads() {
        Thread fourSecondsThread = new Thread(fourSeconds);
        fourSecondsThread.setDaemon(true);
        fourSecondsThread.start();
    }

    private void setMethods() {
        timeClass = new TimeClass();
    }

    private void setVariables() {
        active = "main"; //main dash calendar school fun
    }

    //Tasks
    private Task fourSeconds = new Task<Void>() {
        @Override
        public Void call() {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    setTopTime();
                }
            }, 4 * 1000, 4 * 1000);
            return null;
        }
    };

    //Write data
    private void writeCalendarData() throws Exception {
        File file = new File("/Users/BabySloth/SloolData/Data2.xml");
        HashMap<String, String> combineMap = new HashMap<>();
        combineMap.putAll(calendarController.eventLong);
        combineMap.putAll(calendarController.eventShort);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        org.w3c.dom.Node years = document.getFirstChild();
        NodeList yearList = years.getChildNodes();
        for (int i = 0; i < yearList.getLength(); i++) {
            org.w3c.dom.Node node = yearList.item(i);
            if (node.getNodeName().equals("Year")) {
                Element element = (Element) node;
                element.setTextContent(returnEventContext(element.getAttribute("id"), combineMap));
            }
        }

        TransformerFactory factory1 = TransformerFactory.newInstance();
        Transformer transformer = factory1.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);
        transformer.transform(source, streamResult);

    }

    private String returnEventContext(String year, HashMap<String, String> map) {
        String eventReturn = "";
        for (String key : map.keySet()) {
            String[] keyList = key.split(",");
            if (keyList[1].equals(year)) {
                eventReturn += key + "|" + map.get(key) + "||";
            }
        }
        return eventReturn;
    }


}
