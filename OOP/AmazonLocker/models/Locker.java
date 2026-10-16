package OOP.AmazonLocker.models;

import OOP.AmazonLocker.enums.LockerStatus;
import OOP.AmazonLocker.enums.Size;

import java.util.concurrent.locks.ReentrantLock;

public class Locker {
    private final String lockerId;
    private final Size size;
    private LockerStatus status = LockerStatus.AVAILABLE;
    private String currentDeliveryId;
    private final ReentrantLock lock = new ReentrantLock();

    public Locker(String lockerId, Size size) {
        this.lockerId = lockerId;
        this.size = size;
    }

    public Size getSize() {
        return size;
    }

    // under lock
    LockerStatus status() {
        return status;
    }

    String currentDeliveryId() {
        return currentDeliveryId;
    }

    void reserve(String deliveryId) {
        status = LockerStatus.RESERVED;
        currentDeliveryId = deliveryId;
    }

    void occupy() {
        status = LockerStatus.OCCUPIED;
    }

    void free() {
        status = LockerStatus.AVAILABLE;
        currentDeliveryId = null;
    }

    void outOfService() {
        status = LockerStatus.OUT_OF_SERVICE;
        currentDeliveryId = null;
    }

    void open() {
        System.out.println("[door] " + lockerId + " opens");
    }
}
