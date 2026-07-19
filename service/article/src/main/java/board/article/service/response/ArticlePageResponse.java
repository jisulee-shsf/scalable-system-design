package board.article.service.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticlePageResponse {

    private List<ArticleResponse> articleResponses;
    private Long articleCount;

    public static ArticlePageResponse of(List<ArticleResponse> articleResponses, Long articleCount) {
        ArticlePageResponse articlePageResponse = new ArticlePageResponse();
        articlePageResponse.articleResponses = articleResponses;
        articlePageResponse.articleCount = articleCount;
        return articlePageResponse;
    }
}
