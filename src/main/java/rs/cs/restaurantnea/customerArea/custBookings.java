package rs.cs.restaurantnea.customerArea;

import rs.cs.restaurantnea.Booking;
import rs.cs.restaurantnea.general.regExMatchers;

import java.time.LocalDate;

public class custBookings {
    public static void makeBooking(Booking booking) {
        LocalDate currentDate = LocalDate.now();
        LocalDate maxBookDate = currentDate.plusYears(1);
        if (booking.getDate() == null || !regExMatchers.createNameMatcher(booking.getName()).matches()) {
            // [TBA]
        }
        if (booking.getDate().isBefore(currentDate) || booking.getDate().equals(currentDate) || booking.getDate().isAfter(maxBookDate)) {
            // [TBA]
        }
    }
}
