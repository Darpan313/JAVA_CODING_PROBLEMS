package OOP.LibraryManagement.states;

import OOP.LibraryManagement.entities.BookCopy;
import OOP.LibraryManagement.entities.Member;
import OOP.LibraryManagement.services.TransactionService;

public class CheckedOutState implements ItemState{
    @Override
    public void checkout(BookCopy copy, Member member) {
        System.out.println(copy.getId() + " is already checked out.");
    }

    @Override
    public void returnItem(BookCopy copy) {
        TransactionService.getInstance().endLoan(copy);
        System.out.println(copy.getId() + " returned.");
        // If there are holds, move to OnHold state. Otherwise, become Available.
        if(copy.getItem().hasObservers()) {
            copy.setState(new OnHoldState());
            copy.getItem().notifyObservers();
        }
        else {
            copy.setState(new AvailableState());
        }
    }

    @Override
    public void placeHold(BookCopy copy, Member member) {
        copy.getItem().addObserver(member);
        System.out.println(member.getName() + " place a hold on '" + copy.getItem().getTitle() + "'");
    }
}
