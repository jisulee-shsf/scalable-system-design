package board.comment.service.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CommentPageResponseV2 {

    private List<CommentResponseV2> commentResponses;
    private Long commentCount;

    public static CommentPageResponseV2 of(List<CommentResponseV2> commentResponses, Long commentCount) {
        CommentPageResponseV2 commentPageResponse = new CommentPageResponseV2();
        commentPageResponse.commentResponses = commentResponses;
        commentPageResponse.commentCount = commentCount;
        return commentPageResponse;
    }
}
