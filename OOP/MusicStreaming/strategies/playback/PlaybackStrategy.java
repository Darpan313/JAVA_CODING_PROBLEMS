package OOP.MusicStreaming.strategies.playback;

import OOP.MusicStreaming.entities.Player;
import OOP.MusicStreaming.entities.Song;
import OOP.MusicStreaming.enums.SubscriptionTier;

/*
    The playback experience differs for free and premium users. The Strategy pattern lets us define these behaviours and
    assign them to users at runtime.
 */
public interface PlaybackStrategy {
    void play(Song song, Player player);

    // Simple factory method to get the correct strategy
    static PlaybackStrategy getStrategy(SubscriptionTier tier, int songsPlayed) {
        return tier == SubscriptionTier.PREMIUM ? new PremiumPlaybackStrategy() : new FreePlaybackStrategy(songsPlayed);
    }
}
