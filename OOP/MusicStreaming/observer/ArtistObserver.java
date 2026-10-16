package OOP.MusicStreaming.observer;

import OOP.MusicStreaming.entities.Album;
import OOP.MusicStreaming.entities.Artist;

public interface ArtistObserver {
    void update(Artist artist, Album album);
}
