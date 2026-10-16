package ImplementProblems.ASCIICanvasEngine;

public enum Color {
    RED("\u001B[31m", "●"),
    GREEN("\u001B[32m", "●"),
    BLUE("\u001B[34m", "●"),
    NONE("\u001B[0m", "");

    private final String code;
    private final String symbol;

    Color(String code, String symbol) {
        this.code = code;
        this.symbol = symbol;
    }

    public String getCode() { return code; }
    public String getSymbol() { return symbol; }

    // Method to print the dot formatted in the enum's color
    public void display() {
        System.out.print(code + symbol + NONE.getCode());
    }
}
