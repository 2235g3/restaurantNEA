package rs.cs.restaurantnea.customerArea;

import javafx.scene.control.Alert;
import rs.cs.restaurantnea.Booking;
import rs.cs.restaurantnea.IOData.databaseMethods;
import rs.cs.restaurantnea.general.dataMaintenance;
import rs.cs.restaurantnea.general.errorMethods;
import rs.cs.restaurantnea.general.regExMatchers;

import java.time.LocalDate;
import java.util.ArrayList;

public class makeBookings {
    public static Alert makeBooking(Booking booking) {
        databaseMethods DBM = new databaseMethods();
        Alert alert = new Alert(Alert.AlertType.WARNING);

        if (checkValidInputs(booking)) { // Checks for valid inputs
            return errorMethods.CBInvalidInputs(alert);
        }
        if (checkValidDate(booking)) { // Checks if the date is valid
            return errorMethods.CBInvalidDate(alert);
        }

        if (checkDailyBookings(DBM, booking)) { // Checks if the user already has a booking on that day
            return errorMethods.CBTooManyBookings(alert);
        }

        String[][] potentialTableID = findPotentialTables(DBM, booking); // Finds tables that are big enough to fit the amount of people but are not too big
        String[][] overlappedBookings = findOverlappedBookings(DBM, booking); // Finds bookings at a similar time period to the intended booking
        String[][] bookedTables = findTblID(DBM, overlappedBookings); // Finds all tables that are being used at the time of booking so that there are no tables that get double booked. The average time eating out is around 4 hours

        ArrayList<Integer> availableTables = findAvailableBookings(potentialTableID, bookedTables); // Creates an arraylist of all available tables

        if (availableTables.size() > 0) { // If there is a table available, it gets set into the booking object
            booking.setTableID(availableTables.get(0));
        }
        else {
            return errorMethods.CBFullyBooked(alert);
        }

        insertBooking(DBM, booking); // Inserts data into the bookings table
        int bookingID = findBookingID(DBM, booking); // Selects the new bookingID
        insertTblBookingLink(DBM, booking, bookingID); // Inserts data into the tablesBookingLink table
        return errorMethods.CBBookingSuccess(alert, booking);
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
        Object[] dateParam = {booking.getDate().toString(), booking.getUser().getCustomerID()}; // Creates the parameters
        String[][] countBookings = DBM.getData("SELECT COUNT(bookingID) FROM bookings WHERE Day = ? AND custID = ?", dateParam); // Counts how many bookings the user has on one day, can only be 1 or 0
        if (countBookings[0][0].equals("1")) {
            return true;
        }
        return false;
    }
    public static String[][] findTblID(databaseMethods DBM, String[][] overlappedBookings) {
        String[][] bookedTables = {}; // Inits the array
        if (overlappedBookings.length > 0) { // If there are overlapping bookings this is true
            // Because the amount of bookings that overlap varies, this just creates a dynamic string so that the correct amount of parameters are entered
            String bookedTablesSQL = "SELECT tableID FROM tablesBookingLink WHERE bookingID IN (";
            Object[] bookedTablesParams = {};
            for (int i = 0; i < overlappedBookings.length; i++) { // Loops through to add the right amount of parameters
                bookedTablesParams[i] = overlappedBookings[i][0];
                bookedTablesSQL += "?";
                if (i != overlappedBookings.length - 1) { // The last parameter does not need a comma, this would give an error
                    bookedTablesSQL += ",";
                }
            }
            bookedTablesSQL += ") SORT BY ASC";
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
}
