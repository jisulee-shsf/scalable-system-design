package board.view.repository;

import board.view.entity.ArticleViewCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArticleViewCountBackUpRepositoryTest {

    @Autowired
    private ArticleViewCountBackUpRepository articleViewCountBackUpRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void updateViewCountTest() {
        // given
        ArticleViewCount articleViewCount1 = ArticleViewCount.init(1L, 1L);
        articleViewCountBackUpRepository.save(articleViewCount1);

        // when
        int result1 = articleViewCountBackUpRepository.updateViewCount(1L, 100L);
        int result2 = articleViewCountBackUpRepository.updateViewCount(1L, 300L);
        int result3 = articleViewCountBackUpRepository.updateViewCount(1L, 200L);

        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(1);
        assertThat(result3).isEqualTo(0);

        ArticleViewCount articleViewCount2 = articleViewCountBackUpRepository.findById(articleViewCount1.getArticleId()).get();
        assertThat(articleViewCount2.getViewCount()).isEqualTo(300L);
    }
}
