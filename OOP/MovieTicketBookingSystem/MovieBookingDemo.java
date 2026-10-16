package OOP.MovieTicketBookingSystem;

import OOP.MovieTicketBookingSystem.entities.*;
import OOP.MovieTicketBookingSystem.enums.SeatStatus;
import OOP.MovieTicketBookingSystem.enums.SeatType;
import OOP.MovieTicketBookingSystem.strategies.payment.CreditCardPaymentStrategy;
import OOP.MovieTicketBookingSystem.strategies.pricing.WeekdayPricingStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieBookingDemo {
    public static void main(String[] args) {
        MovieBookingService service = new MovieBookingService().getInstance();
        City nyc = service.addCity("city1", "New York");
        City la = service.addCity("city2", "Los Angeles");

        // 2. Add Movie
        Movie matrix = new Movie("M1", "The Matrix", 120);
        Movie avengers = new Movie("M2", "Avengers: Endgame", 170);
        service.addMovie(matrix);
        service.addMovie(avengers);

        Screen screen1 = new Screen("S1");
        for(int i=0; i<=10; i++) {
            screen1.addSeat(new Seat("A"+i, 1, i, i<=5 ? SeatType.REGULAR : SeatType.PREMUIM));
            screen1.addSeat(new Seat("B"+i, 2, i, i<=5 ? SeatType.REGULAR : SeatType.PREMUIM));
        }

        Theater amcNYC = service.addTheater("Theater1", "AMC Time Square", nyc.getId(), List.of(screen1));
        Map<SeatType, Double> seatPrices = Map.of(
                SeatType.REGULAR, 50.0,
                SeatType.PREMUIM, 80.0,
                SeatType.RECLINER, 120.0
        );

        Show matrixShow = service.addShow("show1", matrix, screen1, LocalDateTime.now().plusHours(2), seatPrices, new WeekdayPricingStrategy());
        Show avengerShow = service.addShow("show2", avengers, screen1, LocalDateTime.now().plusHours(5), seatPrices, new WeekdayPricingStrategy());

        User alice = service.createUser("Alice", "alice.example.com");

        // --- User Story: Alice books tickets ---
        System.out.println("--- Alice's Booking Flow ---");
        String cityName = "New York";
        String movieTitle = "Avengers: Endgame";

        List<Show> availableShows = service.findShows(movieTitle, cityName);
        if (availableShows.isEmpty()) {
            System.out.println("No shows found for " + movieTitle + " in " + cityName);
            return;
        }
        Show selectedShow = availableShows.get(0);

        //2. View available seats
        List<Seat> availableSeats = selectedShow.getScreen().getSeats().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .toList();

        System.out.printf("Available seats for '%s' at %s: %s%n",
                selectedShow.getMovie().getTitle(),
                selectedShow.getStartTime(),
                availableSeats.stream().map(Seat::getId).collect(Collectors.toList()));

        // 3. Select seats
        List<Seat> desiredSeats = List.of(availableSeats.get(2), availableSeats.get(3));
        System.out.println("Alice selects seats: " + desiredSeats.stream().map(Seat::getId).toList());

        Optional<Ticket> ticketOpt = service.bookTickets(
                alice.getId(),
                selectedShow.getId(),
                desiredSeats,
                new CreditCardPaymentStrategy("1234-5678-9876-5432", "123")
        );

        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            System.out.println("\n--- Booking Successful! ---");
            System.out.println("Booking ID: " + ticket.getId());
            System.out.println("User: " + ticket.getUser().getName());
            System.out.println("Movie: " + ticket.getShow().getMovie().getTitle());
            System.out.println("Seats: " + ticket.getSeats().stream().map(Seat::getId).collect(Collectors.toList()));
            System.out.println("Total Amount: $" + ticket.getTotalAmount());
            System.out.println("Payment Status: " + ticket.getPayment().getStatus());
        } else {
            System.out.println("Booking failed.");
        }

        // 5. Verify seat status after booking
        System.out.println("\nSeat status after Alice's booking:");
        desiredSeats.forEach(seat -> System.out.printf("Seat %s status: %s%n", seat.getId(), seat.getStatus()));

        System.out.println("\n--- Bob tries to book the same seats ---");
        User bob = service.createUser("Bob", "bob@example.com");
        Optional<Ticket> bobBooking = service.bookTickets(
                bob.getId(),
                selectedShow.getId(),
                desiredSeats,
                new CreditCardPaymentStrategy("9999-8888-7777-6666", "456")
        );
        System.out.println(bobBooking.isPresent()
                ? "Bob's booking succeeded (this should not happen)."
                : "Bob's booking was correctly rejected: those seats are already taken.");

        // 7. Shut down the system to release resources like the scheduler.
        service.shutDown();
    }
}
