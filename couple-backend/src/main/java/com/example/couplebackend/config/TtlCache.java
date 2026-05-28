package com.example.couplebackend.config;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;

public class TtlCache extends AbstractValueAdaptingCache {
    private final String name;
    private final long ttlNanos;
    private final ConcurrentMap<Object, Entry> store = new ConcurrentHashMap<>();

    public TtlCache(String name, Duration ttl) {
        super(true);
        this.name = name;
        this.ttlNanos = ttl.toNanos();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return store;
    }

    @Override
    protected Object lookup(Object key) {
        Entry entry = store.get(key);
        if (entry == null) {
            return null;
        }
        long now = System.nanoTime();
        if (entry.expired(now)) {
            store.remove(key, entry);
            return null;
        }
        return entry.value();
    }

    @Override
    public void put(Object key, Object value) {
        store.put(key, new Entry(toStoreValue(value), expiresAt()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        Cache.ValueWrapper valueWrapper = get(key);
        if (valueWrapper != null) {
            return (T) valueWrapper.get();
        }
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Throwable ex) {
            throw new Cache.ValueRetrievalException(key, valueLoader, ex);
        }
    }

    @Override
    public Cache.ValueWrapper putIfAbsent(Object key, Object value) {
        while (true) {
            long now = System.nanoTime();
            Entry existing = store.get(key);
            if (existing != null && !existing.expired(now)) {
                return toValueWrapper(existing.value());
            }
            if (existing != null) {
                store.remove(key, existing);
            }
            Entry next = new Entry(toStoreValue(value), expiresAt());
            if (store.putIfAbsent(key, next) == null) {
                return null;
            }
        }
    }

    @Override
    public void evict(Object key) {
        store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    private long expiresAt() {
        return System.nanoTime() + ttlNanos;
    }

    private record Entry(Object value, long expiresAt) {
        boolean expired(long now) {
            return now >= expiresAt;
        }
    }
}
