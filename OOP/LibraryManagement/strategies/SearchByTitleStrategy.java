package OOP.LibraryManagement.strategies;

import OOP.LibraryManagement.entities.LibraryItem;

import java.util.List;

public class SearchByTitleStrategy implements SearchStrategy {
    @Override
    public List<LibraryItem> search(String query, List<LibraryItem> items) {
        return items.stream()
                .filter(item -> item.getTitle().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
}
