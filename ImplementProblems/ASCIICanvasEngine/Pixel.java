package ImplementProblems.ASCIICanvasEngine;

public class Pixel {
    private char c;
    private Color color;

    public Pixel(char c, Color color) {
        this.c = c;
        this.color = color;
    }

    public char getC() {
        return c;
    }

    public Color getColor() {
        return color;
    }
}
