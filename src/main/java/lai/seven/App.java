/*
 * Cayden Lai
 * 19 April 2025
 * CSA 7th Period
 * Commander Schenk
 */

package lai.seven;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    
    private static Scene scene;
    //Loads the main.fxml UI and shows window 
    @Override
    public void start(Stage stage) throws IOException {
        //Load FXML layout and creates scene
        scene = new Scene(loadFXML("main"), 800, 650); 
        stage.setScene(scene);
        stage.setTitle("Runner Management System"); 
        //Display window
        stage.show();
    }

    //Replaces the current scene with a new one
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    //Loads the FXML file
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/lai/seven/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    //Main method to launch the application
    public static void main(String[] args) {
        launch();
    }

}