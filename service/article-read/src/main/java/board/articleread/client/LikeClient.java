package board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class LikeClient {
    @Value("${endpoints.board-like-service.url}")
    private String likeServiceClient;

    private RestClient restClient;

    @PostConstruct
    void initRestClient() {
        restClient = RestClient.create(likeServiceClient);
    }

    public long count(Long articleId) {
        try {
            return restClient.get()
                    .uri("/v1/article-likes/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[LikeClient.read] articleId={}", articleId, e);
            return 0;
        }
    }
}
