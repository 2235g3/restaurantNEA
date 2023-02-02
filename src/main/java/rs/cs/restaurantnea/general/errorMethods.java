package rs.cs.restaurantnea.general;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import rs.cs.restaurantnea.general.objects.Booking;

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
    public static Alert CBInvalidInputs(Alert alert) {
        alert.setHeaderText("One or more inputs invalid");
        alert.setContentText("Booking not made");
        return alert;
    }
    public static Alert CBInvalidDate(Alert alert) {
        alert.setHeaderText("The date is invalid");
        alert.setContentText("The date is invalid if: \n• It is today\n•It is earlier than today\n•It is more than one year from now\nBooking not made");
        return alert;
    }
    public static Alert CBTooManyBookings(Alert alert) {
        alert.setHeaderText("You already have a booking for this date");
        alert.setContentText("You are only permitted one booking per day");
        return alert;
    }
    public static Alert CBFullyBooked(Alert alert) {
        alert.setHeaderText("Restaurant fully booked");
        alert.setContentText("Unfortunately we have no space at that given time, other times might be available. We are sorry if this causes any inconveniences");
        return alert;
    }
    public static Alert CBBookingSuccess(Alert alert, Booking booking) {
        alert.setHeaderText("Your booking has been made");
        alert.setContentText(booking.getName() + ", we are looking forward to seeing you on " + booking.getDate().toString() + " at " + booking.getTime());
        return alert;
    }
    public static Optional<ButtonType> EBDeleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText("Your booking is about to be deleted");
        alert.setContentText("Are you sure you want to do this?");
        return alert.showAndWait();
    }
    public static Alert EBBookingNotDeleted(Alert alert) {
        alert.setHeaderText("Your booking has not been deleted");
        alert.setContentText("We are happy you still want to eat with us");
        return alert;
    }
    public static Alert EBBookingDeleted(Alert alert) {
        alert.setHeaderText("Your booking has been deleted");
        alert.setHeaderText("We are upset you don't want to eat with us");
        return alert;
    }
}
