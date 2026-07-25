package board.comment.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentPathTest {

    @Test
    void createChildCommentPathTest() {
        // <-
        createChildCommentPathTest(CommentPath.create(""), null, "00000");

        // 00000
        //     00000 <-
        createChildCommentPathTest(CommentPath.create("00000"), null, "0000000000");

        // 00000
        //     00000
        //         00000
        //     00001 <-
        createChildCommentPathTest(CommentPath.create("00000"), "000000000000000", "0000000001");

        // 00000
        //     00000
        //         00000
        //             00000
        //     00001 <-
        createChildCommentPathTest(CommentPath.create("00000"), "00000000000000000000", "0000000001");
    }

    void createChildCommentPathTest(CommentPath commentPath, String descendantsTopPath, String expectedChildPath) {
        CommentPath childCommentPath = commentPath.createChildCommentPath(descendantsTopPath);
        assertThat(childCommentPath.getPath()).isEqualTo(expectedChildPath);
    }

    @Test
    void createChildCommentPathIfMaxDepthTest() {
        assertThatThrownBy(() ->
                CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null)
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createChildCommentPathIfChunkOverflowTest() {
        assertThatThrownBy(() ->
                CommentPath.create("").createChildCommentPath("zzzzz")
        ).isInstanceOf(IllegalStateException.class);
    }
}
