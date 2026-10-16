package OOP.MusicStreaming.entities;

import OOP.MusicStreaming.states.PlayerState;
import OOP.MusicStreaming.states.StoppedState;

import java.util.ArrayList;
import java.util.List;

/*
    Player holds the current state and delegate every actions to it.
 */
public class Player {
    private PlayerState state;
    private List<Song> queue = new ArrayList<>();
    private int currentIndex = -1;
    private User currentUser;

    public Player() {
        this.state = new StoppedState();
    }

    public void load(Playable playable, User user) {
        this.currentUser = user;
        this.queue = playable.getTracks();
        this.currentIndex = 0;
        System.out.printf("Loaded %d tracks for user %s.%n", queue.size(), user.getName());
        this.state = new StoppedState();
    }

    public void playCurrentSongInQueue() {
        if(currentIndex>=0 && currentIndex<queue.size()) {
            Song songToPlay = queue.get(currentIndex);
            currentUser.getPlaybackStrategy().play(songToPlay, this);
        }
    }

    public void clickPlay() {
        state.play(this);
    }

    public void clickNext() {
        state.next(this);
    }

    public void clickPause() {
        state.pause(this);
    }

    public void advanceToNextSong() {
        if(currentIndex<queue.size()-1) {
            currentIndex++;
            playCurrentSongInQueue();
        }
        else {
            System.out.println("End of queue");
            state.stop(this);
        }
    }

    public void changeState(PlayerState state) {
        this.state = state;
    }

    public boolean hasQueue() {
        return !queue.isEmpty();
    }
}
