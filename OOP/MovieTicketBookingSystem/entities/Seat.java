package OOP.MovieTicketBookingSystem.entities;

import OOP.MovieTicketBookingSystem.enums.SeatStatus;
import OOP.MovieTicketBookingSystem.enums.SeatType;

public class Seat {
    private final String id;
    private final int row;
    private final int col;
    private final SeatType type;
    private SeatStatus status;

    public Seat(String id, int row, int col, SeatType type) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.type = type;
        this.status = SeatStatus.AVAILABLE;
    }

    public String getId() {
        return this.id;
    }

    public SeatType getType() {
        return type;
    }

    public SeatStatus getStatus() {
        return this.status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}
