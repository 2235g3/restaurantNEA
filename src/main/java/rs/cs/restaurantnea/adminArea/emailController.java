package rs.cs.restaurantnea.adminArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import rs.cs.restaurantnea.general.IOData.emailMethods;
import rs.cs.restaurantnea.general.objects.Email;

public class emailController {
    @FXML
    private ChoiceBox frequencyInput;
    @FXML
    private TextField subjectInput;
    @FXML
    private HTMLEditor contentsInput;
    public void signOut(ActionEvent event) {
        generalAdminMethods.signOut(event);
    }
    public void toBookings(ActionEvent event) {
        generalAdminMethods.toBookings(event);
    }
    public void sendEmail(ActionEvent event) {
        Email email = new Email("restaurantNEAManager@gmail.com", null, subjectInput.getText(), contentsInput.getHtmlText(), String.valueOf(frequencyInput.getValue()));
        emailMethods.sendEmails(email);
    }
    public void initialize() {
        frequencyInput.getItems().add("All Users");
        frequencyInput.getItems().add("Yearly");
        frequencyInput.getItems().add("Monthly");
        frequencyInput.getItems().add("Weekly");
        frequencyInput.setValue("Weekly");
    }
}
