package board.comment.api;

import board.comment.service.response.CommentPageResponseV2;
import board.comment.service.response.CommentResponseV2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

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

    @Test
    void readAllTest() {
        CommentPageResponseV2 pageResponse = restClient.get()
                .uri("/v2/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponseV2.class);

        System.out.println("response.getCommentCount() = " + pageResponse.getCommentCount());
        for (CommentResponseV2 response : pageResponse.getCommentResponses()) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }

        /**
         * response.getCommentId() = 339320271143370752
         * response.getCommentId() = 339320271290171394
         * response.getCommentId() = 339320271302754316
         * response.getCommentId() = 339320271302754325
         * response.getCommentId() = 339320271302754332
         * response.getCommentId() = 339320271315337235
         * response.getCommentId() = 339320271319531525
         * response.getCommentId() = 339320271319531531
         * response.getCommentId() = 339320271319531537
         * response.getCommentId() = 339320271319531543
         */
    }

    @Test
    void readAllInfiniteScrollTest() {
        List<CommentResponseV2> responses1 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponseV2>>() {
                });

        System.out.println("firstPage");
        for (CommentResponseV2 response : responses1) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }

        String lastPath = responses1.getLast().getPath();
        List<CommentResponseV2> responses2 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&lastPath={lastPath}&pageSize=5", lastPath)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponseV2>>() {
                });

        System.out.println("secondPage");
        for (CommentResponseV2 response : responses2) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }

        /**
         * firstPage
         * response.getCommentId() = 339320271143370752
         * response.getCommentId() = 339320271290171394
         * response.getCommentId() = 339320271302754316
         * response.getCommentId() = 339320271302754325
         * response.getCommentId() = 339320271302754332
         * secondPage
         * response.getCommentId() = 339320271315337235
         * response.getCommentId() = 339320271319531525
         * response.getCommentId() = 339320271319531531
         * response.getCommentId() = 339320271319531537
         * response.getCommentId() = 339320271319531543
         */
    }

    @Test
    void countTest() {
        CommentResponseV2 response = createTest(new CommentCreateRequestV2(2L, "content", null, 1L));

        Long count1 = restClient.get()
                .uri("/v1/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count1 = " + count1);

        restClient.delete()
                .uri("/v2/comments/{commentId}", response.getCommentId())
                .retrieve();

        Long count2 = restClient.get()
                .uri("/v1/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count2 = " + count2);
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
