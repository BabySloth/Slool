package SloolDirect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("SceneFXML/ViewFX.fxml"));
        primaryStage.setTitle("SLool");
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.setFullScreen(true);
        primaryStage.setOnCloseRequest((event) -> event.consume());
        primaryStage.show();
    }
    public static void main(String[] args) throws IOException{

        //Makes the logo for dock MAC
        URL pictureURL = new File("/Users/BabySloth/SloolData/Resources/SloolLogo.png").toURI().toURL();
        Image image = new ImageIcon(pictureURL).getImage();
        com.apple.eawt.Application.getApplication().setDockIconImage(image);
        launch(args);
    }
}
