package rs.cs.restaurantnea.adminArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.general.objects.Search;
import rs.cs.restaurantnea.general.objects.User;

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
    private TableColumn<User, Integer> accountTypeCol;
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
    private ChoiceBox accountTypeInput;
    @FXML
    private ChoiceBox promoEmailsInput;
    @FXML
    private TextField memberPointsInput;
    @FXML
    private Button saveButton;
    @FXML
    private Button delButton;

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

        Object[] userParams = {userIDInput.getText()};
        String[][] userDetails = DBM.getData("SELECT users.FName, users.LName, users.email, users.accountType, customers.promoEmails, customers.memberPoints FROM users, customers WHERE users.userID = customers.userID AND users.userID = ?", userParams);
        if (userDetails.length > 0) {
            setAbility(false);
            fNameInput.setText(userDetails[0][0]);
            lNameInput.setText(userDetails[0][1]);
            emailAddressOutput.setText(userDetails[0][2]);
            accountTypeInput.setValue(userDetails[0][3]);
            promoEmailsInput.setValue(userDetails[0][4]);
            memberPointsInput.setText(userDetails[0][5]);
        }
        else {
            setAbility(false);
        }
    }
    public void setAbility(boolean disabled) {
        saveButton.setDisable(disabled);
        delButton.setDisable(disabled);
        fNameInput.setDisable(disabled);
        lNameInput.setDisable(disabled);
        emailAddressOutput.setDisable(disabled);
        accountTypeInput.setDisable(disabled);
        promoEmailsInput.setDisable(disabled);
        memberPointsInput.setDisable(disabled);
    }
    public void updateViewableUsers(ActionEvent event) {
        Search search = new Search(searchInput.getText(), String.valueOf(filterInput.getValue()), String.valueOf(sortByInput.getValue()));
        String[][] userData = null;
        tableOutput.getItems().clear();
        for (String[] user:userData) {
            tableOutput.getItems().add(new User(Integer.parseInt(user[0]), user[1], user[2], user[3], null, Integer.parseInt(user[4]), null, null, -1, user[5], Integer.parseInt(user[6])));
        }
    }
    public void initialize() {
        filterInputInit();
        sortByInputInit();
        cellFactoryInit();
        accountTypeInputInit();
        promoEmailsInputInit();
    }
    public void filterInputInit() {
        String[] filterArr = {"User ID: Lowest to Highest", "User ID: Highest to Lowest", "Member Points: Lowest to Highest", "Member Points: Highest to Lowest"};
        filterInput.getItems().add(filterArr);
        filterInput.setValue("User ID: Lowest to Highest");
    }
    public void sortByInputInit() {
        String[] sortByItems = {"User ID", "First Name", "Last Name", "Email Address", "Account Type", "Promo Emails", "Member Points"};
        sortByInput.getItems().add(sortByItems);
        sortByInput.setValue("User ID");
    }
    public void cellFactoryInit() {
        userIDCol.setCellValueFactory(new PropertyValueFactory<>("UserID"));
        fNameCol.setCellValueFactory(new PropertyValueFactory<>("FName"));
        lNameCol.setCellValueFactory(new PropertyValueFactory<>("LName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("Email"));
        accountTypeCol.setCellValueFactory(new PropertyValueFactory<>("AccountType"));
        promoEmailsCol.setCellValueFactory(new PropertyValueFactory<>("PromoEmails"));
        memberPointsCol.setCellValueFactory(new PropertyValueFactory<>("MemberPoints"));
    }
    public void accountTypeInputInit() {
        int[] accountTypeItems = {1, 2};
        accountTypeInput.getItems().add(accountTypeItems);
    }
    public void promoEmailsInputInit() {
        String[] promoEmailsItems = {"Never", "Yearly", "Monthly", "Weekly"};
        promoEmailsInput.getItems().add(promoEmailsItems);
    }
}