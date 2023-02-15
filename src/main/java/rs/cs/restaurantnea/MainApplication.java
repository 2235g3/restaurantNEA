package rs.cs.restaurantnea;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    // This method sets up the stage and scene when the user starts the program
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("welcomePage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 602, 405);
            stage.setTitle("Restaurant NEA");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.out.println("An error occurred!");
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}