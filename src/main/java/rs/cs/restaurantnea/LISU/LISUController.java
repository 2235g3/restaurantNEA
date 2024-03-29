package rs.cs.restaurantnea.LISU;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import rs.cs.restaurantnea.GeneralController;
import rs.cs.restaurantnea.customerArea.accountController;
import rs.cs.restaurantnea.general.objects.User;
import rs.cs.restaurantnea.general.objects.tempUser;

public class LISUController {

    @FXML
    private TextField emailInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private TextField passwordShownInput;
    @FXML
    private TextField FNameInput;
    @FXML
    private TextField LNameInput;
    @FXML
    private ChoiceBox promoEmailsInput;
    private boolean clicked = false;

    // These methods deal with scene switching on an event
    public void backToHome(ActionEvent event) {
        new GeneralController().selectNewScene("welcomePage.fxml", event);
    }
    public void toCustAccount(ActionEvent event) {
        new GeneralController().selectNewScene("custAccount.fxml", event);
    }
    public void toAdminBookings(ActionEvent event) {
        new GeneralController().selectNewScene("adminUsers.fxml", event);
    }
    public void showHidePass(ActionEvent event) {
        clicked = new generalLISUMethods().showHidePass(passwordInput, passwordShownInput, clicked);
    }

    // This method is the parent to the logIn method, this is just a controller method to deal with logging in
    public void loginCheck(ActionEvent event) {
        tempUser tempLIUser = new tempUser(-1, null, null, emailInput.getText(), getUpdatedPassword(), -1, null);
        User user = new logIn().loginCheck(tempLIUser);
        if (user != null) { // If login was successful the user is taken to their account page, if unsuccessful null is returned
            if (user.getAccountType() == 2) {
                accountController AC = new accountController();
                AC.getData(user); // Sends user data to the next controller
                toCustAccount(event);
            }
            else {
                toAdminBookings(event);
            }
        }
    }

    // This method is the parent to the signUp method, this is just a controller method to deal with signing up
    public void signUpCheck(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING); // Sets up a default alert
        User tempSUUser = new User(-1, FNameInput.getText(), LNameInput.getText(), emailInput.getText(), getUpdatedPassword(), 2, null, null,  -1, String.valueOf(promoEmailsInput.getValue()) , -1);
        alert = new signUp().signUpCheck(tempSUUser, alert);
        alert.show();
    }

    // This method returns the password that the user is typing in, depending on whether the password is shown or not
    public String getUpdatedPassword() {
        if (clicked) {
            return passwordShownInput.getText();
        }
        else {
            return passwordInput.getText();
        }
    }

    // This method adds items to the ChoiceBox promoEmailsInput
    public void initialize() {
        String[] promoEmailsItems = {"Never", "Yearly", "Monthly", "Weekly"};
        for (String item:promoEmailsItems) {
            promoEmailsInput.getItems().add(item);
        }
        promoEmailsInput.setValue("Never");
    }
}
