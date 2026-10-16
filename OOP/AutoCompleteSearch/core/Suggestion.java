package OOP.AutoCompleteSearch.core;

public class Suggestion {
    private final String word;
    private final int weight;

    public Suggestion(String word, int weight) {
        this.word = word;
        this.weight = weight;
    }

    public String getWord() {
        return this.word;
    }

    public int getWeight() {
        return this.weight;
    }
}
