package rs.cs.restaurantnea.general.objects;

public class tempUser {
    private int userID;
    private String FName;
    private String LName;
    private String email;
    private String password;
    private int accountType;
    private String hashedEmail;

    public tempUser(int userID, String FName, String LName, String email, String password, int accountType, String hashedEmail) {
        this.userID = userID;
        this.FName = FName;
        this.LName = LName;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
        this.hashedEmail = hashedEmail;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getHashedEmail() {
        return hashedEmail;
    }

    public void setHashedEmail(String hashedEmail) {
        this.hashedEmail = hashedEmail;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String[] getList() {
        return new String[] {FName,LName,email,password,Integer.toString(accountType),hashedEmail,Integer.toString(userID)};
    }
}
