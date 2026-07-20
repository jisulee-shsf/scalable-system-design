package board.comment.api;

import board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void createTest() {
        CommentResponse response1 = create(new CommentCreateRequest(1L, "content1", null, 1L));
        CommentResponse response2 = create(new CommentCreateRequest(1L, "content2", response1.getCommentId(), 1L));
        CommentResponse response3 = create(new CommentCreateRequest(1L, "content3", response1.getCommentId(), 1L));

        System.out.println("commentId = %s".formatted(response1.getCommentId()));
        System.out.println("\tcommentId = %s".formatted(response2.getCommentId()));
        System.out.println("\tcommentId = %s".formatted(response3.getCommentId()));

//        commentId = 337673485219987456
//          commentId = 337673487338110976
//          commentId = 337673487472328704
    }

    CommentResponse create(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void readTest() {
        CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 337673485219987456L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println("response = " + response);
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/v1/comments/{commentId}", 337673487472328704L)
                .retrieve();
    }

    @Getter
    @AllArgsConstructor
    static class CommentCreateRequest {

        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }
}
