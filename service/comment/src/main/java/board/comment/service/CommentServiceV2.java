package board.comment.service;

import board.comment.entity.ArticleCommentCount;
import board.comment.entity.CommentPath;
import board.comment.entity.CommentV2;
import board.comment.repository.ArticleCommentRepository;
import board.comment.repository.CommentRepositoryV2;
import board.comment.service.request.CommentCreateRequestV2;
import board.comment.service.response.CommentPageResponseV2;
import board.comment.service.response.CommentResponseV2;
import board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentServiceV2 {

    private static final long MOVABLE_PAGE_LIMIT = 10L;

    private final Snowflake snowflake = new Snowflake();
    private final CommentRepositoryV2 commentRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Transactional
    public CommentResponseV2 create(CommentCreateRequestV2 request) {
        CommentV2 parent = findParent(request);
        CommentPath parentCommentPath = parent == null ? CommentPath.create("") : parent.getCommentPath();
        CommentV2 comment = commentRepository.save(
                CommentV2.create(
                        snowflake.nextId(),
                        request.getContent(),
                        request.getArticleId(),
                        request.getWriterId(),
                        parentCommentPath.createChildCommentPath(
                                commentRepository.findDescendantsTopPath(request.getArticleId(), parentCommentPath.getPath())
                                        .orElse(null))
                )
        );

        int result = articleCommentRepository.increase(request.getArticleId());
        if (result == 0) {
            articleCommentRepository.save(
                    ArticleCommentCount.init(request.getArticleId(), 1L)
            );
        }

        return CommentResponseV2.from(comment);
    }

    private CommentV2 findParent(CommentCreateRequestV2 request) {
        String parentPath = request.getParentPath();
        if (parentPath == null) {
            return null;
        }
        return commentRepository.findByArticleIdAndPath(request.getArticleId(), request.getParentPath())
                .filter(not(CommentV2::getDeleted))
                .orElseThrow();
    }

    public CommentResponseV2 read(Long commentId) {
        return CommentResponseV2.from(
                commentRepository.findById(commentId).orElseThrow()
        );
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
                .filter(not(CommentV2::getDeleted))
                .ifPresent(comment -> {
                    if (hasChildren(comment)) {
                        comment.delete();
                    } else {
                        delete(comment);
                    }
                });
    }

    private boolean hasChildren(CommentV2 comment) {
        return commentRepository.findDescendantsTopPath(comment.getArticleId(), comment.getCommentPath().getPath())
                .isPresent();
    }

    private void delete(CommentV2 comment) {
        commentRepository.delete(comment);
        articleCommentRepository.decrease(comment.getArticleId());
        if (!comment.isRoot()) {
            commentRepository.findByArticleIdAndPath(comment.getArticleId(), comment.getCommentPath().getParentPath())
                    .filter(CommentV2::getDeleted)
                    .filter(not(this::hasChildren))
                    .ifPresent(this::delete);
        }
    }

    public CommentPageResponseV2 readAll(Long articleId, Long page, Long pageSize) {
        return CommentPageResponseV2.of(
                commentRepository.findAll(articleId, pageSize, (page - 1) * pageSize).stream()
                        .map(CommentResponseV2::from)
                        .toList(),
                commentRepository.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, MOVABLE_PAGE_LIMIT))
        );
    }

    public List<CommentResponseV2> readAllInfiniteScroll(Long articleId, String lastPath, Long pageSize) {
        List<CommentV2> comments = lastPath == null ?
                commentRepository.findAllInfiniteScroll(articleId, pageSize) :
                commentRepository.findAllInfiniteScroll(articleId, lastPath, pageSize);
        return comments.stream()
                .map(CommentResponseV2::from)
                .toList();
    }

    public Long count(Long articleId) {
        return articleCommentRepository.findById(articleId)
                .map(ArticleCommentCount::getCommentCount)
                .orElse(0L);
    }
}
