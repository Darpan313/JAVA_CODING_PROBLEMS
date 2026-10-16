package OOP.MusicStreaming.strategies.playback;

import OOP.MusicStreaming.entities.Player;
import OOP.MusicStreaming.entities.Song;

public class FreePlaybackStrategy implements PlaybackStrategy {
    private int songsPlayed;
    private static final int SONGS_BEFORE_AD = 3;

    public FreePlaybackStrategy(int initialSongsPlayed) {
        this.songsPlayed = initialSongsPlayed;
    }
    @Override
    public void play(Song song, Player player) {
        if(songsPlayed>0 && songsPlayed % SONGS_BEFORE_AD == 0) {
            System.out.println("\n>>> Playing Advertisement: 'Buy Amazon premium for add-free music!' <<<\n");
        }
        System.out.printf("Free user is now playing %s%n", song);
        songsPlayed++;
    }
}
