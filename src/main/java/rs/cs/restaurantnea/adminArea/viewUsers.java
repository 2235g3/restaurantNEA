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

        String[][] userDetails = DBM.getData("SELECT users.userID, users.FName, users.LName, users.email, users.accountType, users.hashedEmails, users.IV, customers.promoEmails, customers.memberPoints FROM users, customers WHERE users.userID = customers.customerID ORDER BY users.userID ASC", new Object[] {});
        for (int i = 1; i < 4; i++) {
            userDetails[0][i] = CM.decrypt(userDetails[0][i], userDetails[0][5], Base64.getDecoder().decode(userDetails[0][6]));
        }
        return userDetails;
    }
    public static String[][] getFilteredData(Search search) {
        databaseMethods DBM = new databaseMethods();
        cryptMethods CM = new cryptMethods();

        String sql = "SELECT users.userID, users.FName, users.LName, users.email, users.hashedEmails, users.IV, customers.promoEmails, customers.memberPoints FROM users, customers";
        Object[] params = {};
        if (search.getText().length() != 0) {
            sql = createFilteredData(sql, search);
            params = new Object[] {"%" + search.getText() + "%"};
        }
        sql = createSortedSQL(sql, search);
        String[][] userDetails = DBM.getData(sql, params);
        for (int i = 1; i < 4; i++) {
            userDetails[0][i] = CM.decrypt(userDetails[0][i], userDetails[0][5], Base64.getDecoder().decode(userDetails[0][6]));
        }
        return userDetails;
    }
    public static String createFilteredData(String sql, Search search) {
        switch (search.getSortBy()) {
            case "User ID":
                sql += " WHERE users.userID = ?";
                break;
            case "First Name":
                sql += " WHERE users.FName = ?";
                break;
            case "Last Name":
                sql += " WHERE users.LName = ?";
                break;
            case "Email Address":
                sql += " WHERE users.email = ?";
                break;
            case "Promo Emails":
                sql += " WHERE customers.promoEmails = ?";
                break;
            case "memberPoints":
                sql += " WHERE customers.memberPoints = ?";
                break;
        }
        return sql;
    }
    public static String createSortedSQL(String sql, Search search) {
        switch (search.getFilter()) {
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
