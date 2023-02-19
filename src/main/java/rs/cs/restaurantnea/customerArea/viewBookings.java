package rs.cs.restaurantnea.customerArea;

import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.objects.Search;
import rs.cs.restaurantnea.general.objects.User;

import static rs.cs.restaurantnea.customerArea.bookingController.user;

public class viewBookings {
    public static String[][] initViewBookings() {
        String[][] customerBookings = getBookingsData(user); // Gets booking data and returns it
        return customerBookings;
    }
    public static String[][] getBookingsData(User user) {
        databaseMethods DBM = new databaseMethods();
        Object[] bookingParams = {user.getCustomerID()}; // Creates the parameters to find one customers booking data
        String[][] customerBookings = DBM.getData("SELECT bookingName, Day, Time, amountOfPeople, bookingID FROM bookings WHERE custID = ? ORDER BY Day DESC", bookingParams);
        return customerBookings; // Returns booking data
    }
    public static String[][] getFilteredData(Search search) {
        databaseMethods DBM = new databaseMethods();
        String sql = "SELECT bookingName, Day, Time, amountOfPeople, bookingID FROM bookings WHERE custID = ?"; // Creates the base sql query
        Object[] params = {"InvalidParam"}; // Inits the params array, InvalidParam is so the program can check whether the array is empty or not
        if (search.getText().length() != 0) { // If the user has searched something, the search query is added
            sql = createFilteredSQL(sql, search);
            params = new Object[] {user.getCustomerID(), "%" + search.getText() + "%" }; // Creates the parameters for the search query
        }
        sql = createSortedSQL(sql, search); // If the data is to be in any specific order, this adds it to the SQL query
        if (params[0].equals("InvalidParam")) { // If the parameter array has no valid parameters, new params are added
            params[0] = user.getCustomerID();
        }
        String[][] customerBookings = DBM.getData(sql, params); // Gets the filtered user data
        return customerBookings;
    }
    public static String createFilteredSQL(String sql, Search search) {
        switch (search.getSortBy()) { // Searches through the valid filters, adds the corresponding filter to the SQL query
            case "Name":
                sql += " AND bookingName LIKE ?";
                break;
            case "Date":
                sql += " AND Day LIKE ?";
                break;
            case "Time":
                sql += " AND Time LIKE ?";
                break;
            case "Amount of people":
                sql += " AND amountOfPeople LIKE ?";
                break;
            case "Booking ID":
                sql += " AND bookingID LIKE ?";
                break;
        }
        return sql;
    }
    public static String createSortedSQL(String sql, Search search) {
        switch (search.getFilter()) { // Searches through the valid ways of ordering the data, adds the corresponding order to the SQL query
            case "Date: Earliest - Latest":
                sql += " ORDER BY Day ASC";
                break;
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
