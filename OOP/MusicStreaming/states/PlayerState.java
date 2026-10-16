package OOP.MusicStreaming.states;

import OOP.MusicStreaming.entities.Player;

public interface PlayerState {
    void play(Player player);
    void pause(Player player);
    void stop(Player player);
    void next(Player player);
}
