package OOP.TicketBooking.Entity;

import java.util.ArrayList;
import java.util.List;

// We want this to be immuatable class
public class Reservation {
    private final String confirmationId;
    private final Showtime showtime;
    private final List<String> seatIds;

    public Reservation(String confirmationId, Showtime showtime, List<String> seatIds) {
        this.confirmationId = confirmationId;
        this.showtime = showtime;
        this.seatIds = new ArrayList<>(seatIds);    // Defensive copy
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public List<String> getSeatIds() {
        return new ArrayList<>(seatIds);
    }
}
