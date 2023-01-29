package rs.cs.restaurantnea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
        int amtPpl = 0;
        if (String.valueOf(amtPplInput.getValue()).equals("Big table (more than 20 people)")) {
            amtPpl = 21;
        }
        else {
            amtPpl = Integer.parseInt(String.valueOf(amtPplInput.getValue()));
        }
        Booking newBooking = new Booking(nameInput.getText(), dateInput.getValue(), String.valueOf(timeInput.getValue()), amtPpl,String.valueOf(typeInput.getValue()), user, -1);
        Alert alert = custBookings.makeBooking(newBooking);
        alert.show();
    }
    public static void getData(User getUser) {
        user = getUser;
    }
    public void initialize() {
        for (int i = 9; i < 21; i++) {
            if (i < 10) {
                timeInput.getItems().add("0" + i + ":00");
            }
            else {
                timeInput.getItems().add(i + ":00");
            }
        }
        timeInput.setValue("09:00");
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
