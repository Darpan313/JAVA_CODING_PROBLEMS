package OOP.AutoCompleteSearch;

import OOP.AutoCompleteSearch.builder.AutoCompleteSystemBuilder;
import OOP.AutoCompleteSearch.strategy.AlphabetRanking;
import OOP.AutoCompleteSearch.strategy.FrequencyBasedRanking;

import java.util.List;

public class AutoCompleteDemo {
    public static void main(String[] args) {
        System.out.println("------ SCENARIO-1: FREQUENCY BASED RANKING ------");
        AutoCompleteSystem systemByFrequency = new AutoCompleteSystemBuilder()
                .withMaxSuggestions(5)
                .withRankingStrategy(new FrequencyBasedRanking())
                .build();

        // 2. Feed data into the system
        // 'canada' is added most frequently, followed by 'car'
        List<String> dictionary = List.of(
                "car", "cat", "cart", "cartoon", "canada", "candy",
                "car", "canada", "canada", "car", "canada", "canopy", "captain"
        );
        systemByFrequency.addWords(dictionary);

        // 3. Get suggestions for a prefix
        String prefix1 = "ca";
        List<String> suggestions1 = systemByFrequency.getSuggestions(prefix1);
        System.out.println("Suggestions for '" + prefix1 + "': " + suggestions1);

        String prefix2 = "car";
        List<String> suggestions2 = systemByFrequency.getSuggestions(prefix2);
        System.out.println("Suggestions for '" + prefix2 + "': " + suggestions2);

        System.out.println("\n----------- SCENARIO 2: Alphabetical Ranking -----------");

        // 1. Build a new system with the alphabetical strategy
        AutoCompleteSystem systemAlphabetical = new AutoCompleteSystemBuilder()
                .withRankingStrategy(new AlphabetRanking())
                .build();

        // 2. Feed the same data
        systemAlphabetical.addWords(dictionary);

        // 3. Get suggestions for the same prefix
        List<String> suggestions3 = systemAlphabetical.getSuggestions(prefix1);
        System.out.println("Suggestions for '" + prefix1 + "' (alphabetical): " + suggestions3);
    }
}
