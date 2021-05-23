package io.realworld.backend.domain.aggregate.favourite;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ArticleFavouriteRepository
    extends CrudRepository<ArticleFavourite, ArticleFavouriteId> {

  class FavouriteCount {
    private final long articleId;
    private final long count;

    public FavouriteCount(long articleId, long count) {
      this.articleId = articleId;
      this.count = count;
    }

    public long getArticleId() {
      return articleId;
    }

    public long getCount() {
      return count;
    }
  }

  int countByIdArticleId(long articleId);

  @Query(
      "SELECT new io.realworld.backend.domain.aggregate.favourite.ArticleFavouriteRepository$"
          + "FavouriteCount(f.id.articleId, COUNT(*)) "
          + "FROM ArticleFavourite f WHERE f.id.articleId IN (:articleIds) GROUP BY f.id.articleId")
  List<FavouriteCount> countByIdArticleIds(List<Long> articleIds);

  List<ArticleFavourite> findByIdUserId(long userId);
}
