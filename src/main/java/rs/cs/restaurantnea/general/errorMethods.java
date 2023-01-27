package rs.cs.restaurantnea.general;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class errorMethods {
    public static void defaultErrors(Exception e) {
        System.out.println("Error occurred");
        System.out.println(e);
    }
    public static Alert SUInvalidInputs(Alert alert) {
        alert.setHeaderText("One or more inputs invalid");
        alert.setContentText("No account has been created");
        return alert;
    }

    public static Alert SUAccountExists(Alert alert) {
        alert.setHeaderText("An account with that email already exists");
        alert.setContentText("No account has been created");
        return alert;
    }
    public static Alert SUSuccess(Alert alert) {
        alert.setHeaderText("Account created!");
        alert.setContentText("You can now login");
        return alert;
    }
    public static Alert LIAccountNotExists(Alert alert) {
        alert.setHeaderText("Account does not exist");
        alert.setContentText("Please try again");
        return alert;
    }
    public static Alert LIWrongPassword(Alert alert) {
        alert.setHeaderText("Password incorrect");
        alert.setContentText("Please try again");
        return alert;
    }
    public static Alert LISuccess(Alert alert) {
        alert.setHeaderText("You have logged in!");
        alert.setContentText("All information was correct");
        return alert;
    }
    public static Alert UAInvalidInputs(Alert alert) {
        alert.setHeaderText("One or more inputs invalid");
        alert.setContentText("Your data has not been saved");
        return alert;
    }
    public static Alert UASaveSuccess(Alert alert, String[] inputFields) {
        alert.setHeaderText("Your data has been saved!");
        alert.setContentText("First Name: " + inputFields[0] + "\n Last Name: " + inputFields[1] + "\n Promotional Emails Frequency: " + inputFields[2]);
        return alert;
    }
    public static Optional<ButtonType> UADeleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText("Your account is about to be deleted");
        alert.setContentText("Are you sure you want to do this?");
        return alert.showAndWait();
    }
    public static Alert UADeleteWarning(Alert alert) {
        alert.setHeaderText("Your account has now been deleted");
        alert.setContentText("You have now been signed out");
        return alert;
    }
    public static Alert UAAccountExists(Alert alert) {
        alert.setHeaderText("Your account has not been deleted");
        alert.setContentText("You will stayed signed in");
        return alert;
    }
}
