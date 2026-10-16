package OOP.Amazon.models;

public class Address {
    private final String street;
    private final String city;
    private final String province;
    private final String zipCode;

    public Address(String street, String city, String province, String zipCode) {
        this.street = street;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s %s", street, city, province, zipCode);
    }
}
