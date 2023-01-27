package rs.cs.restaurantnea.general;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import rs.cs.restaurantnea.GeneralController;

import java.util.Optional;

public class signOut {
    public static void signOut(ActionEvent event) {
        Optional<ButtonType> confirmation = signOutAlert();
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) { // If the user clicks the "OK" button, they get sent back to the home page.
            new GeneralController().selectNewScene("welcomePage.fxml", event);
        }
    }

    public static Optional<ButtonType> signOutAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Creates an alert that confirms that the user wants to sign out
        alert.setHeaderText("You are about to sign out!");
        alert.setContentText("Are sure you want to do this?");
        return alert.showAndWait(); // Shows the alert but waits for a response
    }
}
