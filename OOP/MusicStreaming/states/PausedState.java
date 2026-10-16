package OOP.MusicStreaming.states;

import OOP.MusicStreaming.entities.Player;

public class PausedState implements PlayerState {
    @Override
    public void play(Player player) {
        System.out.println("Resuming playback");
        player.changeState(new PlayingState());
    }

    @Override
    public void pause(Player player) {
        System.out.println("Already paused");
    }

    @Override
    public void stop(Player player) {
        System.out.println("Stopping playback from paused state.");
        player.changeState(new StoppedState());
    }

    @Override
    public void next(Player player) {
        player.changeState(new PlayingState());
        player.advanceToNextSong();
    }
}
