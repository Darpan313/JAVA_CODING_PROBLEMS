package OOP.AmazonLocker.models;

import OOP.AmazonLocker.enums.DeliveryStatus;
import OOP.AmazonLocker.enums.Size;

import java.time.Instant;

public final class Delivery {
    final String deliveryId, packageId, locationId, customerId;
    final Size size;
    volatile String lockerId;
    volatile DeliveryStatus status = DeliveryStatus.RESERVED;
    volatile String hashCode;
    volatile Instant deliveredAt, pickUpDeadLine, reserveDeadLine;
    volatile int failedAttempts = 0;

    public Delivery(String deliveryId, Package p, String locationId, String customerId, Instant reservedDeadline) {
        this.deliveryId = deliveryId;
        this.packageId = p.packageId();
        this.locationId = locationId;
        this.customerId = customerId;
        this.size = p.size();
        this.reserveDeadLine = reservedDeadline;
    }
}
