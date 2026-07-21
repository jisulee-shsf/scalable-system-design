package board.comment.service.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CommentPageResponse {

    private List<CommentResponse> commentResponses;
    private Long commentCount;

    public static CommentPageResponse of(List<CommentResponse> commentResponses, Long commentCount) {
        CommentPageResponse commentPageResponse = new CommentPageResponse();
        commentPageResponse.commentResponses = commentResponses;
        commentPageResponse.commentCount = commentCount;
        return commentPageResponse;
    }
}
