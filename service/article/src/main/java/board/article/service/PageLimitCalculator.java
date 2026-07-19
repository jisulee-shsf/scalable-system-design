package board.article.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageLimitCalculator {

    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageSize) {
        return ((page - 1) / movablePageSize + 1) * pageSize * movablePageSize + 1;
    }
}
