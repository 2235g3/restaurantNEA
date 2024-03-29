package rs.cs.restaurantnea.customerArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import rs.cs.restaurantnea.general.objects.User;

public class accountController {
    @FXML
    private TextField fNameField;
    @FXML
    private TextField lNameField;
    @FXML
    private Label custIDLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private ChoiceBox promoEmailsField;
    @FXML
    private Label memberPointsLabel;
    private static User user;

    public static void getData(User getUser) {
        user = getUser;
    }
    public void signOut(ActionEvent event) {
        generalCustomerMethods.signOut(event);
    }
    public void toBookings(ActionEvent event) {
        bookingController BC = new bookingController();
        BC.getData(user);
        generalCustomerMethods.toBookings(event);
    }
    public void toMenuInfo(ActionEvent event) {
        menuInfoController MIC = new menuInfoController();
        MIC.getData(user);
        generalCustomerMethods.toMenuInfo(event);
    }
    public void saveUserData(ActionEvent event) {
        String[] inputFields = {fNameField.getText(), lNameField.getText(), String.valueOf(promoEmailsField.getValue())};
        user = userAccount.saveUserData(user, inputFields);
        displayUserData();
    }
    public void deleteAccount(ActionEvent event) {
        userAccount.deleteAccount(user, event);
    }
    public void addChoiceBoxItems() {
        // These lines add items to the ChoiceBox promoEmailsField
        String[] promoEmailsItems = {"Never", "Yearly", "Monthly", "Weekly"};
        for (String item:promoEmailsItems) {
            promoEmailsField.getItems().add(item);
        }
    }
    public void displayUserData() {
        // These lines set the user data to be displayed except for the password which is always hashed and never saved as a text value
        fNameField.setText(user.getFName());
        lNameField.setText(user.getLName());
        custIDLabel.setText(String.valueOf(user.getCustomerID()));
        emailLabel.setText(user.getEmail());
        promoEmailsField.setValue(user.getPromoEmails());
        memberPointsLabel.setText(String.valueOf(user.getMemberPoints()));
    }
    public void initialize() {
        addChoiceBoxItems();
        displayUserData();
    }
}
