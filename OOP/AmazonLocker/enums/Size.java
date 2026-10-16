package OOP.AmazonLocker.enums;

public enum Size {
    S,
    M,
    L,
    XL;

    boolean fits(Size pkg) {
        return this.ordinal()>=pkg.ordinal();
    }
}
