package OOP.CarRentalSystem.observers;

import OOP.CarRentalSystem.models.Bill;
import OOP.CarRentalSystem.models.Reservation;

public class EmailNotificationObserver implements RentalObserver {
    @Override
    public void onReservationCreated(Reservation reservation) {
        System.out.println("[Email] Reservation confirmed: " + reservation.getId()
                + " for " + reservation.getCustomer().getName()
                + " - " + reservation.getVehicleType()
                + " (" + reservation.getPickupDate()
                + " to " + reservation.getReturnDate() + ")");
    }

    @Override
    public void onVehiclePickedUp(Reservation reservation) {
        System.out.println("[Email] Vehicle picked up: "
                + reservation.getCustomer().getName() + " picked up "
                + reservation.getAssignedVehicle().getLicensePlate()
                + " (" + reservation.getVehicleType() + ")");
    }

    @Override
    public void onVehicleReturned(Reservation reservation, Bill bill) {
        System.out.println("[Email] Vehicle returned: "
                + reservation.getCustomer().getName() + " returned "
                + reservation.getAssignedVehicle().getLicensePlate()
                + ". Total: $" + String.format("%.2f", bill.getTotalCost()));
    }
}
