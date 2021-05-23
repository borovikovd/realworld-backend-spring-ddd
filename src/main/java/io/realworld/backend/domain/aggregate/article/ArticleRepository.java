package io.realworld.backend.domain.aggregate.article;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {
  Optional<Article> findBySlug(String slug);

  List<Article> findByAuthorIdIn(Collection<Long> authorIds, Pageable pageable);

  @Query(
      "SELECT DISTINCT a FROM Article a "
          + "LEFT JOIN a.tags t "
          + "LEFT JOIN a.author p "
          + "LEFT JOIN ArticleFavourite f ON a.id = f.id.articleId "
          + "LEFT JOIN User fu ON fu.id = f.id.userId "
          + "WHERE "
          + "(:tag IS NULL OR :tag IN t) AND "
          + "(:author IS NULL OR p.username = :author) AND "
          + "(:favorited IS NULL OR fu.username = :favorited)")
  List<Article> findByFilters(
      @Nullable String tag, @Nullable String author, @Nullable String favorited, Pageable pageable);

  @Query(
      "SELECT COUNT(DISTINCT a.id) FROM Article a "
          + "LEFT JOIN a.tags t "
          + "LEFT JOIN a.author p "
          + "LEFT JOIN ArticleFavourite f ON a.id = f.id.articleId "
          + "LEFT JOIN User fu ON fu.id = f.id.userId "
          + "WHERE "
          + "(:tag IS NULL OR :tag IN t) AND "
          + "(:author IS NULL OR p.username = :author) AND "
          + "(:favorited IS NULL OR fu.username = :favorited)")
  int countByFilter(@Nullable String tag, @Nullable String author, @Nullable String favorited);

  int countByAuthorIdIn(Collection<Long> authorIds);

  @Query("SELECT t from Article a LEFT JOIN a.tags t")
  List<String> findAllTags();
}
