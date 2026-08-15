package board.articleread.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.random.RandomGenerator;

public class DataInitializer {

    RestClient articleRestClient = RestClient.create("http://localhost:9000");
    RestClient commentRestClient = RestClient.create("http://localhost:9001");
    RestClient likeRestClient = RestClient.create("http://localhost:9002");
    RestClient viewRestClient = RestClient.create("http://localhost:9003");

    @Test
    void initialize() {
        for (int i = 0; i < 30; i++) {
            Long articleId = createArticle();
            long commentCount = RandomGenerator.getDefault().nextLong(10);
            long likeCount = RandomGenerator.getDefault().nextLong(10);
            long viewCount = RandomGenerator.getDefault().nextLong(200);

            createComment(articleId, commentCount);
            like(articleId, likeCount);
            view(articleId, viewCount);
        }
    }

    Long createArticle() {
        return articleRestClient.post()
                .uri("/v1/articles")
                .body(new ArticleCreateRequest("title", "content", 1L, 1L))
                .retrieve()
                .body(ArticleResponse.class)
                .getArticleId();
    }

    void createComment(Long articleId, long commentCount) {
        while (commentCount-- > 0) {
            commentRestClient.post()
                    .uri("/v2/comments")
                    .body(new CommentCreateRequest(articleId, "content", 1L))
                    .retrieve();
        }
    }

    void like(Long articleId, long likeCount) {
        while (likeCount-- > 0) {
            likeRestClient.post()
                    .uri("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1", articleId, likeCount)
                    .retrieve();
        }
    }

    void view(Long articleId, long viewCount) {
        while (viewCount-- > 0) {
            viewRestClient.post()
                    .uri("/v1/article-views/articles/{articleId}/users/{userId}", articleId, viewCount)
                    .retrieve();
        }
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long boardId;
        private Long writerId;
    }

    @Getter
    static class ArticleResponse {
        private Long articleId;
    }

    @Getter
    @AllArgsConstructor
    static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long writerId;
    }
}
