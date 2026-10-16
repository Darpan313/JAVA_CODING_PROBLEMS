package OOP.Chess.entities;

import OOP.Chess.enums.Color;

public class Player {
    private final Color color;
    private final String name;

    public Player(Color color, String name) {
        this.color = color;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name + "(" + color + ")";
    }
}
