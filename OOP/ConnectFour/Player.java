package OOP.ConnectFour;

public class Player {
    private final String name;
    private final DiscColor color;

    public Player(String name, DiscColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public DiscColor getColor() {
        return this.color;
    }
}
