package rs.cs.restaurantnea.adminArea;

import javafx.event.ActionEvent;

public class emailController {
    public void signOut(ActionEvent event) {
        generalAdminMethods.signOut(event);
    }
    public void toBookings(ActionEvent event) {
        generalAdminMethods.toBookings(event);
    }
}
