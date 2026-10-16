package OOP.TicketBooking;

import OOP.TicketBooking.Entity.Movie;
import OOP.TicketBooking.Entity.Reservation;
import OOP.TicketBooking.Entity.Showtime;
import OOP.TicketBooking.Entity.Theater;

import java.time.LocalDateTime;
import java.util.*;

public class BookingSystem {
    private final List<Theater> theaters;
    private final Map<String, Movie> moviesById;
    private final Map<String, List<Showtime>> showtimesByMovieId;
    private final Map<String, Showtime> showtimesById;
    private final Map<String, Reservation> reservationById;

    public BookingSystem(List<Theater> theaters) {
        this.theaters = theaters;
        this.moviesById = new HashMap<>();
        this.showtimesByMovieId = new HashMap<>();
        this.showtimesById = new HashMap<>();
        this.reservationById = new HashMap<>();

        for(Theater theater : theaters) {
            for(Showtime showtime : theater.getShowtimes()) {
                Movie movie = showtime.getMovie();
                moviesById.put(movie.getId(), movie);
                showtimesById.put(showtime.getId(), showtime);

                showtimesByMovieId.computeIfAbsent(movie.getId(), k-> new ArrayList<>()).add(showtime);
            }
        }
    }

    public List<Showtime> searchMovies(String title) {
        if(title==null || title.isEmpty()) {
            return new ArrayList<>();
        }

        List<Showtime> results = new ArrayList<>();
        String searchLower = title.toLowerCase();
        LocalDateTime now = LocalDateTime.now();

        for(Movie movie : moviesById.values()) {
            if(movie.getTitle().toLowerCase().contains(searchLower)) {
                List<Showtime> movieShowTimes = showtimesByMovieId.get(movie.getId());
                if(movieShowTimes != null) {
                    for(Showtime showtime : movieShowTimes) {
                        if(showtime.getDateTime().isAfter(now)) {
                            results.add(showtime);
                        }
                    }
                }
            }
        }
        return results;
    }

    public List<Showtime> getShowTimesAtTheater(Theater theater) {
        if(theater == null) {
            return new ArrayList<>();
        }

        List<Showtime> results = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for(Showtime showtime : theater.getShowtimes()) {
            if(showtime.getDateTime().isAfter(now)) {
                results.add(showtime);
            }
        }
        return results;
    }

    public Reservation book(String showtimeId, List<String> seatIds) {
        if(showtimeId == null || seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Invalid booking request");
        }

        Showtime showtime = showtimesById.get(showtimeId);
        if(showtime == null) {
            throw new NoSuchElementException("Showtime not found: "+showtimeId);
        }

        Reservation reservation = new Reservation(
                UUID.randomUUID().toString(),
                showtime,
                seatIds
        );
        showtime.book(reservation);
        reservationById.put(reservation.getConfirmationId(), reservation);
        return reservation;
    }

    public void cancelReservation(String confirmationId) {
        if(confirmationId == null || confirmationId.isEmpty()) {
            throw new IllegalArgumentException("Invalid confirmation Id");
        }

        Reservation reservation = reservationById.get(confirmationId);
        if(reservation == null) {
            throw new NoSuchElementException("Reservation not found: "+ confirmationId);
        }

        Showtime showtime = reservation.getShowtime();
        showtime.cancel(reservation);
        reservationById.remove(confirmationId);
    }
}
