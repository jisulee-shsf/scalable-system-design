package board.comment.api;

import board.comment.service.response.CommentResponseV2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class CommentApiV2Test {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponseV2 response1 = createTest(new CommentCreateRequestV2(1L, "content", null, 1L));
        CommentResponseV2 response2 = createTest(new CommentCreateRequestV2(1L, "content", response1.getPath(), 1L));
        CommentResponseV2 response3 = createTest(new CommentCreateRequestV2(1L, "content", response2.getPath(), 1L));

        System.out.println("response1.getPath() = " + response1.getPath());
        System.out.println("response1.getCommentId() = " + response1.getCommentId());
        System.out.println("\tresponse2.getPath() = " + response2.getPath());
        System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
        System.out.println("\t\tresponse3.getPath() = " + response3.getPath());
        System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());

        /**
         * response1.getPath() = 00003
         * response1.getCommentId() = 339195322932953088
         * 	response2.getPath() = 0000300000
         * 	response2.getCommentId() = 339195323524349952
         * 		response3.getPath() = 000030000000000
         * 		response3.getCommentId() = 339195323700510720
         */
    }

    CommentResponseV2 createTest(CommentCreateRequestV2 request) {
        return restClient.post()
                .uri("/v2/comments")
                .body(request)
                .retrieve()
                .body(CommentResponseV2.class);
    }

    @Test
    void readTest() {
        CommentResponseV2 response = restClient.get()
                .uri("/v2/comments/{commentId}", 339195322932953088L)
                .retrieve()
                .body(CommentResponseV2.class);

        System.out.println("response = " + response);
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/v2/comments/{commentId}", 339195322932953088L)
                .retrieve();
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequestV2 {

        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }
}
