package rs.cs.restaurantnea.customerArea;

import rs.cs.restaurantnea.IOData.databaseMethods;
import rs.cs.restaurantnea.Search;
import rs.cs.restaurantnea.User;

import static rs.cs.restaurantnea.bookingController.user;

public class viewBookings {
    public static String[][] initViewBookings() {
        String[][] customerBookings = getBookingsData(user);
        return customerBookings;
    }
    public static String[][] getBookingsData(User user) {
        databaseMethods DBM = new databaseMethods();
        Object[] bookingParams = {user.getCustomerID()};
        String[][] customerBookings = DBM.getData("SELECT bookingName, Day, Time, amountOfPeople, bookingID FROM bookings WHERE custID = ? ORDER BY Day DESC", bookingParams);
        return customerBookings;
    }
    public static String[][] getFilteredData(Search search) {
        databaseMethods DBM = new databaseMethods();
        String sql = "SELECT bookingName, Day, Time, amountOfPeople, bookingID FROM bookings WHERE custID = ?";
        Object[] params = {"InvalidParam"};
        if (search.getText().length() != 0) {
            switch (search.getSortBy()) {
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
            params = new Object[] {user.getCustomerID(), "%" + search.getText() + "%" };
        }
        switch (search.getFilter()) {
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
        if (params[0].equals("InvalidParam")) {
            params[0] = user.getCustomerID();
        }
        String[][] customerBookings = DBM.getData(sql, params);
        return customerBookings;
    }
}
