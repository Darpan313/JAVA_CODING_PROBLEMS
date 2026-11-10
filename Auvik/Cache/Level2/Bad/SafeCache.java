package Auvik.Cache.Level2.Bad;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
/*
    CODE_REVIEW:
    1. Race -- hits++ is not atomic
    2. Visibility -- gteHits() may never see updates
    3. Race -- check-then-act in getOrCompute
    4. Crash(deadlock) -- exception while holding the lock. put() throws on a blank key after lock(), so unlock() never
       runs. Every later puts blocks forever.
    5. Bug --  mixed locking strategies. The write lock guards only put; get, getOrCompute and replaceAll ignore it. Sim-
                ilary synchronized on replaceAll doesn't stop unsynchronized get calls.
    6. Race(logical) -- replaceAll is not atomic for readers. Between clear() and putAll(), readers see an empty or
                        half-filled cache. Every read becomes miss, which can stampeded database.
    7. Race -- asMap() leaks the internal map.
    8. Crash -- key.isBlank() on a null key throws NPE before your intended message.
 */
public class SafeCache {
    private final Map<String, String> data = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private int hits = 0;

    public String get(String key) {
        String v = data.get(key);
        if (v != null) hits++;
        return v;
    }

    public String getOrCompute(String key, Supplier<String> supplier) {
        if(!data.containsKey(key)) {
            data.put(key, supplier.get());
        }
        return data.get(key);
    }

    public void put(String key, String value) {
        lock.writeLock().lock();
        if(key.isBlank()) {
            throw new IllegalArgumentException("blank key");
        }

        data.put(key, value);
        lock.writeLock().unlock();
    }

    public synchronized void replaceAll(Map<String, String> fresh) {
        data.clear();
        data.putAll(fresh);
    }

    public int getHits() {
        return hits;
    }

    public Map<String, String> asMap() {
        return data;
    }
}
