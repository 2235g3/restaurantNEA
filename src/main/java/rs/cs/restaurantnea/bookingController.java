package rs.cs.restaurantnea;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import rs.cs.restaurantnea.customerArea.makeBookings;
import rs.cs.restaurantnea.customerArea.generalCustomerMethods;
import rs.cs.restaurantnea.customerArea.viewBookings;

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
    public static void viewBookings(TextField searchInput, ChoiceBox filterInput, ChoiceBox sortByInput) {
        Search search = new Search(searchInput.getText(), String.valueOf(filterInput.getValue()), String.valueOf(sortByInput.getValue()));
        viewBookings.viewBookings();
    }
    public static void getData(User getUser) {
        user = getUser;
    }
    public void initialize() {
        for (int i = 9; i < 21; i++) {
            if (i < 10) {
                timeInput.getItems().add("0" + i + ":00");
            }
            else {
                timeInput.getItems().add(i + ":00");
            }
        }
        timeInput.setValue("09:00");

        for (int i = 1; i < 21; i++) {
            amtPplInput.getItems().add(i);
        }
        amtPplInput.getItems().add("Big table (more than 20 people)");
        amtPplInput.setValue(1);

        typeInput.getItems().add("General Eating");
        typeInput.getItems().add("School Trip");
        typeInput.getItems().add("Business Meal");
        typeInput.getItems().add("Birthday Meal");
        typeInput.getItems().add("Other");
        typeInput.setValue("General Eating");

        filterInput.getItems().add("Date: Latest - Earliest");
        filterInput.getItems().add("Date: Earliest - Latest");
        filterInput.getItems().add("Amount of people: Least - Most");
        filterInput.getItems().add("Amount of people: Most - Least");
        filterInput.setValue("Date: Latest - Earliest");

        sortByInput.getItems().add("Name");
        sortByInput.getItems().add("Date");
        sortByInput.getItems().add("Time");
        sortByInput.getItems().add("Amount of people");
        sortByInput.getItems().add("Booking ID");
        sortByInput.setValue("Date");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("Time"));
        amtPplCol.setCellValueFactory(new PropertyValueFactory<>("AmtPpl"));
        bookingIDCol.setCellValueFactory(new PropertyValueFactory<>("BookingID"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[][] customerBookings = viewBookings.viewBookings();
        for (String[] row:customerBookings) {
            tableOutput.getItems().add(new Booking(row[0], LocalDate.parse(row[1],formatter), row[2], Integer.parseInt(row[3]), null, null, -1, Integer.parseInt(row[4])));
        }
    }
}
