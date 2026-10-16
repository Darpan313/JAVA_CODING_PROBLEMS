package OOP.AmazonLocker.models;

import OOP.AmazonLocker.enums.Size;

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LockerLocation {
    private final String locationId, address;
    private final EnumMap<Size, List<Locker>> bySize = new EnumMap<>(Size.class);

    public LockerLocation(String locationId, String address) {
        this.locationId = locationId;
        this.address = address;

        for(Size s : Size.values()) {
            bySize.put(s, new CopyOnWriteArrayList<>());
        }
    }

    public String getLocationId() {
        return locationId;
    }

    public String getAddress() {
        return address;
    }

    public void addLocker(Locker l) {
        bySize.get(l.getSize()).add(l);
    }

    public List<Locker> lockersOfSize(Size s) {
        return bySize.get(s);
    }
}
