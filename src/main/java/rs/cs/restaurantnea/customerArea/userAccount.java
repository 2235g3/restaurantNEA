package rs.cs.restaurantnea.customerArea;

import javafx.scene.control.Alert;
import rs.cs.restaurantnea.general.IOData.cryptMethods;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.objects.User;
import rs.cs.restaurantnea.general.regExMatchers;

import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.util.regex.Matcher;

import static rs.cs.restaurantnea.general.errorMethods.*;

public class userAccount {
    public static User saveUserData(User user, String[] inputFields) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (regExCheck(alert, user) != alert || inputFields[0].length() < 3 || inputFields[1].length() < 3 || inputFields[2].equals("null")) { // Checks if the inputted values are valid
            alert = UAInvalidInputs(alert);
            alert.show();
            return user;
        }
        String[] encryptedFields = {inputFields[0],inputFields[1],inputFields[2]};
        encryptedFields = encryptUserData(user, encryptedFields); // Encrypts the data that is to be updated
        updateUserData(user, encryptedFields); // Updates the data
        return saveSuccess(user, inputFields, alert); // Returns the new user object
    }
    public static Alert regExCheck(Alert alert, User user) {
        Matcher fNameMatcher = regExMatchers.createNameMatcher(user.getFName());
        Matcher lNameMatcher = regExMatchers.createNameMatcher(user.getLName());
        if (!fNameMatcher.matches() || !lNameMatcher.matches()) { // Checks that the inputted names match the regex
            return UAInvalidInputs(alert);
        }
        return alert;
    }
    public static void updateUserData(User user, String[] inputFields) {
        databaseMethods DBM = new databaseMethods();
        Object[] usersParams = {inputFields[0], inputFields[1], user.getUserID()};
        Object[] custParams = {inputFields[2], user.getCustomerID()};
        DBM.CUDData("UPDATE users SET FName = ?, LName = ? WHERE userID = ?", usersParams);
        DBM.CUDData("UPDATE customers SET promoEmails = ? WHERE custID = ?", custParams); // Updates the users' data
    }
    public static String[] encryptUserData(User user, String[] fieldsToEncrpyt) {
        cryptMethods CM = new cryptMethods();
        for (int i = 0; i < 2; i++) {
            fieldsToEncrpyt[i] = CM.encrypt(fieldsToEncrpyt[i], user.getHashedEmail(), false, new IvParameterSpec(Base64.getDecoder().decode(user.getIV()))); // Encrypts the data
        }
        return fieldsToEncrpyt;
    }
    public static User saveSuccess(User user, String[] inputFields, Alert alert) {
        user.setFName(inputFields[0]);
        user.setLName(inputFields[1]);
        user.setPromoEmails(inputFields[2]); // Saves the data into the user obj
        UASaveSuccess(alert, inputFields).show();
        return user;
    }
    public static void deleteAccount(User user) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        if (UADeleteConfirmation().isPresent()) { // Confirms the user wants to delete their account to prevent misclicks
            deleteData(user); // Deletes the database data
            deleteKeyEntry(user); // Deletes the key entry for that user
            UADeleteWarning(alert).show();
        }
        else {
            UAAccountExists(alert).show();
        }
    }
    public static void deleteData(User user) {
        databaseMethods DBM = new databaseMethods();
        Object[] userParams = {user.getUserID()};
        DBM.CUDData("DELETE FROM users WHERE userID = ?", userParams); // Deletes data from the user table
        Object[] custParams = {user.getCustomerID()};
        DBM.CUDData("DELETE FROM customers WHERE custID = ?", custParams); // Deletes data from the customer table
        String[][] userBookings = DBM.getData("SELECT bookingID FROM bookings WHERE custID = ?", custParams); // Selects the users' bookings
        Object[] linkParams = new Object[userBookings.length];
        String sql = "DELETE FROM tablesBookingLink WHERE bookingID IN ("; // Creates the base sql query
        for (int i = 0; i < userBookings.length; i++) { // Adds the params and expands the query to add more params
            linkParams[i] = userBookings[i][0];
            sql += "?, ";
        }
        if (userBookings.length > 0) {
            sql = sql.substring(0, sql.length() - 2) + ")"; // Removes the last comma
            DBM.CUDData(sql, linkParams); // Deletes the data from the link table
            DBM.CUDData("DELETE FROM bookings WHERE custID = ?", custParams); // Deletes data from the booking table
        }

    }
    public static void deleteKeyEntry(User user) {
        cryptMethods CM = new cryptMethods();
        CM.deleteKeyEntry(user.getHashedEmail()); // Deletes the key entry
    }
}