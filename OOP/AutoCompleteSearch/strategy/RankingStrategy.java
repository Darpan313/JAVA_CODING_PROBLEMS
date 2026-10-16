package OOP.AutoCompleteSearch.strategy;

import OOP.AutoCompleteSearch.core.Suggestion;

import java.util.List;

public interface RankingStrategy {
    List<Suggestion> rank(List<Suggestion> suggestions);
}
