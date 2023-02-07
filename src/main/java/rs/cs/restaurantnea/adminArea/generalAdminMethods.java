package rs.cs.restaurantnea.adminArea;

import javafx.event.ActionEvent;
import rs.cs.restaurantnea.GeneralController;
import rs.cs.restaurantnea.general.signOut;

public class generalAdminMethods {
    public static void signOut(ActionEvent event) {
        signOut.signOut(event);
    }
    public static void toEmails(ActionEvent event) {
        new GeneralController().selectNewScene("adminEmails.fxml", event);
    }
    public static void toBookings(ActionEvent event) {
        new GeneralController().selectNewScene("adminBookings.fxml", event);
    }
    public static void toUsers(ActionEvent event) {
        new GeneralController().selectNewScene("adminusers.fxml", event);
    }
}
