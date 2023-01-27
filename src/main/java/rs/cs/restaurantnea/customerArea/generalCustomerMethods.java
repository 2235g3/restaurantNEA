package rs.cs.restaurantnea.customerArea;

import javafx.event.ActionEvent;
import rs.cs.restaurantnea.GeneralController;
import rs.cs.restaurantnea.general.signOut;

public class generalCustomerMethods {
    public static void signOut(ActionEvent event) {
        signOut.signOut(event);
    }
    public static void toAccount(ActionEvent event) {
        new GeneralController().selectNewScene("custAccount.fxml",event);
    }
    public static void toBookings(ActionEvent event) {
        new GeneralController().selectNewScene("custBookings.fxml",event);
    }
    public static void toOrders(ActionEvent event) {
        new GeneralController().selectNewScene("custOrders.fxml",event);
    }
    public static void toMenuInfo(ActionEvent event) {
        new GeneralController().selectNewScene("menuInfo.fxml",event);
    }
}
