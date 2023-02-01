package rs.cs.restaurantnea;

import java.time.LocalDate;

public class Booking {
    private String name;
    private LocalDate date;
    private String time;
    private int amtPpl;
    private String eventType;
    private User user;
    private int tableID;
    private int bookingID;

    public Booking(String name, LocalDate date, String time, int amtPpl, String eventType, User user, int tableID, int bookingID) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.amtPpl = amtPpl;
        this.eventType = eventType;
        this.user = user;
        this.tableID = tableID;
        this.bookingID = bookingID;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAmtPpl() {
        return amtPpl;
    }

    public void setAmtPpl(int amtPpl) {
        this.amtPpl = amtPpl;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
