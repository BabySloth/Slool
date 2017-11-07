package SloolDirect;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sceneFXML/ViewFX.fxml"));
        primaryStage.setTitle("SLool");
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.setFullScreen(true);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent event){
                event.consume();
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.show();

    }
    public static void main(String[] args) throws IOException{
        URL pictureURL = new File("/Users/BabySloth/SloolData/Resources/SloolLogo.png").toURI().toURL();
        Image image = new ImageIcon(pictureURL).getImage();
        com.apple.eawt.Application.getApplication().setDockIconImage(image);
        launch(args);
    }
}
