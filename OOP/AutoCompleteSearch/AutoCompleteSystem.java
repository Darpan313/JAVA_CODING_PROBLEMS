package OOP.AutoCompleteSearch;

import OOP.AutoCompleteSearch.core.Suggestion;
import OOP.AutoCompleteSearch.core.Trie;
import OOP.AutoCompleteSearch.core.TrieNode;
import OOP.AutoCompleteSearch.strategy.RankingStrategy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCompleteSystem {
    private final Trie trie;
    private final RankingStrategy rankingStrategy;
    private final int maxSuggestions;

    public AutoCompleteSystem(RankingStrategy strategy, int maxSuggestions) {
        this.trie = new Trie();
        rankingStrategy = strategy;
        this.maxSuggestions = maxSuggestions;
    }

    public void addWord(String word) {
        this.trie.insert(word);
    }

    public void addWords(List<String> words) {
        words.forEach(this::addWord);
    }

    public List<String> getSuggestions(String prefix) {
        TrieNode prefixNode = trie.searchPrefix(prefix);
        if(prefixNode == null) {
            return Collections.emptyList();
        }
        List<Suggestion> rawSuggestions = trie.collectSuggestions(prefixNode, prefix.toLowerCase());
        List<Suggestion> rankedSuggestions = rankingStrategy.rank(rawSuggestions);

        return rankedSuggestions.stream()
                .limit(maxSuggestions)
                .map(Suggestion::getWord)
                .collect(Collectors.toList());
    }
}
