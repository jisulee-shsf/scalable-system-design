package board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class ViewClient {
    @Value("${endpoints.board-view-service-url}")
    private String viewServiceClient;

    private RestClient restClient;

    @PostConstruct
    void initRestClient() {
        restClient = RestClient.create(viewServiceClient);
    }

    public long count(Long articleId) {
        try {
            return restClient.get()
                    .uri("/v1/view/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[ViewClient.read] articleId={}", articleId, e);
            return 0;
        }
    }
}
