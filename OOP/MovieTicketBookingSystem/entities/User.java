package OOP.MovieTicketBookingSystem.entities;

public class User {
    String id;
    String name;
    String email;

    public User(String name, String email) {
        this.id = "user-"+name;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
