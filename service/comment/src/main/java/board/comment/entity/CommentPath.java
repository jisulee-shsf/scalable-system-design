package board.comment.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentPath {

    private String path;

    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int DEPTH_CHUNK_SIZE = 5;
    private static final int MAX_DEPTH = 5;

    private static final String MIN_CHUNK = String.valueOf(CHARSET.charAt(0)).repeat(DEPTH_CHUNK_SIZE);
    private static final String MAX_CHUNK = String.valueOf(CHARSET.charAt(CHARSET.length() - 1)).repeat(DEPTH_CHUNK_SIZE);

    public static CommentPath create(String path) {
        if (isDepthOverflowed(path)) {
            throw new IllegalStateException("depth overflowed");
        }
        CommentPath commentPath = new CommentPath();
        commentPath.path = path;
        return commentPath;
    }

    private static boolean isDepthOverflowed(String path) {
        return calculateDepth(path) > MAX_DEPTH;
    }

    private static int calculateDepth(String path) {
        return path.length() / DEPTH_CHUNK_SIZE;
    }

    public int getDepth() {
        return calculateDepth(path);
    }

    public boolean isRoot() {
        return calculateDepth(path) == 1;
    }

    public String getParentPath() {
        return path.substring(0, (path.length() - DEPTH_CHUNK_SIZE));
    }

    public CommentPath createChildCommentPath(String descendantsTopPath) {
        if (descendantsTopPath == null) {
            return CommentPath.create(path + MIN_CHUNK);
        }
        String childrenTopPath = findChildrenTopPath(descendantsTopPath);
        return CommentPath.create(increase(childrenTopPath));
    }

    private String findChildrenTopPath(String descendantsTopPath) {
        return descendantsTopPath.substring(0, (getDepth() + 1) * DEPTH_CHUNK_SIZE);
    }

    private String increase(String path) {
        String lastChunk = path.substring(path.length() - DEPTH_CHUNK_SIZE);
        if (isChunkOverflowed(lastChunk)) {
            throw new IllegalStateException("chunk overflowed");
        }

        int value = 0;
        int charsetLength = CHARSET.length();

        for (char c : lastChunk.toCharArray()) {
            value = value * charsetLength + CHARSET.indexOf(c);
        }
        value += 1;

        String result = "";
        for (int i = 0; i < DEPTH_CHUNK_SIZE; i++) {
            result = CHARSET.charAt(value % charsetLength) + result;
            value /= charsetLength;
        }

        return path.substring(0, path.length() - DEPTH_CHUNK_SIZE) + result;
    }

    private boolean isChunkOverflowed(String lastChunk) {
        return lastChunk.equals(MAX_CHUNK);
    }
}
