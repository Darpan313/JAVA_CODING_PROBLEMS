package OOP.LibraryManagement.factory;

import OOP.LibraryManagement.entities.Book;
import OOP.LibraryManagement.entities.LibraryItem;
import OOP.LibraryManagement.entities.Magazine;
import OOP.LibraryManagement.enums.ItemType;

public class ItemFactory {
    public static LibraryItem createItem(ItemType type, String id, String title, String authorOrPublisher) {
        switch (type) {
            case BOOK -> {
                return new Book(id, title, authorOrPublisher);
            }
            case MAGAZINE -> {
                return new Magazine(id, title, authorOrPublisher);
            }
            default -> throw new IllegalArgumentException("Unknown Item Type");
        }
    }
}
