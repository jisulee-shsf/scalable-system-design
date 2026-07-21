package board.comment.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class PageLimitCalculator {

    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageLimit) {
        return ((page - 1) / movablePageLimit + 1) * pageSize * movablePageLimit + 1;
    }
}
