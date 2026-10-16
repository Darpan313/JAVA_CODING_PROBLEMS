package Amazon.LockerSystem;

public class Compartment {
    private Size size;
    private boolean occupied;

    public Compartment(Size size) {
        this.size = size;
        this.occupied = false;
    }

    public Size getSize() {
        return this.size;
    }

    public boolean isOccupied() {
        return this.occupied;
    }

    public void markOccupied() {
        this.occupied = true;
    }

    public void markFree() {
        this.occupied = false;
    }

    public void open() {

    }
}
