package rs.cs.restaurantnea.general.objects;

public class Email {
    private String senderEmail;
    private String[] recipientEmails;
    private String[] BCCEmails;
    private String Subject;
    private String Contents;
    private String[] attachmentPaths;
    private String frequencyType;

    public Email(String senderEmail, String[] recipientEmails, String[] BCCEmails, String subject, String contents, String[] attachmentPaths, String frequencyType) {
        this.senderEmail = senderEmail;
        this.recipientEmails = recipientEmails;
        this.BCCEmails = BCCEmails;
        this.Subject = subject;
        this.Contents = contents;
        this.attachmentPaths = attachmentPaths;
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

    public String[] getBCCEmails() {
        return BCCEmails;
    }

    public void setBCCEmails(String[] BCCEmails) {
        this.BCCEmails = BCCEmails;
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

    public String[] getAttachmentPaths() {
        return attachmentPaths;
    }

    public void setAttachmentPaths(String[] attachmentPaths) {
        this.attachmentPaths = attachmentPaths;
    }

    public String getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }
}
