package rs.cs.restaurantnea.adminArea;

import rs.cs.restaurantnea.general.IOData.cryptMethods;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.objects.Search;

import java.util.Base64;

public class viewUsers {
    public static String[][] initViewUsers() {
        String[][] userDetails = getUserDetails();

        return userDetails;
    }
    public static String[][] getUserDetails() {
        databaseMethods DBM = new databaseMethods();
        cryptMethods CM = new cryptMethods();

        // Gets the user data
        String[][] userDetails = DBM.getData("SELECT users.userID, users.FName, users.LName, users.email, users.accountType, users.hashedEmails, users.IV, customers.promoEmails, customers.memberPoints FROM users, customers WHERE users.userID = customers.customerID ORDER BY users.userID ASC", new Object[] {});
        for (int i = 1; i < 4; i++) {
            userDetails[0][i] = CM.decrypt(userDetails[0][i], userDetails[0][5], Base64.getDecoder().decode(userDetails[0][6])); // Decrypts the user data
        }
        return userDetails;
    }
    public static String[][] getFilteredData(Search search) {
        databaseMethods DBM = new databaseMethods();
        cryptMethods CM = new cryptMethods();

        String sql = "SELECT users.userID, users.FName, users.LName, users.email, users.hashedEmails, users.IV, customers.promoEmails, customers.memberPoints FROM users, customers WHERE users.userID = customers.userID AND users.accountType = 2";
        Object[] params = {};
        if (search.getText().length() != 0) {
            sql = createFilteredData(sql, search); // Creates the filtered sql query
            params = new Object[] {"%" + search.getText() + "%"}; // Creates the params to find what the user searched
        }
        sql = createSortedSQL(sql, search); // Creates the ordered sql query
        String[][] userDetails = DBM.getData(sql, params); // Gets the user details
        for (int i = 0; i < userDetails.length; i++) {
            for (int j = 1; j < 4; j++) {
                userDetails[i][j] = CM.decrypt(userDetails[i][j], userDetails[i][4], Base64.getDecoder().decode(userDetails[i][5])); // Decrypts the user data
            }
        }
        return userDetails;
    }
    public static String createFilteredData(String sql, Search search) {
        switch (search.getSortBy()) { // Finds the corresponding search to add to the query
            case "User ID":
                sql += " AND users.userID = ?";
                break;
            case "First Name":
                sql += " AND users.FName = ?";
                break;
            case "Last Name":
                sql += " AND users.LName = ?";
                break;
            case "Email Address":
                sql += " AND users.email = ?";
                break;
            case "Promo Emails":
                sql += " AND customers.promoEmails = ?";
                break;
            case "memberPoints":
                sql += " AND customers.memberPoints = ?";
                break;
        }
        return sql;
    }
    public static String createSortedSQL(String sql, Search search) {
        switch (search.getFilter()) { // Finds the corresponding order to add to the query
            case "User ID: Lowest to Highest":
                sql += " ORDER BY users.userID ASC";
                break;
            case "User ID: Highest to Lowest":
                sql += " ORDER BY users.userID DESC";
                break;
            case "Member Points: Lowest to Highest":
                sql += " ORDER BY customers.memberPoints ASC";
                break;
            case "Member Points: Highest to Lowest":
                sql += " ORDER BY customers.memberPoints DESC";
                break;
        }
        return sql;
    }
}
