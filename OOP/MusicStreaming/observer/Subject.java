package OOP.MusicStreaming.observer;

import OOP.MusicStreaming.entities.Album;
import OOP.MusicStreaming.entities.Artist;

import java.util.ArrayList;
import java.util.List;
/*
    This is reusable subject base class that manages the observer list and broadcast updates.
 */
public abstract class Subject {
    private final List<ArtistObserver> observers = new ArrayList<>();
    public void addObserver(ArtistObserver observer) {
        observers.add(observer);
    }
    public void notifyObservers(Artist artist, Album album) {
        for(ArtistObserver observer : observers) {
            observer.update(artist, album);
        }
    }
}
