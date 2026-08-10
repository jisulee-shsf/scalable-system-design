package board.articleread.service.response;

import board.articleread.repository.ArticleQueryModel;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ArticleReadResponse {
    private Long articleId;
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long articleCommentCount;
    private Long articleLikeCount;
    private Long articleViewCount;

    public static ArticleReadResponse from(ArticleQueryModel model, Long articleViewCount) {
        ArticleReadResponse response = new ArticleReadResponse();
        response.articleId = model.getArticleId();
        response.title = model.getTitle();
        response.content = model.getContent();
        response.boardId = model.getBoardId();
        response.writerId = model.getWriterId();
        response.createdAt = model.getCreatedAt();
        response.modifiedAt = model.getModifiedAt();
        response.articleCommentCount = model.getArticleCommentCount();
        response.articleLikeCount = model.getArticleLikeCount();
        response.articleViewCount = articleViewCount;
        return response;
    }
}
