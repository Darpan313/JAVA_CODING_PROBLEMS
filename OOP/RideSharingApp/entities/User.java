package OOP.RideSharingApp.entities;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class User {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    protected final int id;
    protected final String name;

    User(String name) {
        this.id = SEQ.getAndIncrement();
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
