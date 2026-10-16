package OOP.CarRentalSystem.observers;

import OOP.CarRentalSystem.models.Bill;
import OOP.CarRentalSystem.models.Reservation;

public interface RentalObserver {
    void onReservationCreated(Reservation reservation);
    void onVehiclePickedUp(Reservation reservation);
    void onVehicleReturned(Reservation reservation, Bill bill);
}
