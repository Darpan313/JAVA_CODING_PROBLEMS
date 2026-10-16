package OOP.LibraryManagement.entities;

import OOP.LibraryManagement.states.AvailableState;
import OOP.LibraryManagement.states.ItemState;

// This class is the Context for the State Pattern
public class BookCopy {
    private final String id;
    private final LibraryItem item;
    private ItemState currentState;

    public BookCopy(String id, LibraryItem item) {
        this.id = id;
        this.item = item;
        this.currentState = new AvailableState();
        item.addCopy(this);
    }

    public String getId() {
        return id;
    }

    public LibraryItem getItem() {
        return item;
    }

    public void setState(ItemState state) {
        currentState = state;
    }

    public void checkout(Member member) {
        currentState.checkout(this, member);
    }

    public void returnItem(String copyId) {
        currentState.returnItem(this);
    }

    public void placeHold(Member member) {
        currentState.placeHold(this, member);
    }

    public boolean isAvailable() {
        return currentState instanceof AvailableState;
    }
}
