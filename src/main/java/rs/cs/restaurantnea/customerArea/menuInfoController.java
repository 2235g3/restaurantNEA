package rs.cs.restaurantnea.customerArea;

import javafx.event.ActionEvent;
import rs.cs.restaurantnea.general.objects.User;

public class menuInfoController {

    private static User user;

    public static void getData(User getUser) {
        user = getUser;
    }
    public void signOut(ActionEvent event) {
        generalCustomerMethods.signOut(event);
    }
    public void toBookings(ActionEvent event) {
        bookingController BC = new bookingController();
        BC.getData(user);
        generalCustomerMethods.toBookings(event);
    }
    public void toAccount(ActionEvent event) {
        accountController AC = new accountController();
        AC.getData(user);
        generalCustomerMethods.toAccount(event);
    }
}
