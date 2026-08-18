package board.articleread.cache;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class OptimizedCacheTest {

    @Test
    void parseDataTest() {
        parseDataTest("data", 10L);
        parseDataTest(1L, 10L);
        parseDataTest(1, 10L);
        parseDataTest(new TestClass("test"), 10L);
    }

    void parseDataTest(Object data, long ttlSeconds) {
        OptimizedCache optimizedCache = OptimizedCache.of(data, Duration.ofSeconds(ttlSeconds));
        System.out.println("optimizedCache = " + optimizedCache);

        Object resolvedData = optimizedCache.parseData(data.getClass());
        System.out.println("resolvedData = " + resolvedData);

        assertThat(resolvedData).isEqualTo(data);
    }

    @Test
    void isExpiredTest() {
        assertThat(OptimizedCache.of("test", Duration.ofSeconds(-10L)).isExpired()).isTrue();
        assertThat(OptimizedCache.of("test", Duration.ofSeconds(10L)).isExpired()).isFalse();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    static class TestClass {
        String data;
    }
}
