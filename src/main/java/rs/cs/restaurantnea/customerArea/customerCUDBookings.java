package rs.cs.restaurantnea.customerArea;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import rs.cs.restaurantnea.general.objects.Booking;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.dataMaintenance;
import rs.cs.restaurantnea.general.errorMethods;
import rs.cs.restaurantnea.general.regExMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class customerCUDBookings {
    public static Alert makeBooking(Booking booking) {
        databaseMethods DBM = new databaseMethods();
        Alert alert = new Alert(Alert.AlertType.WARNING);

        if (checkValidInputs(booking)) { // Checks for valid inputs
            return errorMethods.premadeAlertErrors(alert, "One or more inputs invalid", "Booking not made");
        }
        if (checkValidDate(booking)) { // Checks if the date is valid
            return errorMethods.premadeAlertErrors(alert, "The date is invalid", "The date is invalid if: \n• It is today\n•It is earlier than today\n•It is more than one year from now\nBooking not made");
        }

        if (checkDailyBookings(DBM, booking)) { // Checks if the user already has a booking on that day
            return errorMethods.premadeAlertErrors(alert, "You already have a booking for this date", "You are only permitted one booking per day");
        }

        String[][] potentialTableID = findPotentialTables(DBM, booking); // Finds tables that are big enough to fit the amount of people but are not too big
        String[][] overlappedBookings = findOverlappedBookings(DBM, booking); // Finds bookings at a similar time period to the intended booking
        String[][] bookedTables = findTblID(DBM, overlappedBookings); // Finds all tables that are being used at the time of booking so that there are no tables that get double booked. The average time eating out is around 4 hours

        ArrayList<Integer> availableTables = findAvailableBookings(potentialTableID, bookedTables); // Creates an arraylist of all available tables

        if (availableTables.size() > 0) { // If there is a table available, it gets set into the booking object
            booking.setTableID(availableTables.get(0));
        }
        else {
            return errorMethods.premadeAlertErrors(alert, "Restaurant fully booked", "Unfortunately we have no space at that given time, other times might be available. We are sorry if this causes any inconveniences");
        }

        insertBooking(DBM, booking); // Inserts data into the bookings table
        int bookingID = findBookingID(DBM, booking); // Selects the new bookingID
        insertTblBookingLink(DBM, booking, bookingID); // Inserts data into the tablesBookingLink table
        updateMemberPoints(DBM, booking);
        return errorMethods.premadeAlertErrors(alert, "Your booking has been made", booking.getName() + ", we are looking forward to seeing you on " + booking.getDate().toString() + " at " + booking.getTime());
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
    public static boolean checkDailyBookings(databaseMethods DBM, Booking booking) {
        Object[] dateParam = {booking.getDate().toString(), booking.getUser().getCustomerID(), booking.getBookingID()}; // Creates the parameters
        String[][] countBookings = DBM.getData("SELECT COUNT(bookingID) FROM bookings WHERE Day = ? AND custID = ? AND NOT bookingID = ?", dateParam); // Counts how many bookings the user has on one day, can only be 1 or 0
        if (countBookings[0][0].equals("1")) {
            return true;
        }
        return false;
    }
    public static String[][] findTblID(databaseMethods DBM, String[][] overlappedBookings) {
        String[][] bookedTables = {}; // Inits the array
        if (overlappedBookings.length > 1) { // If there are overlapping bookings this is true
            // Because the amount of bookings that overlap varies, this just creates a dynamic string so that the correct amount of parameters are entered
            String bookedTablesSQL = "SELECT tableID FROM tablesBookingLink WHERE bookingID IN (";
            ArrayList<Object> bookedTablesParams = new ArrayList<>();
            for (int i = 0; i < overlappedBookings.length; i++) { // Loops through to add the right amount of parameters
                bookedTablesParams.add(overlappedBookings[i][0]);
                bookedTablesSQL += "?";
                if (i != overlappedBookings.length - 1) { // The last parameter does not need a comma, this would give an error
                    bookedTablesSQL += ",";
                }
            }
            bookedTablesSQL += ") SORT BY ASC";
            Object[] params = bookedTablesParams.toArray();
            return DBM.getData(bookedTablesSQL, params); // Gets tableIDs of bookings at a similar time
        }
        else if (overlappedBookings.length == 1) {
            Object[] params = {overlappedBookings[0][0]};
            return DBM.getData("SELECT tableID FROM tablesBookingLink WHERE bookingID = ?", params);
        }
        return bookedTables;
    }
    public static String[][] findPotentialTables(databaseMethods DBM, Booking booking) {
        Object[] tableParams = {booking.getAmtPpl(), booking.getAmtPpl() + 3}; // Only selects tables that have between the correct amount and +3 of the correct amount of seats to reduce wasted space in case a larger table wants to book which means the business earns more money
        String[][] potentialTableID = DBM.getData("SELECT tableID FROM tables WHERE (seats BETWEEN ? AND ?)", tableParams);
        return potentialTableID;
    }
    public static String[][] findOverlappedBookings(databaseMethods DBM, Booking booking) {
        Object[] bookingParams = {Integer.parseInt(booking.getTime().substring(0,2)) - 4, Integer.parseInt(booking.getTime().substring(0,2)) + 4, booking.getDate().toString(), booking.getBookingID()};
        String[][] overlappedBookings = DBM.getData("SELECT bookingID FROM bookings WHERE Time BETWEEN ? AND ? AND Day = ? AND NOT bookingID = ?", bookingParams); // Finds bookings that overlap with the new booking
        return overlappedBookings;
    }
    public static ArrayList<Integer> findAvailableBookings(String[][] potentialTableID, String[][] bookedTables) {
        ArrayList<Integer> availableTables = new ArrayList<>();
        for (int i = 0; i < potentialTableID.length; i++) { // Loops through all potential tables
            if (bookedTables.length == 0) { // If there are no tables that overlap, then the values are just added to the arraylist
                availableTables.add(Integer.parseInt(potentialTableID[i][0]));
            }
            else if (!dataMaintenance.binarySearch(bookedTables, Integer.parseInt(potentialTableID[i][0]), bookedTables.length)) { // Uses a binary search to find tables that are already in use
                availableTables.add(Integer.parseInt(potentialTableID[i][0])); // Adds tables that are free to the arraylist
            }
        }
        return availableTables;
    }
    public static void insertBooking(databaseMethods DBM, Booking booking) {
        Object[] insertBookingParams = {booking.getUser().getCustomerID(), Integer.parseInt(booking.getTime().substring(0,2)), booking.getDate().toString(), booking.getAmtPpl(), booking.getEventType(), booking.getName()};
        DBM.CUDData("INSERT INTO bookings(custID, Time, Day, amountOfPeople, eventType, bookingName) VALUES(?,?,?,?,?,?)",insertBookingParams); // Inserts data into the bookings table
    }
    public static int findBookingID(databaseMethods DBM, Booking booking) {
        Object[] bookingIDParam = {booking.getUser().getCustomerID(), booking.getDate().toString()};
        String[][] bookingID = DBM.getData("SELECT bookingID FROM bookings WHERE custID = ? AND Day = ?", bookingIDParam); // Selects the bookingID of the new booking
        return Integer.parseInt(bookingID[0][0]);
    }
    public static void insertTblBookingLink(databaseMethods DBM, Booking booking, int bookingID) {
        Object[] insertTblBookingParams = {booking.getTableID(), bookingID};
        DBM.CUDData("INSERT INTO tablesBookingLink(tableID, bookingID) VALUES(?,?)", insertTblBookingParams); // Inserts the link between tables and bookings
    }
    public static void updateMemberPoints(databaseMethods DBM, Booking booking) {
        Object[] updateMemberPointsParams = {booking.getUser().getCustomerID()};
        DBM.CUDData("UPDATE customers SET memberPoints = memberPoints + 5 WHERE custID = ?", updateMemberPointsParams);
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

        if (checkValidInputs(booking) || checkValidDate(booking)) {
            errorMethods.premadeAlertErrors(alert, "One or more inputs are invalid", "The date is invalid if: \n• It is today\n•It is earlier than today\n•It is more than one year from now\nPlease check your inputs again\nBooking not made").show();
            return false;
        }
        if (checkDailyBookings(DBM, booking)) { // Checks if the user already has a booking on that day
            errorMethods.premadeAlertErrors(alert, "You already have a booking for this date", "You are only permitted one booking per day").show();
            return false;
        }

        String[][] potentialTableID = customerCUDBookings.findPotentialTables(DBM, booking); // Finds tables that are big enough to fit the amount of people but are not too big
        String[][] overlappedBookings = customerCUDBookings.findOverlappedBookings(DBM, booking); // Finds bookings at a similar time period to the intended booking
        String[][] bookedTables = customerCUDBookings.findTblID(DBM, overlappedBookings); // Finds all tables that are being used at the time of booking so that there are no tables that get double booked. The average time eating out is around 4 hours

        ArrayList<Integer> availableTables = customerCUDBookings.findAvailableBookings(potentialTableID, bookedTables); // Creates an arraylist of all available tables

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
