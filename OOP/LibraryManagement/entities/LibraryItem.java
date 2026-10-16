package OOP.LibraryManagement.entities;

import java.util.ArrayList;
import java.util.List;

public abstract class LibraryItem {
    private final String id;
    private final String title;
    protected final List<BookCopy> copies = new ArrayList<>();
    // Observer Pattern: List of members waiting for this item
    private final List<Member> observers = new ArrayList<>();

    public LibraryItem(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public void addCopy(BookCopy copy) {
        copies.add(copy);
    }
    public void addObserver(Member member) {
        observers.add(member);
    }
    public void removeObserver(Member member) {
        observers.remove(member);
    }

    public void notifyObservers() {
        System.out.println("Notifying " + observers.size() + " observers for title '" + title + "'...");
        new ArrayList<>(observers).forEach(observer -> observer.update(this));
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public List<BookCopy> getCopies() {
        return copies;
    }

    public abstract String getAuthorOrPublisher();

    public long getAvailableCopyCount() {
        return copies.stream().filter(BookCopy::isAvailable).count();
    }

    public boolean hasObservers() {
        return !observers.isEmpty();
    }

    public boolean isObserver(Member member) {
        return observers.contains(member);
    }
}
