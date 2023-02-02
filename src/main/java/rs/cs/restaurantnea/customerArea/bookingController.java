package rs.cs.restaurantnea.customerArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import rs.cs.restaurantnea.general.IOData.databaseMethods;
import rs.cs.restaurantnea.customerArea.CUDBookings;
import rs.cs.restaurantnea.customerArea.generalCustomerMethods;
import rs.cs.restaurantnea.customerArea.viewBookings;
import rs.cs.restaurantnea.general.objects.Booking;
import rs.cs.restaurantnea.general.objects.Search;
import rs.cs.restaurantnea.general.objects.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class bookingController {
    @FXML
    private TextField nameInput;
    @FXML
    private DatePicker dateInput;
    @FXML
    private ChoiceBox timeInput;
    @FXML
    private ChoiceBox amtPplInput;
    @FXML
    private ChoiceBox typeInput;
    @FXML
    private ChoiceBox filterInput;
    @FXML
    private TextField searchInput;
    @FXML
    private ChoiceBox sortByInput;
    @FXML
    private TableView<Booking> tableOutput;
    @FXML
    private TableColumn<Booking, String> nameCol;
    @FXML
    private TableColumn<Booking, LocalDate> dateCol;
    @FXML
    private TableColumn<Booking, String> timeCol;
    @FXML
    private TableColumn<Booking, Integer> amtPplCol;
    @FXML
    private TableColumn<Booking, Integer> bookingIDCol;
    @FXML
    private TextField bookingIDInput;
    @FXML
    private TextField bookingNameInput;
    @FXML
    private DatePicker bookingDateInput;
    @FXML
    private ChoiceBox bookingTimeInput;
    @FXML
    private ChoiceBox bookingAmtPplInput;
    @FXML
    private Button saveButton;
    @FXML
    private Button delButton;
    public static User user;

    public void signOut(ActionEvent event) {
        generalCustomerMethods.signOut(event);
    }
    public void toAccount(ActionEvent event) {
        generalCustomerMethods.toAccount(event);
    }
    public void toOrders(ActionEvent event) {
        generalCustomerMethods.toOrders(event);
    }
    public void toMenuInfo(ActionEvent event) {
        generalCustomerMethods.toMenuInfo(event);
    }
    public void makeBooking(ActionEvent event) {
        int amtPpl = 0; // Inits the amount of people
        if (String.valueOf(amtPplInput.getValue()).equals("Big table (more than 20 people)")) {
            amtPpl = 21; // To prevent the choicebox from being infinite, by setting 21+ ppl to just 21 allows the restaurant to just prepare for a large table, they can always add more small tables if more space is needed
        }
        else {
            amtPpl = Integer.parseInt(String.valueOf(amtPplInput.getValue()));
        }
        Booking newBooking = new Booking(nameInput.getText(), dateInput.getValue(), String.valueOf(timeInput.getValue()), amtPpl,String.valueOf(typeInput.getValue()), user, -1, -1); // Creates a temp booking obj
        Alert alert = CUDBookings.makeBooking(newBooking); // Attempts to make the booking
        alert.show();
    }
    public void findBooking(ActionEvent event) {
        try {
            databaseMethods DBM = new databaseMethods();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Inits the date formatter, formats String to LocalDate
            Object[] params = {Integer.parseInt(bookingIDInput.getText()), user.getCustomerID()};
            String[][] bookingDetails = DBM.getData("SELECT bookingName, Day, Time, amountOfPeople FROM bookings WHERE bookingID = ? AND custID = ?", params); // Selects the user specific booking data
            if (bookingDetails.length != 0) { // If the booking exists
                setAbility(false); // Enables the other inputs
                bookingNameInput.setText(bookingDetails[0][0]); // These set the values of the inputs to the values of the booking the user wants to edit
                bookingDateInput.setValue(LocalDate.parse(bookingDetails[0][1], formatter));
                if (bookingDetails[0][2].length() == 1) {
                    bookingTimeInput.setValue("0" + bookingDetails[0][2] + ":00");
                } else {
                    bookingTimeInput.setValue(bookingDetails[0][2] + ":00");
                }
                bookingAmtPplInput.setValue(Integer.parseInt(bookingDetails[0][3]));
            } else {
                setAbility(true); // Disables the other inputs
            }
        } catch (Exception e) {
            setAbility(true); // Disables the other inputs
        }
    }
    public void setAbility(boolean disabled) { // Dis/Enables the inputs to edit a booking
        saveButton.setDisable(disabled);
        delButton.setDisable(disabled);
        bookingNameInput.setDisable(disabled);
        bookingAmtPplInput.setDisable(disabled);
        bookingDateInput.setDisable(disabled);
        bookingTimeInput.setDisable(disabled);
    }
    public void updateBookings(ActionEvent event) {
        Booking booking = new Booking(bookingNameInput.getText(), bookingDateInput.getValue(), String.valueOf(bookingTimeInput.getValue()).substring(0,2), Integer.parseInt(String.valueOf(bookingAmtPplInput.getValue())), null, user, -1, Integer.parseInt(bookingIDInput.getText()));
        boolean accountDeleted = CUDBookings.updateBookings(booking); // Attempts to update the booking
        if (accountDeleted) {
            setAbility(true); // Disables the input to edit a booking
        }
    }
    public void deleteBooking(ActionEvent event) {
        CUDBookings.deleteBookingData(Integer.parseInt(bookingIDInput.getText())); // Attempts to delete the booking
        setAbility(true); // Disables the input to edit a booking
    }
    public void updateViewableBookings(ActionEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Inits the date formatter, formats String to LocalDate
        Search search = new Search(searchInput.getText(), String.valueOf(filterInput.getValue()), String.valueOf(sortByInput.getValue())); // Creates an object that holds all the search info
        String[][] customerBookings = viewBookings.getFilteredData(search); // Collects all the filtered data in the order selected
        tableOutput.getItems().clear(); // Clears the table so just the filtered data is displayed
        for (String[] row:customerBookings) { // Loops through the data to add to the table
            tableOutput.getItems().add(new Booking(row[0], LocalDate.parse(row[1],formatter), row[2], Integer.parseInt(row[3]), null, null, -1, Integer.parseInt(row[4])));
        }
    }
    public static void getData(User getUser) {
        user = getUser;
    }
    public void initialize() { // Inits the scene
        timeChoiceBoxInit();
        amtPplChoiceBoxInit();
        typeChoiceBoxInit();
        filterChoiceBoxInit();
        sortByChoiceBoxInit();
        cellFactoryInit();
        tableInit();
    }

    public void tableInit() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Inits the date formatter, formats String to LocalDate
        String[][] customerBookings = viewBookings.initViewBookings(); // Gets the customers bookings
        for (String[] row:customerBookings) { // Adds data to the table
            tableOutput.getItems().add(new Booking(row[0], LocalDate.parse(row[1],formatter), row[2], Integer.parseInt(row[3]), null, null, -1, Integer.parseInt(row[4])));
        }
    }
    public void timeChoiceBoxInit() { // Adds values to the time choice box
        for (int i = 9; i < 21; i++) {
            if (i < 10) {
                timeInput.getItems().add("0" + i + ":00");
                bookingTimeInput.getItems().add("0" + i + ":00");

            }
            else {
                timeInput.getItems().add(i + ":00");
                bookingTimeInput.getItems().add(i + ":00");
            }
        }
        timeInput.setValue("09:00");
    }
    public void amtPplChoiceBoxInit() { // Adds values to the amount of ppl choice box
        for (int i = 1; i < 21; i++) {
            amtPplInput.getItems().add(i);
            bookingAmtPplInput.getItems().add(i);
        }
        amtPplInput.getItems().add("Big table (more than 20 people)");
        bookingAmtPplInput.getItems().add("Big table (more than 20 people)");
        amtPplInput.setValue(1);
    }
    public void typeChoiceBoxInit() { // Adds values to the event type choice box
        String[] eventType = {"General Eating", "School Trip", "Business Meal", "Birthday Meal", "Other"};
        for (String event:eventType) {
            typeInput.getItems().add(event);
        }
        typeInput.setValue("General Eating");
    }
    public void filterChoiceBoxInit() { // Adds values to the filter choice box
        String[] filterItems = {"Date: Latest - Earliest", "Date: Earliest - Latest", "Amount of people: Least - Most", "Amount of people: Most - Least"};
        for (String item:filterItems) {
            filterInput.getItems().add(item);
        }
        filterInput.setValue("Date: Latest - Earliest");
    }
    public void sortByChoiceBoxInit() { // Adds values to the sort by choice box
        String[] sortItems = {"Name", "Date", "Time", "Amount of people", "Booking ID"};
        for (String item:sortItems) {
            sortByInput.getItems().add(item);
        }
        sortByInput.setValue("Date");
    }
    public void cellFactoryInit() { // Creates cell factories to allow data to be added to columns
        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("Time"));
        amtPplCol.setCellValueFactory(new PropertyValueFactory<>("AmtPpl"));
        bookingIDCol.setCellValueFactory(new PropertyValueFactory<>("BookingID"));
    }
}