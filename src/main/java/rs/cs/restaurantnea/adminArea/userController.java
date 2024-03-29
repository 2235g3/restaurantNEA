package rs.cs.restaurantnea.adminArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import rs.cs.restaurantnea.LISU.generalLISUMethods;
import rs.cs.restaurantnea.LISU.signUp;
import rs.cs.restaurantnea.general.IOData.cryptMethods;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.objects.Search;
import rs.cs.restaurantnea.general.objects.User;

import java.util.Base64;
import java.util.Optional;

import static rs.cs.restaurantnea.general.errorMethods.UADeleteConfirmation;
import static rs.cs.restaurantnea.general.errorMethods.exceptionErrors;

public class userController {
    @FXML
    private TableView<User> tableOutput;
    @FXML
    private TextField searchInput;
    @FXML
    private ChoiceBox filterInput;
    @FXML
    private ChoiceBox sortByInput;
    @FXML
    private TableColumn<User, Integer> userIDCol;
    @FXML
    private TableColumn<User, String> fNameCol;
    @FXML
    private TableColumn<User, String> lNameCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, String> promoEmailsCol;
    @FXML
    private TableColumn<User, Integer> memberPointsCol;
    @FXML
    private TextField userIDInput;
    @FXML
    private TextField fNameInput;
    @FXML
    private TextField lNameInput;
    @FXML
    private TextField emailAddressOutput;
    @FXML
    private ChoiceBox promoEmailsInput;
    @FXML
    private TextField memberPointsInput;
    @FXML
    private Button saveButton;
    @FXML
    private Button delButton;
    @FXML
    private TextField fNameField;
    @FXML
    private TextField lNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordShownField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ChoiceBox promoEmailField;
    private boolean clicked = false;

