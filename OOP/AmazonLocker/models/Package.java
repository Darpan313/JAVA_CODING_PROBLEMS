package OOP.AmazonLocker.models;

import OOP.AmazonLocker.enums.Size;

public record Package(String packageId, String customerId, Size size) {
}
