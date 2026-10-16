package OOP.MusicStreaming.service;

import OOP.MusicStreaming.entities.Artist;
import OOP.MusicStreaming.entities.Song;

import java.util.List;

public class SearchService {
    public List<Song> searchSongsByTitle(List<Song> songs, String query) {
        return songs.stream()
                .filter(s -> s.getTitle().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public List<Artist> searchArtistsByName(List<Artist> artists, String query) {
        return artists.stream()
                .filter(a -> a.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
}
