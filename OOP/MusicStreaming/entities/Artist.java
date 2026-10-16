package OOP.MusicStreaming.entities;

import OOP.MusicStreaming.observer.Subject;

import java.util.ArrayList;
import java.util.List;

/*
    Artist extends Subject, so releasing an album appends to the discography and notifies every follower.
 */
public class Artist extends Subject {
    private final String id;
    private final String name;
    private final List<Album> discography = new ArrayList<>();

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void releaseAlbum(Album album) {
        discography.add(album);
        System.out.printf("[System] Artist %s has released a new album: %s%n", name, album.getTitle());
        notifyObservers(this, album);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
