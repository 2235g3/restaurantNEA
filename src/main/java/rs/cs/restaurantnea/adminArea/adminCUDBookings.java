package rs.cs.restaurantnea.adminArea;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.dataMaintenance;
import rs.cs.restaurantnea.general.errorMethods;
import rs.cs.restaurantnea.general.objects.Booking;
import rs.cs.restaurantnea.general.regExMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class adminCUDBookings {
    public static boolean checkCustomerExists(int customerID) {
        databaseMethods DBM = new databaseMethods();

        Object[] customerParams= {customerID};
        String[][] countCustomers = DBM.getData("SELECT COUNT(custID) FROM customers WHERE custID = ?", customerParams);
        if (countCustomers.length == 1) { // If there the customer exists in the system, true is returned
            return true;
        }
        else {
            return false;
        }
    }
    public static String[][] findTblID(databaseMethods DBM, String[][] overlappedBookings) {
        String[][] bookedTables = {}; // Inits the array
        if (overlappedBookings.length > 0) { // If there are overlapping bookings this is true
            // Because the amount of bookings that overlap varies, this just creates a dynamic string so that the correct amount of parameters are entered
            String bookedTablesSQL = "SELECT tableID FROM tablesBookingLink WHERE bookingID IN (";
            ArrayList<Object> dynBookedTablesParams = new ArrayList<>(); // Allows for a list of parameters that are dynamically sized
            for (int i = 0; i < overlappedBookings.length; i++) { // Loops through to add the right amount of parameters
                dynBookedTablesParams.add(overlappedBookings[i][0]);
                bookedTablesSQL += "?";
                if (i != overlappedBookings.length - 1) { // The last parameter does not need a comma, this would give an error
                    bookedTablesSQL += ",";
                }
            }
            bookedTablesSQL += ") SORT BY ASC";
            Object[] bookedTablesParams = dynBookedTablesParams.toArray();
            bookedTables = DBM.getData(bookedTablesSQL, bookedTablesParams); // Gets tableIDs of bookings at a similar time
        }
        return bookedTables;
    }
    public static String[][] findPotentialTables(databaseMethods DBM, Booking booking) {
        Object[] tableParams = {booking.getAmtPpl(), booking.getAmtPpl() + 3}; // Only selects tables that have between the correct amount and +3 of the correct amount of seats to reduce wasted space in case a larger table wants to book which means the business earns more money
        String[][] potentialTableID = DBM.getData("SELECT tableID FROM tables WHERE (seats BETWEEN ? AND ?)", tableParams);
        return potentialTableID;
    }
    public static String[][] findOverlappedBookings(databaseMethods DBM, Booking booking) {
        Object[] bookingParams = {Integer.parseInt(booking.getTime().substring(0,1)) - 4, Integer.parseInt(booking.getTime().substring(0,1)) + 4, booking.getDate().toString()};
        String[][] overlappedBookings = DBM.getData("SELECT bookingID FROM bookings WHERE (time BETWEEN ? AND ?) AND (Day) = ?", bookingParams); // Finds bookings that overlap with the new booking
        return overlappedBookings;
    }
    public static ArrayList<Integer> findAvailableBookings(String[][] potentialTableID, String[][] bookedTables) {
        ArrayList<Integer> availableTables = new ArrayList<>();
        for (int i = 0; i < potentialTableID.length; i++) { // Loops through all potential tables
            if (bookedTables.length == 0) { // If there are no tables that overlap, then the values are just added to the arraylist
                availableTables.add(Integer.parseInt(potentialTableID[i][0]));
            } else if (!dataMaintenance.binarySearch(bookedTables, Integer.parseInt(potentialTableID[i][0]), bookedTables.length)) { // Uses a binary search to find tables that are already in use
                availableTables.add(Integer.parseInt(potentialTableID[i][0])); // Adds tables that are free to the arraylist
            }
        }
        return availableTables;
    }
    public static boolean checkDailyBookings(databaseMethods DBM, Booking booking) {
        Object[] custIDParams = {booking.getBookingID()};
        String[][] custID = DBM.getData("SELECT custID FROM bookings WHERE bookingID = ?", custIDParams);
        Object[] dateParam = {booking.getDate().toString(), custID[0][0], booking.getBookingID()}; // Creates the parameters
        String[][] countBookings = DBM.getData("SELECT COUNT(bookingID) FROM bookings WHERE Day = ? AND custID = ? AND NOT bookingID = ?", dateParam); // Counts how many bookings the user has on one day, can only be 1 or 0
        if (countBookings[0][0].equals("1")) {
            return true;
        }
        return false;
    }
    public static boolean checkValidInputs(Booking booking) {
        if (booking.getDate() == null || !regExMatchers.createNameMatcher(booking.getName()).matches()) { // If no date is inputted or the name is invalid, an error is thrown
            return true;
        }
        return false;
    }
    public static boolean checkValidDate(Booking booking) {
        LocalDate currentDate = LocalDate.now(); // Gets the date as of right now
        LocalDate maxBookDate = currentDate.plusYears(1); // Gets the date as of one year from now
        if (booking.getDate().isBefore(currentDate) || booking.getDate().equals(currentDate) || booking.getDate().isAfter(maxBookDate)) { // Checks if the booking is within a valid range
            return true;
        }
        return false;
    }
    public static void deleteBookingData(int bookingID) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        Optional<ButtonType> confirmation = errorMethods.EBDeleteConfirmation();
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) { // Runs if the user confirms they want to delete their booking
            databaseMethods DBM = new databaseMethods();
            Object[] deleteParam = {bookingID}; // The only parameter needed is the booking id for both queries
            DBM.CUDData("DELETE FROM bookings WHERE bookingID = ?", deleteParam); // Deletes the actual booking
            DBM.CUDData("DELETE FROM tablesBookingLink WHERE bookingID = ?", deleteParam); // Deletes the bookings' link to its restaurant table
            errorMethods.premadeAlertErrors(alert, "Your booking has been deleted", "We are upset you don't want to eat with us").show();
        } else {
            errorMethods.premadeAlertErrors(alert, "Your booking has not been deleted", "We are happy you still want to eat with us").show();
        }
    }
    public static boolean updateBookings(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        databaseMethods DBM = new databaseMethods();

        if (checkDailyBookings(DBM, booking)) { // Checks if the user already has a booking on that day
            errorMethods.premadeAlertErrors(alert, "You already have a booking for this date", "You are only permitted one booking per day").show();
            return false;
        }
        if (checkValidInputs(booking)) {
            errorMethods.exceptionErrors("One or more inputs invalid", "Booking not made");
            return false;
        }
        if (checkValidDate(booking)) {
            errorMethods.exceptionErrors("The date is invalid", "The date is invalid if: \n• It is today\n•It is earlier than today\n•It is more than one year from now\nBooking not made");
            return false;
        }

        String[][] potentialTableID = adminCUDBookings.findPotentialTables(DBM, booking); // Finds tables that are big enough to fit the amount of people but are not too big
        String[][] overlappedBookings = adminCUDBookings.findOverlappedBookings(DBM, booking); // Finds bookings at a similar time period to the intended booking
        String[][] bookedTables = adminCUDBookings.findTblID(DBM, overlappedBookings); // Finds all tables that are being used at the time of booking so that there are no tables that get double booked. The average time eating out is around 4 hours

        ArrayList<Integer> availableTables = adminCUDBookings.findAvailableBookings(potentialTableID, bookedTables); // Creates an arraylist of all available tables

        if (availableTables.size() == 0) { // Runs if there are no available tables
            errorMethods.premadeAlertErrors(alert, "Restaurant fully booked", "Unfortunately we have no space at that given time, other times might be available. We are sorry if this causes any inconveniences").show();
            return false;
        } else { // If there is a table available, it gets set into the booking object and the booking is updated
            booking.setTableID(availableTables.get(0));
            Object[] updateBookingsParams = {booking.getName(), booking.getDate().toString(), Integer.parseInt(booking.getTime()), booking.getAmtPpl(), booking.getBookingID()};
            DBM.CUDData("UPDATE bookings SET bookingName = ?, Day = ?, Time = ?, amountOfPeople = ? WHERE bookingID = ?", updateBookingsParams);
            Object[] updateLinkParams = {booking.getTableID(), booking.getBookingID()};
            DBM.CUDData("UPDATE tablesBookingLink SET tableID = ? WHERE bookingID = ?", updateLinkParams);
            errorMethods.premadeAlertErrors(alert, "Your booking has been made", booking.getName() + ", we are looking forward to seeing you on " + booking.getDate().toString() + " at " + booking.getTime()).show();
            return true;
        }
    }
}
