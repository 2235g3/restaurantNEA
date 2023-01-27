package rs.cs.restaurantnea.LISU;

import javafx.scene.control.Alert;
import rs.cs.restaurantnea.IOData.cryptMethods;
import rs.cs.restaurantnea.IOData.databaseMethods;
import rs.cs.restaurantnea.User;
import rs.cs.restaurantnea.general.errorMethods;
import rs.cs.restaurantnea.general.regExMatchers;

import java.util.regex.Matcher;

import static rs.cs.restaurantnea.general.errorMethods.SUInvalidInputs;

public class signUp {
    public static Alert signUpCheck(User user, Alert alert) {
        // Defines some objects used throughout the method
        cryptMethods CM = new cryptMethods();
        databaseMethods DBM = new databaseMethods();

        if (regExCheck(user, alert) != alert || user.getFName().length() < 3 || user.getLName().length() < 3 || user.getPromoEmails().equals("null")) { // Ensures valid data is entered
            return SUInvalidInputs(alert); // If any invalid data is entered, an error message is displayed
        }

        // Sets some data to the hashed values they are supposed to be
        user.setHashedEmail(CM.hashing(user.getEmail()));
        user.setPassword(CM.hashing(user.getPassword()));

        if (DBM.getData("SELECT email FROM users WHERE hashedEmails = ?", new Object[] {user.getHashedEmail()}).length != 0) { // Checks the users table to check whether an account with the same email doesn't already exist
            return errorMethods.SUAccountExists(alert); // Displays an error message
        }

        databaseInsert(user,CM,DBM,new Object[] {user.getHashedEmail()}); // Inserts the user's data into the database
        return errorMethods.SUSuccess(alert); // Displays a success alert
    }

    public static Alert regExCheck(User user, Alert alert) {
        // These create a matcher for each regular expression
        Matcher emailMatcher = regExMatchers.createEmailMatcher(user);
        Matcher passMatcher = regExMatchers.createPasswordMatcher(user);
        Matcher fNameMatcher = regExMatchers.createNameMatcher(user.getFName());
        Matcher lNameMatcher = regExMatchers.createNameMatcher(user.getLName());

        if (!emailMatcher.matches() || !passMatcher.matches() || !fNameMatcher.matches() || lNameMatcher.matches()) { // If the input is invalid, an error alert is displayed
            return SUInvalidInputs(alert);
        }
        return alert;
    }

    private static String[] encryptData(cryptMethods CM, User user) {
        String[] userDataArr = user.getList(); // The user's data is converted into an array

        CM.initKeys(); // The keys to encrypt are initialised
        CM.generateIV(); // The Initialization Vector (IV) is generated
        for (int i = 1; i < 5; i++) { // Loops through data to be encrypted
            userDataArr[i] = CM.encrypt(userDataArr[i], user.getHashedEmail(), true, CM.IV); // Encrypts the data
        }
        return userDataArr; // Returns the updated data to be inserted into the database
    }

    public static void databaseInsert(User user, cryptMethods CM, databaseMethods DBM, Object[] param) {
        String[] userDataArr = encryptData(CM, user); // Encrypts the data

        String IV = java.util.Base64.getEncoder().encodeToString(CM.iv); // Converts the IV from a Byte[] to a string

        Object[] usersQueryParam = {userDataArr[1], userDataArr[2], userDataArr[3], userDataArr[4], userDataArr[5], userDataArr[6], IV}; // Creates an array of parameters. The items are objects so that items can have different data types and be stored in the same array.

        DBM.CUDData("INSERT INTO users(FName, LName, email, password, accountType, hashedEmails, IV) VALUES(?,?,?,?,?,?,?)", usersQueryParam); // Inserts data into the users table

        int ID = Integer.parseInt(DBM.getData("SELECT userID FROM users WHERE hashedEmails = ?", param)[0][0]); // Gets the userID of the new user from the users table so that the userID can be stored in the customers table

        Object[] customersQueryParam = {ID, user.getPromoEmails(), 0, 0}; // Creates another array to be stored as an object array.
        DBM.CUDData("INSERT INTO customers(userID, promoEmails, memberPoints, totalAmountSpent) VALUES(?,?,?,?)", customersQueryParam); // Inserts the data into the customers table
    }
}
