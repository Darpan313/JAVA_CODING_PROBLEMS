package Auvik.Cache.Level3;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

public final class TTLCache<K, V> implements AutoCloseable {

    private record  Entry<V>(V value, long expiresAtNanos) {}

    private final ConcurrentHashMap<K, Entry<V>> map = new ConcurrentHashMap<>();
    private final long ttlNanos;
    private final LongSupplier clock;
    private final ScheduledExecutorService cleaner;

    public TTLCache(Duration ttl, Duration cleanUpEvery) {
        this(ttl, cleanUpEvery, System::nanoTime);
    }

    TTLCache(Duration ttl, Duration cleanupEvery, LongSupplier clock) {
        if(ttl.isZero() || ttl.isNegative()) throw new IllegalArgumentException("ttl must be > 0");
        this.ttlNanos = ttl.toNanos();
        this.clock = clock;
        this.cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ttl-cache-cleaner");
            t.setDaemon(true);
            return t;
        });
        long period = cleanupEvery.toMillis();
        cleaner.scheduleAtFixedRate(this::removeExpired, period, period, TimeUnit.MILLISECONDS);
    }

    public void put(K key, V value) {
        Objects.requireNonNull(value);
        map.put(key, new Entry<>(value, clock.getAsLong()+ttlNanos));
    }

    public Optional<V> get(K key) {
        Entry<V> e = map.get(key);
        if (e==null) return Optional.empty();
        if(isExpired(e, clock.getAsLong())) {
            map.remove(key, e);
            return Optional.empty();
        }
        return Optional.of(e.value);
    }

    private static boolean isExpired(Entry<?> e, long now) {
        return now - e.expiresAtNanos() >= 0;
    }

    private void removeExpired() {
        try {
            long now = clock.getAsLong();
            for(Map.Entry<K, Entry<V>> en : map.entrySet()) {
                if(isExpired(en.getValue(), now)) {
                    map.remove(en.getKey(), en.getValue());
                }
            }
        } catch (RuntimeException e) {

        }
    }
    @Override
    public void close() throws Exception {
        cleaner.shutdownNow();
    }
}
