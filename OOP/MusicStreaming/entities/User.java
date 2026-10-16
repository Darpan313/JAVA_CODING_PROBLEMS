package OOP.MusicStreaming.entities;

import OOP.MusicStreaming.enums.SubscriptionTier;
import OOP.MusicStreaming.observer.ArtistObserver;
import OOP.MusicStreaming.strategies.playback.PlaybackStrategy;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User implements ArtistObserver {
    private final String id;
    private final String name;
    private final PlaybackStrategy playbackStrategy;
    private final Set<Artist> followedArtists = new HashSet<>();

    public User(String name, SubscriptionTier tier, int songsPlayed) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.playbackStrategy = PlaybackStrategy.getStrategy(tier, songsPlayed);
    }

    public void followArtist(Artist artist) {
        followedArtists.add(artist);
        artist.addObserver(this);
    }

    @Override
    public void update(Artist artist, Album newAlbum) {
        System.out.printf("[Notification for %s] Your followed artist %s just released a new album: %s!%n",
                this.name, artist.getName(), newAlbum.getTitle());
    }

    public PlaybackStrategy getPlaybackStrategy() {
        return playbackStrategy;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
