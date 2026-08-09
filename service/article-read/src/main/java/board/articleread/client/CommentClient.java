package board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class CommentClient {
    @Value("${endpoints.board-comment-service-url}")
    private String commentServiceClient;

    private RestClient restClient;

    @PostConstruct
    void initRestClient() {
        restClient = RestClient.create(commentServiceClient);
    }

    public long count(Long articleId) {
        try {
            return restClient.get()
                    .uri("/v2/comment/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[CommentClient.read] articleId={}", articleId, e);
            return 0;
        }
    }
}
