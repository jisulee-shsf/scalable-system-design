package board.articleread.repository;

import board.common.dataserializer.DataSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArticleQueryModelRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "article-read::article::%s";

    public void create(ArticleQueryModel model, Duration ttl) {
        redisTemplate.opsForValue()
                .set(generateKey(model), DataSerializer.serialize(model), ttl);
    }

    public void update(ArticleQueryModel model) {
        redisTemplate.opsForValue()
                .setIfPresent(generateKey(model), DataSerializer.serialize(model));
    }

    public void delete(Long articleId) {
        redisTemplate.delete(generateKey(articleId));
    }

    public Optional<ArticleQueryModel> read(Long articleId) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(generateKey(articleId))
        ).map(json -> DataSerializer.deserialize(json, ArticleQueryModel.class));
    }

    private String generateKey(ArticleQueryModel model) {
        return generateKey(model.getArticleId());
    }

    private String generateKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }
}
