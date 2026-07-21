package board.comment.api;

import board.comment.service.response.CommentPageResponse;
import board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

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
    void readAllTest() {
        CommentPageResponse pageResponse = restClient.get()
                .uri("/v1/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("pageResponse.getCommentCount() = " + pageResponse.getCommentCount());
        for (CommentResponse response : pageResponse.getCommentResponses()) {
            if (!response.getCommentId().equals(response.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }
    }

    /**
     * response.getCommentId() = 337753034013220864
     *  response.getCommentId() = 337753034147438600
     * response.getCommentId() = 337753034013220865
     *  response.getCommentId() = 337753034147438599
     * response.getCommentId() = 337753034013220866
     *  response.getCommentId() = 337753034147438601
     * response.getCommentId() = 337753034013220867
     *  response.getCommentId() = 337753034147438592
     * response.getCommentId() = 337753034017415168
     *  response.getCommentId() = 337753034147438597
     */

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> responses1 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("firstPage");
        for (CommentResponse response1 : responses1) {
            if (!response1.getCommentId().equals(response1.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("response1.getCommentId() = " + response1.getCommentId());
        }

        Long lastParentCommentId = responses1.getLast().getParentCommentId();
        Long lastCommentId = responses1.getLast().getCommentId();

        List<CommentResponse> responses2 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&lastParentCommentId={lastParentCommentId}&lastCommentId={lastCommentId}&pageSize=5", lastParentCommentId, lastCommentId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("secondPage");
        for (CommentResponse response2 : responses2) {
            if (!response2.getCommentId().equals(response2.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("response2.getCommentId() = " + response2.getCommentId());
        }
    }

    /**
     * firstPage
     * response1.getCommentId() = 337753034013220864
     * 	response1.getCommentId() = 337753034147438600
     * response1.getCommentId() = 337753034013220865
     * 	response1.getCommentId() = 337753034147438599
     * response1.getCommentId() = 337753034013220866
     * secondPage
     * 	response2.getCommentId() = 337753034147438601
     * response2.getCommentId() = 337753034013220867
     * 	response2.getCommentId() = 337753034147438592
     * response2.getCommentId() = 337753034017415168
     * 	response2.getCommentId() = 337753034147438597
     */

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
