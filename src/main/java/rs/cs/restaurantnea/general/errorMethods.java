package rs.cs.restaurantnea.general;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class errorMethods {
    private static Alert warningAlert = new Alert(Alert.AlertType.WARNING);
    public static void defaultErrors(Exception e) { // This method is used for general errors, primarily for bug checking
        warningAlert.setContentText(e.toString());
        warningAlert.setHeaderText("An error has occurred");
        warningAlert.show();
    }
    public static Alert premadeAlertErrors(Alert alert, String headerText, String contentText) { // If an alert is defined elsewhere, the header and content is set
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }
    public static void exceptionErrors(String headerText, String contentText) { // Uses the private alert to set the header and content
        warningAlert.setHeaderText(headerText);
        warningAlert.setContentText(contentText);
        warningAlert.show();
    }

    // These two methods create confirmation alerts
    public static Optional<ButtonType> UADeleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText("Your account is about to be deleted");
        alert.setContentText("Are you sure you want to do this?");
        return alert.showAndWait();
    }

    public static Optional<ButtonType> EBDeleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText("Your booking is about to be deleted");
        alert.setContentText("Are you sure you want to do this?");
        return alert.showAndWait();
    }
}