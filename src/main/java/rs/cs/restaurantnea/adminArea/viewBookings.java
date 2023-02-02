package rs.cs.restaurantnea.adminArea;

import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.objects.Search;
import rs.cs.restaurantnea.general.objects.User;

import static rs.cs.restaurantnea.customerArea.bookingController.user;

public class viewBookings {
    public static String[][] initViewBookings() {
        String[][] customerBookings = getBookingsData(); // Gets booking data and returns it
        return customerBookings;
    }
    public static String[][] getBookingsData() {
        databaseMethods DBM = new databaseMethods();
        String[][] customerBookings = DBM.getData("SELECT bookingName, Day, Time, amountOfPeople, bookingID FROM bookings ORDER BY Day DESC", new Object[] {});
        return customerBookings; // Returns booking data
    }
    public static String[][] getFilteredData(Search search) {
        databaseMethods DBM = new databaseMethods();
        String sql = "SELECT bookingName, Day, Time, amountOfPeople, bookingID FROM bookings"; // Creates the base sql query
        Object[] params = {}; // Inits the params array, InvalidParam is so the program can check whether the array is empty or not
        if (search.getText().length() != 0) { // If the user has searched something, the search query is added
            sql = createFilteredSQL(sql, search);
            params = new Object[] {"%" + search.getText() + "%" }; // Creates the parameters for the search query
        }
        createSortedSQL(sql, search); // If the data is to be in any specific order, this adds it to the SQL query
        String[][] customerBookings = DBM.getData(sql, params); // Gets the filtered user data
        return customerBookings;
    }
    public static String createFilteredSQL(String sql, Search search) {
        switch (search.getSortBy()) { // Searches through the valid filters, adds the corresponding filter to the SQL query
            case "Name":
                sql += " WHERE bookingName LIKE ?";
                break;
            case "Date":
                sql += " WHERE Day LIKE ?";
                break;
            case "Time":
                sql += " WHERE Time LIKE ?";
                break;
            case "Amount of people":
                sql += " WHERE amountOfPeople LIKE ?";
                break;
            case "Booking ID":
                sql += " WHERE bookingID LIKE ?";
                break;
        }
        return sql;
    }
    public static String createSortedSQL(String sql, Search search) {
        switch (search.getFilter()) { // Searches through the valid ways of oredering the data, adds the corresponding order to the SQL query
            case "Date: Latest - Earliest":
                sql += " ORDER BY Day DESC";
                break;
            case "Amount of people: Least - Most":
                sql += " ORDER BY amountOfPeople ASC";
                break;
            case "Amount of people: Most - Least":
                sql += " ORDER BY amountOfPeople DESC";
                break;
        }
        return sql;
    }
}
