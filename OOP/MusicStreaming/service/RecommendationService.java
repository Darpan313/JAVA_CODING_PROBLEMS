package OOP.MusicStreaming.service;

import OOP.MusicStreaming.entities.Song;
import OOP.MusicStreaming.strategies.recommendation.RecommendationStrategy;

import java.util.List;

public class RecommendationService {
    private RecommendationStrategy strategy;

    public RecommendationService(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Song> getRecommendations(List<Song> allSongs) {
        return strategy.recommend(allSongs);
    }
}
