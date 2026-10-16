package OOP.MusicStreaming.entities;

import java.util.ArrayList;
import java.util.List;

/*
    An album is a composite: it holds a list of song and returns a defensive copy so callers cannot mutate its internal
    list.
 */
public class Album implements Playable {
    private final String title;
    private final Artist artist;
    private final List<Song> tracks = new ArrayList<>();

    public Album(String title, Artist artist) {
        this.title = title;
        this.artist = artist;
    }

    public void addTrack(Song song) {
        tracks.add(song);
    }

    @Override
    public List<Song> getTracks() {
        return List.copyOf(tracks);
    }

    public Artist getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }
}
