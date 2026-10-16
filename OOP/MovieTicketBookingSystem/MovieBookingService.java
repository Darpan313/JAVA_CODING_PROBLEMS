package OOP.MovieTicketBookingSystem;

import OOP.MovieTicketBookingSystem.entities.*;
import OOP.MovieTicketBookingSystem.enums.SeatType;
import OOP.MovieTicketBookingSystem.strategies.payment.PaymentStrategy;
import OOP.MovieTicketBookingSystem.strategies.pricing.PricingStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MovieBookingService {
    private static volatile  MovieBookingService instance;
    private final SeatLockManager seatLockManager;
    private final Map<String, City> cities;
    private final Map<String, Movie> movies;
    private final Map<String, Theater> theaters;
    private final Map<Screen, Theater> screenToTheater;
    private final Map<String, Show> shows;
    private final Map<String, User> users;
    private final TicketManager ticketManager;

    public MovieBookingService() {
        this.cities = new ConcurrentHashMap<>();
        this.movies = new ConcurrentHashMap<>();
        this.theaters = new ConcurrentHashMap<>();
        this.screenToTheater = new ConcurrentHashMap<>();
        this.shows = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.seatLockManager = new SeatLockManager();
        this.ticketManager = new TicketManager(seatLockManager);
    }

    public static MovieBookingService getInstance() {
        if(instance == null) {
            synchronized (MovieBookingService.class) {
                if(instance == null) {
                    instance = new MovieBookingService();
                }
            }
        }
        return instance;
    }

    public City addCity(String id, String name) {
        City city = new City(id, name);
        cities.put(city.getId(), city);
        return city;
    }

    public void addMovie(Movie movie) {
        movies.put(movie.getId(), movie);
    }

    public Theater addTheater(String id, String name, String cityId, List<Screen> screens) {
        City city = cities.get(cityId);
        Theater theater = new Theater(id, name, city, screens);
        theaters.put(theater.getId(), theater);
        for(Screen screen : screens) {
            screenToTheater.put(screen, theater);
        }
        return theater;
    }

    public Show addShow(String id, Movie movie, Screen screen, LocalDateTime startTime, Map<SeatType, Double> seatPrices, PricingStrategy pricingStrategy) {
        Show show = new Show(id, movie, screen, startTime, seatPrices, pricingStrategy);
        shows.put(show.getId(), show);
        return show;
    }

    public User createUser(String name, String email) {
        User user = new User(name, email);
        users.put(user.getId(), user);
        return user;
    }

    public List<Show> findShows(String movieTitle, String cityName) {
        List<Show> result = new ArrayList<>();
        shows.values().stream()
                .filter(show -> show.getMovie().getTitle().equalsIgnoreCase(movieTitle))
                .filter(show -> {
                    Theater theater = findTheaterForShow(show);
                    return theater!=null && theater.getCity().getName().equalsIgnoreCase(cityName);
                })
                .forEach(result::add);
        return result;
    }

    private Theater findTheaterForShow(Show show) {
        return screenToTheater.get(show.getScreen());
    }

    public Optional<Ticket> bookTickets(String userId, String showId, List<Seat> desiredSeats, PaymentStrategy paymentStrategy) {
        return ticketManager.buyTicket(users.get(userId), shows.get(showId), desiredSeats, paymentStrategy);
    }

    public void shutDown() {
        this.seatLockManager.shutdown();
        System.out.println("MovieTicketBookingSystem has been shut down.");
    }
}
