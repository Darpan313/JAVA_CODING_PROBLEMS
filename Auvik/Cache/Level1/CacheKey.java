package Auvik.Cache.Level1;

import java.util.Objects;

/*
    A record gives you final fields, equals, hashCode and toString for free.
 */
public record CacheKey(String tenant, String id) {
    public CacheKey {
        Objects.requireNonNull(tenant);
        Objects.requireNonNull(id);
    }
}
