package OOP.LibraryManagement.states;

import OOP.LibraryManagement.entities.BookCopy;
import OOP.LibraryManagement.entities.Member;

public interface ItemState {
    void checkout(BookCopy copy, Member member);
    void returnItem(BookCopy copy);
    void placeHold(BookCopy copy, Member member);
}
