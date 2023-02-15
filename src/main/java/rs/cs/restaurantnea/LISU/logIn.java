package rs.cs.restaurantnea.LISU;

import javafx.scene.control.Alert;
import rs.cs.restaurantnea.general.IOData.cryptMethods;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.errorMethods;
import rs.cs.restaurantnea.general.objects.User;
import rs.cs.restaurantnea.general.objects.tempUser;

import java.util.Base64;

public class logIn {
    //Method to check login details
    public static User loginCheck(tempUser tempUser) {
        // Defines some objects used throughout the method
        cryptMethods CM = new cryptMethods();
        databaseMethods DBM = new databaseMethods();
        Alert alert = new Alert(Alert.AlertType.WARNING);

        tempUser = hashData(CM, tempUser); // Hashes data

        if (checkAdmin(DBM, tempUser)) {
            Object[] param = {tempUser.getHashedEmail()};
            String[][] adminDetails = DBM.getData("SELECT password, IV FROM users WHERE hashedEmails = ?", param);
            adminDetails[0][0] = CM.decrypt(adminDetails[0][0], tempUser.getHashedEmail(), Base64.getDecoder().decode(adminDetails[0][1]));
            if (tempUser.getPassword().equals(adminDetails[0][0])) {
                errorMethods.premadeAlertErrors(alert, "You have logged in!", "All information was correct").show();
                return new User(-1,null,null,null,null,1,null,null,-1,null,-1);
            }
            errorMethods.premadeAlertErrors(alert, "Password incorrect", "Please try again").show();
            return null;
        } else {
            try {
                String[][] accountInfo = findUserData(DBM, tempUser); // Collects specific user data

                if (!checkUserExists(alert, accountInfo)) { // Checks if user exists
                    return null;
                }

                for (int j = 1; j < 5; j++) { // Decrypts all data that needs decrypting
                    accountInfo[0][j] = CM.decrypt(accountInfo[0][j], tempUser.getHashedEmail(), Base64.getDecoder().decode(accountInfo[0][7]));
                }

                if (!checkPassword(alert, accountInfo[0], tempUser)) { // Checks if password correct
                    return null;
                }
                return loginSuccess(alert, accountInfo[0]);
            } catch (Exception e) {
                errorMethods.defaultErrors(e);
                return null;
            }
        }
    }
    public static boolean checkAdmin(databaseMethods DBM, tempUser tempUser) {
        Object[] param = {tempUser.getHashedEmail()};
        String[][] accountType = DBM.getData("SELECT accountType FROM users WHERE hashedEmails = ?", param);
        if (accountType.length > 0) {
            if (accountType[0][0].equals("1")) {
                return true;
            }
        }
        return false;
    }
    public static tempUser hashData(cryptMethods CM, tempUser tempUser) {
        // Sets some data to the hashed values they are supposed to be
        tempUser.setHashedEmail(CM.hashing(tempUser.getEmail()));
        tempUser.setPassword(CM.hashing(tempUser.getPassword()));
        return tempUser;
    }

    public static String[][] findUserData(databaseMethods DBM, tempUser tempUser) {
        Object[] param = {tempUser.getHashedEmail()}; // Sets the parameter for the SQL query
        // Returns specific user data as a 1d array
        return DBM.getData("SELECT users.userID, users.FName, users.LName, users.email, users.password, users.accountType, users.hashedEmails, users.IV, customers.custID, customers.promoEmails, customers.memberPoints FROM users, customers WHERE users.userID = customers.userID AND users.hashedEmails = ? LIMIT 1", param);
    }

    public static boolean checkUserExists(Alert alert, String[][] accountInfo) {
        if (accountInfo.length == 0) { // If no records exist with the email the user provided, an error message is displayed and they stay on the same page
            alert = errorMethods.premadeAlertErrors(alert, "Account does not exist", "Please try again");
            alert.show();
            return false;
        }
        return true;
    }

    public static boolean checkPassword(Alert alert, String[] accountInfo, tempUser tempUser) {
        if (!accountInfo[4].equals(tempUser.getPassword())) { // If the password is incorrect an error message is displayed and they stay on the same page
            alert = errorMethods.premadeAlertErrors(alert, "Password incorrect", "Please try again");
            alert.show();
            return false;
        }
        return true;
    }

    public static User loginSuccess(Alert alert, String[] accountInfo) {
        User user = new User(Integer.parseInt(accountInfo[0]), accountInfo[1], accountInfo[2], accountInfo[3], accountInfo[4], Integer.parseInt(accountInfo[5]), accountInfo[6], accountInfo[7], Integer.parseInt(accountInfo[8]), accountInfo[9], Integer.parseInt(accountInfo[10])); // The user's information is saved as a User object
        alert = errorMethods.premadeAlertErrors(alert, "You have logged in!", "All information was correct"); // A success message is displayed
        alert.show();
        return user;
    }
}
