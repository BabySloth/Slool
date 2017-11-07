package SloolDirect;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class DashController implements Initializable{
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void start(String string){
        System.out.println("Dash: "+string);
    }
}
