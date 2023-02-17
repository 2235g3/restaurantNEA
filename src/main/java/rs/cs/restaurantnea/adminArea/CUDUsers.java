package rs.cs.restaurantnea.adminArea;

import rs.cs.restaurantnea.customerArea.userAccount;
import rs.cs.restaurantnea.general.IOData.cryptMethods;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.errorMethods;
import rs.cs.restaurantnea.general.objects.User;
import rs.cs.restaurantnea.general.regExMatchers;

import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.util.regex.Matcher;

public class CUDUsers {
    public static boolean regExCheck(User user) {
        Matcher fNameMatcher = regExMatchers.createNameMatcher(user.getFName());
        Matcher lNameMatcher = regExMatchers.createNameMatcher(user.getLName());
        if (!fNameMatcher.matches() || !lNameMatcher.matches()) { // Checks that the inputted names match the regex
            return false;
        }
        return true;
    }
    public static void updateUser(User user) {
        cryptMethods CM = new cryptMethods();
        databaseMethods DBM = new databaseMethods();

        user.setHashedEmail(CM.hashing(user.getEmail()));
        Object[] IVParams = {user.getUserID()};
        user.setIV(DBM.getData("SELECT IV FROM users WHERE userID = ?", IVParams)[0][0]); // Gets the IV from the database to allow encryption
        if (user.getFName().length() < 3 || user.getLName().length() < 3 || regExCheck(user)) { // Checks for invalid inputs
            errorMethods.exceptionErrors("Account was not updated","The first and/or last name is incorrect");
        }

        // Updates the customer data in the database
        user.setFName(CM.encrypt(user.getFName(), user.getHashedEmail(), false, new IvParameterSpec(Base64.getDecoder().decode(user.getIV()))));
        user.setLName(CM.encrypt(user.getLName(), user.getHashedEmail(), false, new IvParameterSpec(Base64.getDecoder().decode(user.getIV()))));
        Object[] updatedUserParams = {user.getFName(), user.getLName(), user.getUserID()};
        DBM.CUDData("UPDATE users SET FName = ?, LName = ? WHERE userID = ?", updatedUserParams);
        Object[] updatedCustomerParams = {user.getPromoEmails(), user.getMemberPoints(), user.getUserID()};
        DBM.CUDData("UPDATE customers SET promoEmails = ?, memberPoints = ? WHERE userID = ?", updatedCustomerParams);
    }
    public static void deleteUser(User user) { // Deletes specific customer data
        databaseMethods DBM = new databaseMethods();

        Object[] getCustIDParams = {user.getUserID()};
        user.setCustomerID(Integer.parseInt(DBM.getData("SELECT custID FROM customers WHERE userID = ?", getCustIDParams)[0][0]));
        userAccount.deleteData(user);
        userAccount.deleteKeyEntry(user);
    }
}
