package OOP.MusicStreaming.strategies.recommendation;

import OOP.MusicStreaming.entities.Song;

import java.util.List;

public interface RecommendationStrategy {
    List<Song> recommend(List<Song> allSongs);
}
