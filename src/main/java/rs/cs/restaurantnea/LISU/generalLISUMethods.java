package rs.cs.restaurantnea.LISU;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class generalLISUMethods {
    public boolean showHidePass(PasswordField passwordInput, TextField passwordShownInput, boolean clicked) {
        clicked = !clicked; //Shows that the Show/Hide Password checkbox has been clicked
        if (clicked) { // If true, the password is displayed as text
            passVisibility(passwordInput.getText(), true, passwordShownInput, passwordInput);
        } else { // If false, the password is shown as obscured text
            passVisibility(passwordShownInput.getText(), false, passwordShownInput, passwordInput);
        }
        return clicked;
    }

    public void passVisibility(String textVal, boolean passShow, TextField passwordShownInput, PasswordField passwordInput) {
        if (passShow) { // Displays the password as text
            passwordShownInput.setText(textVal);
            passwordShownInput.setVisible(true);
            passwordInput.setVisible(false);
        }
        else { // Displays the password as obscured text
            passwordInput.setText(textVal);
            passwordInput.setVisible(true);
            passwordShownInput.setVisible(false);
        }
    }
}
