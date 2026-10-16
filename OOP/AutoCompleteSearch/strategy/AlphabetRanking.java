package OOP.AutoCompleteSearch.strategy;

import OOP.AutoCompleteSearch.core.Suggestion;

import java.util.Comparator;
import java.util.List;

public class AlphabetRanking implements RankingStrategy {
    @Override
    public List<Suggestion> rank(List<Suggestion> suggestions) {
        return suggestions.stream()
                .sorted(Comparator.comparing(Suggestion::getWord))
                .toList();
    }
}
