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
            email = findRecipients(email);
            Properties emailServiceProp = emailServiceConfig();
            Session session = createSession(emailServiceProp);
            if (email.getRecipientEmails().length > 0) {
                Message message = createMessage(session, email);
                Transport.send(message);
            }
            errorMethods.exceptionErrors("The email was sent!", "The email should appear in inboxes soon!");
        } catch (Exception e) {
            errorMethods.exceptionErrors("There was an error sending the email", "Here is the error:\n" + e);
        }
    }
    public static Properties emailServiceConfig() {
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
    public static Session createSession(Properties emailServiceProp) {
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
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("restaurantneamanager@gmail.com"));
            for (String recipientEmail: email.getRecipientEmails()) {
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(recipientEmail));
            }

            message.setSubject(email.getSubject());
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(email.getContents(), "text/html; charset=utf-8");

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

        ArrayList<Object> dynFrequencyParams = createSQLRecipients(email);
        String sql = dynFrequencyParams.get(dynFrequencyParams.size() - 1).toString();
        dynFrequencyParams.remove(dynFrequencyParams.size() - 1);
        Object[] frequencyParams = dynFrequencyParams.toArray();
        String[][] encryptedEmails = DBM.getData(sql, frequencyParams);
        String[] recipientEmails = new String[encryptedEmails.length];
        for (int i = 0; i < encryptedEmails.length; i++) {
            recipientEmails[i] = CM.decrypt(encryptedEmails[i][0], encryptedEmails[i][1],  Base64.getDecoder().decode(encryptedEmails[i][2]));
        }
        email.setRecipientEmails(recipientEmails);
        return email;
    }
    public static ArrayList<Object> createSQLRecipients(Email email) {
        String[] frequencies = {"Weekly", "Monthly", "Yearly", "All Users", "Never"};
        String sql = "SELECT users.email, users.hashedEmails, users.IV FROM users, customers WHERE users.userID = customers.userID AND customers.promoEmails IN (";
        ArrayList<Object> dynFrequencyParams = new ArrayList<>();

        for (int i = 0; i < frequencies.length - 1; i++) {
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
        dynFrequencyParams.add(sql);
        return dynFrequencyParams;
    }
}
