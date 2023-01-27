package rs.cs.restaurantnea.general;

import rs.cs.restaurantnea.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regExMatchers {
    public static Matcher createEmailMatcher(User user) {
        Pattern emailRegExPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"); // Defines the regular expression for the password
        return emailRegExPattern.matcher(user.getEmail()); // Returns the matcher
    }
    public static Matcher createPasswordMatcher(User user) {
        Pattern passRegExPattern = Pattern.compile("(?=^.{8,20}$)((?!.*\\s)(?=.*[A-Z])(?=.*[a-z]))((?=(.*\\d){1,})(?=(.*\\W){1,}))^.*$"); // Defined the regular expression for the email
        return passRegExPattern.matcher(user.getPassword());
    }
    public static Matcher createNameMatcher(String name) {
        Pattern nameRegExPattern = Pattern.compile("\\b([A-ZÀ-ÿ][a-z]+)"); // Defined the regular expression for the user's name
        return nameRegExPattern.matcher(name);
    }
}
