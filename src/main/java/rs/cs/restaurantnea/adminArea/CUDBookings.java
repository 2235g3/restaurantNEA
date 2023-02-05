package rs.cs.restaurantnea.adminArea;

import javafx.scene.control.Alert;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.dataMaintenance;
import rs.cs.restaurantnea.general.errorMethods;
import rs.cs.restaurantnea.general.objects.Booking;

import java.util.ArrayList;

public class CUDBookings {
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
    public static void deleteBookingData(int bookingID) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (errorMethods.EBDeleteConfirmation().isPresent()) { // Runs if the user confirms they want to delete their booking
            databaseMethods DBM = new databaseMethods();
            Object[] deleteParam = {bookingID}; // The only parameter needed is the booking id for both queries
            DBM.CUDData("DELETE FROM bookings WHERE bookingID = ?", deleteParam); // Deletes the actual booking
            DBM.CUDData("DELETE FROM tablesBookingLink WHERE bookingID = ?", deleteParam); // Deletes the bookings' link to its restaurant table
            errorMethods.EBBookingDeleted(alert).show();
        } else {
            errorMethods.EBBookingNotDeleted(alert).show();
        }
    }
    public static boolean updateBookings(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        databaseMethods DBM = new databaseMethods();

        String[][] potentialTableID = CUDBookings.findPotentialTables(DBM, booking); // Finds tables that are big enough to fit the amount of people but are not too big
        String[][] overlappedBookings = CUDBookings.findOverlappedBookings(DBM, booking); // Finds bookings at a similar time period to the intended booking
        String[][] bookedTables = CUDBookings.findTblID(DBM, overlappedBookings); // Finds all tables that are being used at the time of booking so that there are no tables that get double booked. The average time eating out is around 4 hours

        ArrayList<Integer> availableTables = CUDBookings.findAvailableBookings(potentialTableID, bookedTables); // Creates an arraylist of all available tables

        if (availableTables.size() == 0) { // Runs if there are no available tables
            errorMethods.CBFullyBooked(alert).show();
            return false;
        } else { // If there is a table available, it gets set into the booking object and the booking is updated
            booking.setTableID(availableTables.get(0));
            Object[] updateBookingsParams = {booking.getName(), booking.getDate().toString(), Integer.parseInt(booking.getTime()), booking.getAmtPpl(), booking.getBookingID()};
            DBM.CUDData("UPDATE bookings SET bookingName = ?, Day = ?, Time = ?, amountOfPeople = ? WHERE bookingID = ?", updateBookingsParams);
            Object[] updateLinkParams = {booking.getTableID(), booking.getBookingID()};
            DBM.CUDData("UPDATE tablesBookingLink SET tableID = ? WHERE bookingID = ?", updateLinkParams);
            errorMethods.CBBookingSuccess(alert, booking).show();
            return true;
        }
    }
}
