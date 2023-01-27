package rs.cs.restaurantnea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import rs.cs.restaurantnea.customerArea.custBookings;
import rs.cs.restaurantnea.customerArea.generalCustomerMethods;

public class bookingController {
    @FXML
    private TextField nameInput;
    @FXML
    private DatePicker dateInput;
    @FXML
    private ChoiceBox timeInput;
    @FXML
    private ChoiceBox amtPplInput;
    @FXML
    private ChoiceBox typeInput;
    private static User user;

    public void signOut(ActionEvent event) {
        generalCustomerMethods.signOut(event);
    }
    public void toAccount(ActionEvent event) {
        generalCustomerMethods.toAccount(event);
    }
    public void toOrders(ActionEvent event) {
        generalCustomerMethods.toOrders(event);
    }
    public void toMenuInfo(ActionEvent event) {
        generalCustomerMethods.toMenuInfo(event);
    }
    public void makeBooking(ActionEvent event) {
        Booking newBooking = new Booking(nameInput.getText(), dateInput.getValue(), String.valueOf(timeInput.getValue()), Integer.parseInt(String.valueOf(amtPplInput.getValue())),String.valueOf(typeInput.getValue()), user);
        custBookings.makeBooking(newBooking);
        System.out.println(newBooking.getDate());
        System.out.println(newBooking.getEventType());;
    }
    public static void getData(User getUser) {
        user = getUser;
    }
    public void initialize() {
        timeInput.getItems().add("Breakfast 9am - 12pm");
        timeInput.getItems().add("Lunch 12pm - 5pm");
        timeInput.getItems().add("Dinner 5pm - Close");
        timeInput.setValue("Breakfast 9am - 12pm");
        for (int i = 1; i < 21; i++) {
            amtPplInput.getItems().add(i);
        }
        amtPplInput.getItems().add("Big table (more than 20 people)");
        amtPplInput.setValue(1);
        typeInput.getItems().add("General Eating");
        typeInput.getItems().add("School Trip");
        typeInput.getItems().add("Business Meal");
        typeInput.getItems().add("Birthday Meal");
        typeInput.getItems().add("Other");
        typeInput.setValue("General Eating");
    }
}
