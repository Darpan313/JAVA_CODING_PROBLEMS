package OOP.LibraryManagement.states;

import OOP.LibraryManagement.entities.BookCopy;
import OOP.LibraryManagement.entities.Member;
import OOP.LibraryManagement.services.TransactionService;

public class AvailableState implements ItemState {
    @Override
    public void checkout(BookCopy copy, Member member) {
        TransactionService.getInstance().createLoan(copy, member);
        copy.setState(new CheckedOutState());
        System.out.println(copy.getId() + " checked out by " + member.getName());
    }

    @Override
    public void returnItem(BookCopy copy) {
        System.out.println("Cannot return an item that is already available.");
    }

    @Override
    public void placeHold(BookCopy copy, Member member) {
        System.out.println("Cannot place hold on an available item. Please check it out.");
    }
}
