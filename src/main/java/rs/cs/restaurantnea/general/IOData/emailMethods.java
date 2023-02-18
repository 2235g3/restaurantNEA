package rs.cs.restaurantnea.general.IOData;

import rs.cs.restaurantnea.general.errorMethods;
import rs.cs.restaurantnea.general.objects.Email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;

public class emailMethods {
    public static void sendEmails(Email email) {
        try {
            email = findRecipients(email); // Finds the recipient emails of the selected group
            Properties emailServiceProp = emailServiceConfig(); // Inits the email service
            Session session = createSession(emailServiceProp); // Creates a session to connect to the admin email account
            if (email.getRecipientEmails().length > 0) { // If there is at least one recipeint, the email is sent
                Message message = createMessage(session, email); // Creates the email content
                Transport.send(message); // Sends the email
                errorMethods.exceptionErrors("The email was sent!", "The email should appear in inboxes soon!");
            }
            else {
                errorMethods.exceptionErrors("The email was not sent", "There were no recipients in the email, please try again.");
            }
        } catch (Exception e) {
            errorMethods.exceptionErrors("There was an error sending the email", "Here is the error:\n" + e);
        }
    }
    public static Properties emailServiceConfig() { // This method just sets up the email service to allow for the email to be sent correctly
        Properties emailServiceProp = new Properties();
        emailServiceProp.put("mail.transport.protocol", "smtp");
        emailServiceProp.put("mail.smtp.auth", true);
        emailServiceProp.put("mail.smtp.starttls.enable", "true");
        emailServiceProp.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        emailServiceProp.put("mail.smtp.host", "smtp.gmail.com");
        emailServiceProp.put("mail.smtp.port", 25);
        emailServiceProp.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        return emailServiceProp;
    }
    public static Session createSession(Properties emailServiceProp) { // Sets the sender email account and allows for the program to access their account
        Session session = Session.getInstance(emailServiceProp, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("restaurantneamanager@gmail.com", "risunxcoticijuqq");
            }
        });
        return session;
    }

    public static Message createMessage(Session session, Email email) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email.getSenderEmail()));
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("restaurantneamanager@gmail.com")); // Sets the recipient
            for (String recipientEmail: email.getRecipientEmails()) {
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(recipientEmail)); // Adds BCC recipients to hide the emails from being leaked to other users
            }

            message.setSubject(email.getSubject()); // Sets the email subject
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(email.getContents(), "text/html; charset=utf-8"); // Sets the email content

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);
            return message;
        } catch (Exception e) {
            errorMethods.exceptionErrors("There was an error creating the message", "Here is the error:\n" + e);
            return null;
        }
    }
    public static Email findRecipients(Email email) {
        databaseMethods DBM = new databaseMethods();
        cryptMethods CM = new cryptMethods();

        ArrayList<Object> dynFrequencyParams = createSQLRecipients(email); // Sets the params
        String sql = dynFrequencyParams.get(dynFrequencyParams.size() - 1).toString(); // Sets the sql string
        dynFrequencyParams.remove(dynFrequencyParams.size() - 1); // Removes the sql string from the params
        Object[] frequencyParams = dynFrequencyParams.toArray(); // Creates the object array of params
        String[][] encryptedEmails = DBM.getData(sql, frequencyParams); // Gets the encrypted emails
        String[] recipientEmails = new String[encryptedEmails.length]; // This is to store the recipient emails
        for (int i = 0; i < encryptedEmails.length; i++) {
            recipientEmails[i] = CM.decrypt(encryptedEmails[i][0], encryptedEmails[i][1],  Base64.getDecoder().decode(encryptedEmails[i][2])); // Decrypts the emails
        }
        email.setRecipientEmails(recipientEmails); // Stores them in the email object
        return email;
    }
    public static ArrayList<Object> createSQLRecipients(Email email) {
        String[] frequencies = {"Weekly", "Monthly", "Yearly", "All Users", "Never"};
        String sql = "SELECT users.email, users.hashedEmails, users.IV FROM users, customers WHERE users.userID = customers.userID AND customers.promoEmails IN (";
        ArrayList<Object> dynFrequencyParams = new ArrayList<>(); // This allows for a dynamic amount of parameters to be set

        // If Monthly is selected, customers who have the promoEmail selection of Monthly and Yearly are selected for example
        for (int i = 0; i < frequencies.length - 1; i++) { // Adds all the frequency groups necessary
            if (frequencies[i].equals("All Users")) {
                dynFrequencyParams.add(frequencies[frequencies.length - 1]);
            }
            else {
                dynFrequencyParams.add(frequencies[i]);
            }
            if (frequencies[i].equals(email.getFrequencyType())) {
                sql += "?)";
                i = frequencies.length;
            }
            else {
                sql += "?,";
            }
        }
        dynFrequencyParams.add(sql); // Allows for the updated sql string to be parsed back to the original method
        return dynFrequencyParams;
    }
}
