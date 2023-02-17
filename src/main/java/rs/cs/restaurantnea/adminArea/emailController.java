package rs.cs.restaurantnea.adminArea;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import rs.cs.restaurantnea.general.IOData.emailMethods;
import rs.cs.restaurantnea.general.errorMethods;
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
    public void toUsers(ActionEvent event) {
        generalAdminMethods.toUsers(event);
    }
    public void sendEmail(ActionEvent event) {
        if (subjectInput.getText().length() > 0 && contentsInput.getHtmlText().length() > 0) { // Checks for a subject and email contents before sending
            Email email = new Email("restaurantNEAManager@gmail.com", null, subjectInput.getText(), contentsInput.getHtmlText(), String.valueOf(frequencyInput.getValue()));
            emailMethods.sendEmails(email);
        }
        else {
            errorMethods.exceptionErrors("One or more inputs invalid", "You must have a subject and a contents of the email.\nThe email was not sent");
        }
    }
    public void initialize() {
        frequencyInput.getItems().add("All Users");
        frequencyInput.getItems().add("Yearly");
        frequencyInput.getItems().add("Monthly");
        frequencyInput.getItems().add("Weekly");
        frequencyInput.setValue("Weekly");
    }
}
