package OOP.MusicStreaming.entities;

import java.util.List;

/*
    The Playable abstraction lets a single song and a group of songs be treated the same way. This is Composite Pattern
    Song, Album and PlayList all expose the same methods to return their tracks.

    The contract is one method that returns the list of songs to play. A Song returns itself; and Album or Playlist returns
    all of its tracks.
 */
public interface Playable {
    List<Song> getTracks();
}
