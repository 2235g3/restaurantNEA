package rs.cs.restaurantnea.customerArea;

import javafx.scene.control.Alert;
import rs.cs.restaurantnea.IOData.cryptMethods;
import rs.cs.restaurantnea.IOData.databaseMethods;
import rs.cs.restaurantnea.User;
import rs.cs.restaurantnea.general.regExMatchers;

import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.util.regex.Matcher;

import static rs.cs.restaurantnea.general.errorMethods.*;

public class userAccount {
    public static User saveUserData(User user, String[] inputFields) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (regExCheck(alert, user) != alert || inputFields[0].length() < 3 || inputFields[1].length() < 3 || inputFields[2].equals("null")) {
            alert = UAInvalidInputs(alert);
            alert.show();
            return user;
        }
        String[] encryptedFields = {inputFields[0],inputFields[1],inputFields[2]};
        encryptedFields = encryptUserData(user, encryptedFields);
        updateUserData(user, encryptedFields);
        return saveSuccess(user, inputFields, alert);
    }
    public static Alert regExCheck(Alert alert, User user) {
        Matcher fNameMatcher = regExMatchers.createNameMatcher(user.getFName());
        Matcher lNameMatcher = regExMatchers.createNameMatcher(user.getLName());
        if (!fNameMatcher.matches() || !lNameMatcher.matches()) {
            return UAInvalidInputs(alert);
        }
        return alert;
    }
    public static void updateUserData(User user, String[] inputFields) {
        databaseMethods DBM = new databaseMethods();
        Object[] usersParams = {inputFields[0], inputFields[1], user.getUserID()};
        Object[] custParams = {inputFields[2], user.getCustomerID()};
        DBM.CUDData("UPDATE users SET FName = ?, LName = ? WHERE userID = ?", usersParams);
        DBM.CUDData("UPDATE customers SET promoEmails = ? WHERE custID = ?", custParams);
    }
    public static String[] encryptUserData(User user, String[] fieldsToEncrpyt) {
        cryptMethods CM = new cryptMethods();
        for (int i = 0; i < 2; i++) {
            fieldsToEncrpyt[i] = CM.encrypt(fieldsToEncrpyt[i], user.getHashedEmail(), false, new IvParameterSpec(Base64.getDecoder().decode(user.getIV())));
        }
        return fieldsToEncrpyt;
    }
    public static User saveSuccess(User user, String[] inputFields, Alert alert) {
        user.setFName(inputFields[0]);
        user.setLName(inputFields[1]);
        user.setPromoEmails(inputFields[2]);
        UASaveSuccess(alert, inputFields).show();
        return user;
    }
    public static void deleteAccount(User user) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        if (UADeleteConfirmation().isPresent()) {
            deleteData(user);
            deleteKeyEntry(user);
            UADeleteWarning(alert).show();
        }
        else {
            UAAccountExists(alert).show();
        }
    }
    public static void deleteData(User user) {
        databaseMethods DBM = new databaseMethods();
        Object[] userParams = {user.getUserID()};
        DBM.CUDData("DELETE FROM users WHERE userID = ?", userParams);
        Object[] custParams = {user.getCustomerID()};
        DBM.CUDData("DELETE FROM customers WHERE custID = ?", custParams);
    }
    public static void deleteKeyEntry(User user) {
        cryptMethods CM = new cryptMethods();
        CM.deleteKeyEntry(user.getHashedEmail());
    }
}