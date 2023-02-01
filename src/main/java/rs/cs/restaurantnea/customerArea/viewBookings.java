package rs.cs.restaurantnea.customerArea;

import rs.cs.restaurantnea.Booking;
import rs.cs.restaurantnea.IOData.databaseMethods;
import rs.cs.restaurantnea.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static rs.cs.restaurantnea.bookingController.user;

public class viewBookings {
    public static String[][] viewBookings() {
        databaseMethods DBM = new databaseMethods();

        String[][] customerBookings = getBookingsData(DBM, user);
        return customerBookings;
    }
    public static String[][] getBookingsData(databaseMethods DBM, User user) {
        Object[] bookingParams = {user.getCustomerID()};
        String[][] customerBookings = DBM.getData("SELECT bookingName, Day, Time, amountOfPeople, bookingID FROM bookings WHERE custID = ? ", bookingParams);
        return customerBookings;
    }
}
