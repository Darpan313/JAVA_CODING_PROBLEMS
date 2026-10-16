package OOP.MusicStreaming.strategies.recommendation;

import OOP.MusicStreaming.entities.Song;

import java.util.Comparator;
import java.util.List;

public class SimpleRecommendationStrategy implements RecommendationStrategy {
    @Override
    public List<Song> recommend(List<Song> allSongs) {
        System.out.println("Generating recommendation...");
        return allSongs.stream()
                .sorted(Comparator.comparing(Song::getTitle))
                .limit(5)
                .toList();
    }
}
