package OOP.TicketBooking.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Showtime {
    private final String id;
    private final Theater theater;
    private final LocalDateTime dateTime;
    private final String screenLabel;
    private final Movie movie;
    private final List<Reservation> reservationList;

    public Showtime(String id, Theater theater, LocalDateTime dateTime, String screenLabel, Movie movie) {
        this.id = id;
        this.theater = theater;
        this.dateTime = dateTime;
        this.screenLabel = screenLabel;
        this.movie = movie;
        this.reservationList = new ArrayList<>();
    }

    public String getId() {
        return this.id;
    }

    public Theater getTheater() {
        return this.theater;
    }

    public Movie getMovie() {
        return movie;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isAvailable(String seatId) {
        for(Reservation reservation : reservationList)  {
            if(reservation.getSeatIds().contains(seatId)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getAvailableSeats() {
        Set<String> booked = new HashSet<>();
        for(Reservation reservation : reservationList) {
            booked.addAll(reservation.getSeatIds());
        }
        List<String> availableSeats = new ArrayList<>();
        for(char row = 'A'; row<='Z'; row++) {
            for(int i=0; i<=20; i++) {
                String seatId = ""+row+i;
                if(!booked.contains(seatId)) {
                    availableSeats.add(seatId);
                }
            }
        }
        return availableSeats;
    }

    public void book(Reservation reservation) {
        synchronized (this) {
            List<String> seatIds = reservation.getSeatIds();
            if(seatIds==null || seatIds.isEmpty()) {
                throw new IllegalArgumentException("Must select atleast one seat");
            }

            for(String seatId : seatIds) {
                if(!isValidSeat(seatId)) {
                    throw  new IllegalArgumentException("Invalid seat: " + seatId);
                }
            }

            for(String seatId : seatIds) {
                if(!isAvailable(seatId)) {
                    throw new IllegalArgumentException("Seat " + seatId + " is not available");
                }
            }

            reservationList.add(reservation);
        }
    }

    public void cancel(Reservation reservation) {
        synchronized (this) {
            reservationList.remove(reservation);
        }
    }

    private boolean isValidSeat(String seatId) {
        if(seatId == null || seatId.isEmpty()){
            return false;
        }
        char row = seatId.charAt(0);
        try {
            int num = Integer.parseInt(seatId.substring(1));
            return row>='A' && row<='Z' && num>=0 && num<=20;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
