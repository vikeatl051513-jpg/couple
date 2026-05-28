package com.example.couplebackend.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class TtlCacheTest {
    @Test
    void returnsCachedValueUntilTtlExpires() throws Exception {
        TtlCache cache = new TtlCache("test", Duration.ofMillis(20));

        cache.put("key", "value");

        assertThat(cache.get("key", String.class)).isEqualTo("value");

        Thread.sleep(30);

        assertThat(cache.get("key")).isNull();
    }

    @Test
    void getWithCallableStoresLoadedValue() {
        TtlCache cache = new TtlCache("test", Duration.ofMinutes(1));
        AtomicInteger loaderCalls = new AtomicInteger();

        String first = cache.get("key", () -> "value-" + loaderCalls.incrementAndGet());
        String second = cache.get("key", () -> "value-" + loaderCalls.incrementAndGet());

        assertThat(first).isEqualTo("value-1");
        assertThat(second).isEqualTo("value-1");
        assertThat(loaderCalls).hasValue(1);
    }

    @Test
    void clearRemovesStoredValues() {
        TtlCache cache = new TtlCache("test", Duration.ofMinutes(1));
        cache.put("key", "value");

        cache.clear();

        assertThat(cache.get("key")).isNull();
    }
}
