package board.article.api;


import board.article.service.response.ArticlePageResponse;
import board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ArticleApiTest {

    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        ArticleCreateRequest request = new ArticleCreateRequest("title", "content", 1L, 1L);
        ArticleResponse response = create(request);
        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        ArticleResponse response = read();
        System.out.println("response = " + response);
    }

    ArticleResponse read() {
        return restClient.get()
                .uri("/v1/articles/{articleId}", 336392512069443584L)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readAllTest() {
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&page=50000&pageSize=30")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("response.getArticleCount() = " + response.getArticleCount());
        for (ArticleResponse articleResponse : response.getArticleResponses()) {
            System.out.println("articleResponse.getArticleId() = " + articleResponse.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest() {
        List<ArticleResponse> responses = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=30")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("firstPage");
        for (ArticleResponse response : responses) {
            System.out.println("response.getArticleId() = " + response.getArticleId());
        }

        Long lastArticleId = responses.getLast().getArticleId();
        List<ArticleResponse> responses2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&lastArticleId={lastArticleId}&pageSize=30", lastArticleId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("secondPage");
        for (ArticleResponse response : responses2) {
            System.out.println("response.getArticleId() = " + response.getArticleId());
        }
    }

    @Test
    void updateTest() {
        ArticleUpdateRequest request = new ArticleUpdateRequest("title2", "content2");
        ArticleResponse response = update(336392512069443584L, request);
        System.out.println("response = " + response);
    }

    ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        return restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/v1/articles/{articleId}", 336392512069443584L)
                .retrieve();
    }

    @Test
    void countTest() {
        ArticleResponse response = create(new ArticleCreateRequest("title", "content", 2L, 1L));

        Long count1 = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count1 = " + count1);

        restClient.delete()
                .uri("/v1/articles/{articleId}", response.getArticleId())
                .retrieve();

        Long count2 = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count2 = " + count2);
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
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}
