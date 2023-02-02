package rs.cs.restaurantnea;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import rs.cs.restaurantnea.IOData.databaseMethods;
import rs.cs.restaurantnea.customerArea.makeBookings;
import rs.cs.restaurantnea.customerArea.generalCustomerMethods;
import rs.cs.restaurantnea.customerArea.viewBookings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
    private Button findButton;
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
        int amtPpl = 0;
        if (String.valueOf(amtPplInput.getValue()).equals("Big table (more than 20 people)")) {
            amtPpl = 21;
        }
        else {
            amtPpl = Integer.parseInt(String.valueOf(amtPplInput.getValue()));
        }
        Booking newBooking = new Booking(nameInput.getText(), dateInput.getValue(), String.valueOf(timeInput.getValue()), amtPpl,String.valueOf(typeInput.getValue()), user, -1, -1);
        Alert alert = makeBookings.makeBooking(newBooking);
        alert.show();
    }
    public void findBooking(ActionEvent event) {
        databaseMethods DBM = new databaseMethods();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Object[] params = {bookingIDInput.getText(), user.getCustomerID()};
        String[][] bookingDetails = DBM.getData("SELECT bookingName, Day, Time, amountOfPeople FROM bookings WHERE bookingID = ? AND custID = ?", params);
        saveButton.setDisable(false);
        delButton.setDisable(false);
        bookingNameInput.setDisable(false);
        bookingAmtPplInput.setDisable(false);
        bookingDateInput.setDisable(false);
        bookingTimeInput.setDisable(false);
        bookingNameInput.setText(bookingDetails[0][0]);
        bookingDateInput.setValue(LocalDate.parse(bookingDetails[0][1], formatter));
        bookingTimeInput.setValue(bookingDetails[0][2]);
        bookingAmtPplInput.setValue(bookingDetails[0][3]);
    }
    public void updateBookings() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        databaseMethods DBM = new databaseMethods();
        Booking booking = new Booking(bookingNameInput.getText(), bookingDateInput.getValue(), String.valueOf(bookingTimeInput.getValue()), Integer.parseInt(String.valueOf(bookingAmtPplInput.getValue())), null, user, -1, Integer.parseInt(bookingIDInput.getText()));
        String[][] potentialTableID = makeBookings.findPotentialTables(DBM, booking); // Finds tables that are big enough to fit the amount of people but are not too big
        String[][] overlappedBookings = makeBookings.findOverlappedBookings(DBM, booking); // Finds bookings at a similar time period to the intended booking
        String[][] bookedTables = makeBookings.findTblID(DBM, overlappedBookings); // Finds all tables that are being used at the time of booking so that there are no tables that get double booked. The average time eating out is around 4 hours

        ArrayList<Integer> availableTables = makeBookings.findAvailableBookings(potentialTableID, bookedTables); // Creates an arraylist of all available tables

        if (availableTables.size() > 0) { // If there is a table available, it gets set into the booking object
            booking.setTableID(availableTables.get(0));
        } else {
            // [TBA]
        }
        DBM.CUDData("UPDATE bookings SET bookingName = ?, Day = ?, Time = ?, amountOfPeople = ? WHERE bookingID = ?");
    }
    public void updateViewableBookings(ActionEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Search search = new Search(searchInput.getText(), String.valueOf(filterInput.getValue()), String.valueOf(sortByInput.getValue()));
        String[][] customerBookings = viewBookings.getFilteredData(search);
        tableOutput.getItems().clear();
        for (String[] row:customerBookings) {
            tableOutput.getItems().add(new Booking(row[0], LocalDate.parse(row[1],formatter), row[2], Integer.parseInt(row[3]), null, null, -1, Integer.parseInt(row[4])));
        }
    }
    public static void getData(User getUser) {
        user = getUser;
    }
    public void initialize() {
        timeChoiceBoxInit();
        amtPplChoiceBoxInit();
        typeChoiceBoxInit();
        filterChoiceBoxInit();
        sortByChoiceBoxInit();
        cellFactoryInit();
        tableInit();
    }

    public void tableInit() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[][] customerBookings = viewBookings.initViewBookings();
        for (String[] row:customerBookings) {
            tableOutput.getItems().add(new Booking(row[0], LocalDate.parse(row[1],formatter), row[2], Integer.parseInt(row[3]), null, null, -1, Integer.parseInt(row[4])));
        }
    }
    public void timeChoiceBoxInit() {
        for (int i = 9; i < 21; i++) {
            if (i < 10) {
                timeInput.getItems().add("0" + i + ":00");
            }
            else {
                timeInput.getItems().add(i + ":00");
            }
        }
        timeInput.setValue("09:00");
    }
    public void amtPplChoiceBoxInit() {
        for (int i = 1; i < 21; i++) {
            amtPplInput.getItems().add(i);
        }
        amtPplInput.getItems().add("Big table (more than 20 people)");
        amtPplInput.setValue(1);
    }
    public void typeChoiceBoxInit() {
        String[] eventType = {"General Eating", "School Trip", "Business Meal", "Birthday Meal", "Other"};
        for (String event:eventType) {
            typeInput.getItems().add(event);
        }
        typeInput.setValue("General Eating");
    }
    public void filterChoiceBoxInit() {
        String[] filterItems = {"Date: Latest - Earliest", "Date: Earliest - Latest", "Amount of people: Least - Most", "Amount of people: Most - Least"};
        for (String item:filterItems) {
            filterInput.getItems().add(item);
        }
        filterInput.setValue("Date: Latest - Earliest");
    }
    public void sortByChoiceBoxInit() {
        String[] sortItems = {"Name", "Date", "Time", "Amount of people", "Booking ID"};
        for (String item:sortItems) {
            sortByInput.getItems().add(item);
        }
        sortByInput.setValue("Date");
    }
    public void cellFactoryInit() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("Time"));
        amtPplCol.setCellValueFactory(new PropertyValueFactory<>("AmtPpl"));
        bookingIDCol.setCellValueFactory(new PropertyValueFactory<>("BookingID"));
    }


}
