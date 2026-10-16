package OOP.MovieTicketBookingSystem.entities;

import java.util.List;

public class Theater {
    private final String id;
    private final String name;
    private final City city;
    private final List<Screen> screens;


    public Theater(String id, String name, City city, List<Screen> screens) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.screens = screens;
    }

    public String getId() {
        return this.id;
    }

    public City getCity() {
        return city;
    }
}
