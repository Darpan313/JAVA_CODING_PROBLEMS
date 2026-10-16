package OOP.MusicStreaming.strategies.playback;

import OOP.MusicStreaming.entities.Player;
import OOP.MusicStreaming.entities.Song;

public class PremiumPlaybackStrategy implements PlaybackStrategy {
    @Override
    public void play(Song song, Player player) {
        System.out.printf("Premium user is now playing: %s%n", song);
    }
}