    public void signOut(ActionEvent event) {
        generalAdminMethods.signOut(event);
    }
    public void toBookings(ActionEvent event) {
        generalAdminMethods.toBookings(event);
    }
    public void toEmails(ActionEvent event) {
        generalAdminMethods.toEmails(event);
    }
    public void findUser(ActionEvent event) {
        databaseMethods DBM = new databaseMethods();
        cryptMethods CM = new cryptMethods();

        try {
            if (Integer.parseInt(userIDInput.getText()) > -1 || userIDInput.getText().length() > 0) { // If the user ID was inputted, the if statement is run
                Object[] userParams = {userIDInput.getText()};
                String[][] userDetails = DBM.getData("SELECT users.FName, users.LName, users.email, users.accountType, customers.promoEmails, customers.memberPoints, users.hashedEmails, users.IV FROM users, customers WHERE users.userID = customers.userID AND users.userID = ?", userParams);
                for (int i = 0; i < 3; i++) {
                    userDetails[0][i] = CM.decrypt(userDetails[0][i], userDetails[0][6], Base64.getDecoder().decode(userDetails[0][7])); // Decrypts the user data
                }
                if (userDetails.length > 0) {
                    setAbility(false); // Enables the inputs

                    // Sets the inputs to the user values
                    fNameInput.setText(userDetails[0][0]);
                    lNameInput.setText(userDetails[0][1]);
                    emailAddressOutput.setText(userDetails[0][2]);
                    promoEmailsInput.setValue(userDetails[0][4]);
                    memberPointsInput.setText(userDetails[0][5]);
                } else {
                    setAbility(true); // Disables the inputs
                    exceptionErrors("User not found", "The user ID is not related to any account");
                }
            }
            else {
                exceptionErrors("User not found", "The user ID is not related to any account");
            }
        } catch (Exception e) {
            exceptionErrors("User not found", "The user ID is not related to any account");
        }
    }
    public void setAbility(boolean disabled) { // Toggles whether the inputs are enables or disabled
        saveButton.setDisable(disabled);
        delButton.setDisable(disabled);
        fNameInput.setDisable(disabled);
        lNameInput.setDisable(disabled);
        emailAddressOutput.setDisable(disabled);
        promoEmailsInput.setDisable(disabled);
        memberPointsInput.setDisable(disabled);
    }
    public void updateViewableUsers(ActionEvent event) {
        Search search = new Search(searchInput.getText(), String.valueOf(filterInput.getValue()), String.valueOf(sortByInput.getValue()));
        String[][] userData = viewUsers.getFilteredData(search);
        tableOutput.getItems().clear(); // Clears the table to remove duplicates
        for (String[] user:userData) { // Adds the new data
            tableOutput.getItems().add(new User(Integer.parseInt(user[0]), user[1], user[2], user[3], null, -1, null, null, -1, user[6], Integer.parseInt(user[7])));
        }
    }
    public void updateUser(ActionEvent event) {
        try {
            User selectedUser = new User(Integer.parseInt(userIDInput.getText()), fNameInput.getText(), lNameInput.getText(), emailAddressOutput.getText(), null, -1, null, null, -1, String.valueOf(promoEmailsInput.getValue()), Integer.parseInt(memberPointsInput.getText()));
            CUDUsers.updateUser(selectedUser);
            findUser(event);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void createUser(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING); // Sets up a default alert
        User newUser = new User(-1, fNameField.getText(), lNameField.getText(), emailField.getText(), getUpdatedPassword(), 2, null, null, -1, String.valueOf(promoEmailField.getValue()), -1);
        alert = signUp.signUpCheck(newUser, alert); // Uses the already existing class used when signing up to create new users.
        alert.show();
    }
    public void showHidePass(ActionEvent event) {
        clicked = new generalLISUMethods().showHidePass(passwordField, passwordShownField, clicked);
    }
    public String getUpdatedPassword() {
        if (clicked) {
            return passwordShownField.getText();
        }
        else {
            return passwordField.getText();
        }
    }
    public void deleteUser(ActionEvent event) {
        Optional<ButtonType> confirmation = UADeleteConfirmation();
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
            User selectedUser = new User(Integer.parseInt(userIDInput.getText()), fNameInput.getText(), lNameInput.getText(), emailAddressOutput.getText(), null, -1, null, null, -1, String.valueOf(promoEmailsInput.getValue()), Integer.parseInt(memberPointsInput.getText()));
            CUDUsers.deleteUser(selectedUser); // Deletes the user
            setAbility(true); // Disables the inputs
            exceptionErrors("The account has now been deleted", "This account no longer exists");
        }
        else {
            exceptionErrors("The account has not been deleted", "Please be more careful, customer accounts are valuable");
        }
    }
    public void initialize() {
        filterInputInit();
        sortByInputInit();
        cellFactoryInit();
        promoEmailsInputInit();
        updateViewableUsers(null);
        setAbility(true);
    }
    public void filterInputInit() { // Adds the filter items to the choicebox
        String[] filterItems = {"User ID: Lowest to Highest", "User ID: Highest to Lowest", "Member Points: Lowest to Highest", "Member Points: Highest to Lowest"};
        for (String item:filterItems) {
            filterInput.getItems().add(item);
        }
        filterInput.setValue("User ID: Lowest to Highest");
    }
    public void sortByInputInit() { // Adds the sort by items to the choicebox
        String[] sortByItems = {"User ID", "First Name", "Last Name", "Email Address", "Promo Emails", "Member Points"};
        for (String item:sortByItems) {
            sortByInput.getItems().add(item);
        }
        sortByInput.setValue("User ID");
    }
    public void cellFactoryInit() { // Creates the cell factory for the columns in the table
        userIDCol.setCellValueFactory(new PropertyValueFactory<>("UserID"));
        fNameCol.setCellValueFactory(new PropertyValueFactory<>("FName"));
        lNameCol.setCellValueFactory(new PropertyValueFactory<>("LName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("Email"));
        promoEmailsCol.setCellValueFactory(new PropertyValueFactory<>("PromoEmails"));
        memberPointsCol.setCellValueFactory(new PropertyValueFactory<>("MemberPoints"));
    }
    public void promoEmailsInputInit() { // Adds the promoEmails items to the choicebox
        String[] promoEmailsItems = {"Never", "Yearly", "Monthly", "Weekly"};
        for (String item:promoEmailsItems) {
            promoEmailField.getItems().add(item);
            promoEmailsInput.getItems().add(item);
        }
        promoEmailField.setValue("Never");
    }
}