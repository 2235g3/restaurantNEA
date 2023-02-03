package rs.cs.restaurantnea.general.IOData;

import rs.cs.restaurantnea.general.errorMethods;
import rs.cs.restaurantnea.general.objects.Email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class emailMethods {
    public static void sendEmails(Email email) {
        try {
            Properties emailServiceProp = emailServiceConfig();
            Session session = createSession(emailServiceProp);
            Message message = createMessage(session, email);
            Transport.send(message);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static Properties emailServiceConfig() {
        Properties emailServiceProp = new Properties();
        emailServiceProp.put("mail.smtp.auth", true);
        emailServiceProp.put("mail.smtp.starttls.enable", "true");
        emailServiceProp.put("mail.smtp.host", "smtp.mailtrap.io");
        emailServiceProp.put("mail.smtp.port", "25");
        emailServiceProp.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
        return emailServiceProp;
    }
    public static Session createSession(Properties emailServiceProp) {
        Session session = Session.getInstance(emailServiceProp, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("username", "password");
            }
        });
        return session;
    }
    public static Message createMessage(Session session, Email email) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email.getSenderEmail()));
            for (String recipientEmail: email.getRecipientEmails()) {
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            }
            for (String BCCEmail: email.getBCCEmails()) {
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(BCCEmail));
            }

            message.setSubject(email.getSubject());
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(email.getContents(), "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);
            return message;
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
            return null;
        }
    }
}
