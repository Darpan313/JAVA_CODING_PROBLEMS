package OOP.AutoCompleteSearch.builder;

import OOP.AutoCompleteSearch.AutoCompleteSystem;
import OOP.AutoCompleteSearch.strategy.FrequencyBasedRanking;
import OOP.AutoCompleteSearch.strategy.RankingStrategy;

public class AutoCompleteSystemBuilder {
    private RankingStrategy rankingStrategy = new FrequencyBasedRanking(); // Default Ranking Strategy
    private int maxSuggestions = 10; // Default Limit

    public AutoCompleteSystemBuilder withRankingStrategy(RankingStrategy strategy) {
        this.rankingStrategy = strategy;
        return this;
    }

    public AutoCompleteSystemBuilder withMaxSuggestions(int max) {
        this.maxSuggestions = max;
        return this;
    }

    public AutoCompleteSystem build() {
        return new AutoCompleteSystem(rankingStrategy, maxSuggestions);
    }
}
