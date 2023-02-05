package rs.cs.restaurantnea.general.objects;

public class Email {
    private String senderEmail;
    private String[] recipientEmails;
    private String Subject;
    private String Contents;
    private String frequencyType;

    public Email(String senderEmail, String[] recipientEmails, String subject, String contents, String frequencyType) {
        this.senderEmail = senderEmail;
        this.recipientEmails = recipientEmails;
        this.Subject = subject;
        this.Contents = contents;
        this.frequencyType = frequencyType;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String[] getRecipientEmails() {
        return recipientEmails;
    }

    public void setRecipientEmails(String[] recipientEmails) {
        this.recipientEmails = recipientEmails;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public String getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }
}
