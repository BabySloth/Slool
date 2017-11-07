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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class HeadController implements Initializable{
    public final FXMLLoader loader = new FXMLLoader();
    private TimeClass timeClass;
    private final Timer timer = new Timer();
    private HashMap<String, Node> cache = new HashMap<>();
    private String active;
    private Node node;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private CalendarController calendarController;
    @FXML HBox holder;
    @FXML VBox mainHolder;
    @FXML TextField topTime;
    @FXML Button closeButton, minimizeButton, fullscreenButton;
    @FXML ToggleButton mainChange, dashChange, calendarChange, schoolChange, funChange;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMethods();
        setThreads();
        setVariables();
        buttonSetUp();
        toggleButtonSetUp();
    }
    @FXML private void actionClose(){
        Platform.exit();
        System.exit(0);
    }
    @FXML private void actionFullscreen(){
        Stage stage = (Stage) mainHolder.getScene().getWindow();
        if(stage.isFullScreen()){
            fullscreenButton.setText("Fullscreen");
            stage.setFullScreen(false);
        }else{
            fullscreenButton.setText("Minimize");
            stage.setFullScreen(true);
        }
    }
    @FXML private void actionMinimize(){
        Stage stage = (Stage) mainHolder.getScene().getWindow();
        stage.setIconified(true);
    }
    @FXML private void actionCalendarChange() throws IOException{
        loadFXML("calendar", "calendarFX.fxml");
        calendarController = loader.getController();
    }
    @FXML private void actionMainChange(){
        if(!active.equals("main")){
            removeActive();
            holder.getChildren().add(mainHolder);
            active = "main";
        }
    }
    @FXML private void actionDashChange() throws IOException{
        loadFXML("dash", "dashFX.fxml");
    }

    private void loadFXML(String name, String fxmlName) throws IOException{
        node = cache.get(name);
        if(!active.equals(name)){
            removeActive();
            if(node == null){
                loader.setLocation(getClass().getResource("sceneFXML/"+fxmlName));
                node = loader.load();
                cache.put(name, node);
            }
            holder.getChildren().add(node);
            active = name;
        }
    }
    private void buttonSetUp(){
        double radius = 7;
        closeButton.setShape(new Circle(radius));
        closeButton.setMinSize(radius*2, radius*2);
        closeButton.setMaxSize(radius*2, radius*2);
        minimizeButton.setShape(new Circle(radius));
        minimizeButton.setMinSize(radius*2, radius*2);
        minimizeButton.setMaxSize(radius*2, radius*2);
        fullscreenButton.setShape(new Circle(radius));
        fullscreenButton.setMinSize(radius*2, radius*2);
        fullscreenButton.setMaxSize(radius*2, radius*2);

    }
    private void setTopTime(){
        topTime.setText(timeClass.dayOfWeek+" "+timeClass.monthNumber+"/"+timeClass.day+"/"+timeClass.year+" | "+timeClass.hour+":"+timeClass.minute+" "+timeClass.am_pm);
    }
    private void removeActive(){
        if(active.equals("main")){
            holder.getChildren().remove(mainHolder);
        }else{
            holder.getChildren().remove(node);
        }
    }
    private void toggleButtonSetUp(){
        mainChange.setToggleGroup(toggleGroup);
        dashChange.setToggleGroup(toggleGroup);
        calendarChange.setToggleGroup(toggleGroup);
        schoolChange.setToggleGroup(toggleGroup);
        funChange.setToggleGroup(toggleGroup);
    }
    //Set up
    private void setThreads(){
        Thread fourSecondsThread = new Thread(fourSeconds);
        fourSecondsThread.setDaemon(true);
        fourSecondsThread.start();
    }
    private void setMethods(){
        timeClass = new TimeClass();
    }
    private void setVariables(){
        active = "main"; //main dash calendar school fun
    }

    //Tasks
    private Task fourSeconds = new Task<Void>(){
        @Override
        public Void call(){
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    setTopTime();
                    //System.out.println(calendarController.eventShort);
                }
            }, 4*1000, 4*1000);
            return null;
        }
    };

}
