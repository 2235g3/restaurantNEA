package rs.cs.restaurantnea;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GeneralController {

    public Stage stage;
    private Scene newScene;
    private Parent root;
    public void selectNewScene(String fileName, ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource(fileName)); // Gets the new FXML scene
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Gets the stage that is to be changed
            newScene = new Scene(root); // Makes the FXML into a scene
            stage.setScene(newScene); // Sets the new scene
            stage.show(); // Displays the new scene
        } catch (Exception e) {
            System.out.println("An Error Occurred!");
            System.out.println(e);
            e.printStackTrace();
        }
    }

    // These methods change the scene when login/signup buttons are pressed
    public void loginPage(ActionEvent event) {selectNewScene("loginPage.fxml",event);}
    public void signUpPage(ActionEvent event) {selectNewScene("signupPage.fxml",event);}
}
