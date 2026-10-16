package Amazon.ParkingLot;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ParkingLot {
    private final List<ParkingSpot> spots;
    private final Set<String> occupiedSpots;
    private final Map<String, Ticket> activeTickets;
    private final long hourlyRatesInCents;

    public ParkingLot(List<ParkingSpot> spots, long hourlyRatesInCents) {
        this.spots = spots;
        this.occupiedSpots = new HashSet<>();
        this.activeTickets = new HashMap<>();
        this.hourlyRatesInCents = hourlyRatesInCents;
    }

    public Ticket enter(VehicleType vehicleType) {
        ParkingSpot spot = findAvailableSpot(vehicleType);
        if(spot == null) {
            throw new RuntimeException("No available spot for vehicle type " + vehicleType);
        }

        occupiedSpots.add(spot.getId());

        String ticketId = UUID.randomUUID().toString();
        long entryTime = System.currentTimeMillis();
        Ticket ticket = new Ticket(ticketId, spot.getId(), vehicleType, entryTime);

        activeTickets.put(ticketId, ticket);

        return ticket;
    }

    public long exit(String ticketId) {
        if(ticketId == null || ticketId.isEmpty()) {
            throw new RuntimeException("Invalid ticket ID");
        }

        Ticket ticket = activeTickets.get(ticketId);
        if(ticket == null) {
            throw new RuntimeException("Ticket not found or already used");
        }

        long exitTime = System.currentTimeMillis();
        long fee = computeFee(ticket.getEntryTime(), exitTime);
        occupiedSpots.remove(ticket.getSpotId());
        activeTickets.remove(ticketId);
        return fee;
    }

    private ParkingSpot findAvailableSpot(VehicleType vehicleType) {
        SpotType requiredSpotType = mapVehicleTypeToSpotType(vehicleType);

        for(ParkingSpot parkingSpot : spots) {
            if(!occupiedSpots.contains(parkingSpot.getId()) && parkingSpot.getType() == requiredSpotType) {
                return parkingSpot;
            }
        }

        return null;
    }

    private SpotType mapVehicleTypeToSpotType(VehicleType vehicleType) {
        if(vehicleType == VehicleType.MOTORCYCLE) return SpotType.MOTORCYCLE;
        if(vehicleType == VehicleType.CAR) return SpotType.CAR;
        if(vehicleType == VehicleType.LARGE) return SpotType.LARGE;
        throw new RuntimeException("Unknow vehicle type");
    }

    private long computeFee(long entryTime, long exitTime) {
        long durationInMillis = exitTime - entryTime;
        long durationInHours = durationInMillis / (1000*60*60);

        if(durationInMillis % (1000*60*60)>0) {
            durationInHours++;
        }

        return durationInHours*hourlyRatesInCents;
    }
}
