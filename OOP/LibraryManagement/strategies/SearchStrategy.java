package OOP.LibraryManagement.strategies;

import OOP.LibraryManagement.entities.LibraryItem;

import java.util.List;

public interface SearchStrategy {
    List<LibraryItem> search(String query, List<LibraryItem> items);
}
