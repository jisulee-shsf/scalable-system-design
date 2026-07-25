package board.comment.repository;

import board.comment.entity.CommentV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryV2 extends JpaRepository<CommentV2, Long> {

    @Query("select c from CommentV2 c where c.articleId = :articleId and c.commentPath.path = :path")
    Optional<CommentV2> findByArticleIdAndPath(
            @Param("articleId") Long articleId,
            @Param("path") String path
    );

    @Query(
            value = "select path " +
                    "from comment_v2 " +
                    "where article_id = :articleId " +
                    "and path > :pathPrefix " +
                    "and path like :pathPrefix% " +
                    "order by path desc " +
                    "limit 1",
            nativeQuery = true
    )
    Optional<String> findDescendantsTopPath(
            @Param("articleId") Long articleId,
            @Param("pathPrefix") String pathPrefix
    );

    @Query(
            value = "select comment_v2.* " +
                    "from (" +
                    "select comment_id " +
                    "from comment_v2 " +
                    "where article_id = :articleId " +
                    "order by path asc " +
                    "limit :limit offset :offset" +
                    ") t left join comment_v2 on t.comment_id = comment_v2.comment_id",
            nativeQuery = true
    )
    List<CommentV2> findAll(
            @Param("articleId") Long articleId,
            @Param("limit") Long limit,
            @Param("offset") Long offset
    );

    @Query(
            value = "select count(*) " +
                    "from (" +
                    "select comment_id " +
                    "from comment_v2 " +
                    "where article_id = :articleId " +
                    "limit :limit" +
                    ") t",
            nativeQuery = true
    )
    Long count(
            @Param("articleId") Long articleId,
            @Param("limit") Long limit
    );
}
