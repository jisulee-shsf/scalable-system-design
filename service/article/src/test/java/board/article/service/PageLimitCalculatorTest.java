package board.article.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageLimitCalculatorTest {

    @Test
    void calculatePageLimitTest() {
        calculatePageLimitTest(1L, 30L, 10L, 301L);
        calculatePageLimitTest(10L, 30L, 10L, 301L);
        calculatePageLimitTest(11L, 30L, 10L, 601L);
    }

    void calculatePageLimitTest(Long page, Long pageSize, Long movablePageLimit, Long expected) {
        Long result = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageLimit);
        assertThat(result).isEqualTo(expected);
    }
}
