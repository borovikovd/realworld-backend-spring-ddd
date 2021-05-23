package io.realworld.backend.application;

import static io.realworld.backend.application.Util.validateBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import io.realworld.backend.application.service.ArticleService;
import io.realworld.backend.domain.aggregate.article.Article;
import io.realworld.backend.domain.aggregate.article.ArticleRepository;
import io.realworld.backend.domain.aggregate.comment.CommentRepository;
import io.realworld.backend.domain.aggregate.favourite.ArticleFavourite;
import io.realworld.backend.domain.aggregate.favourite.ArticleFavouriteId;
import io.realworld.backend.domain.aggregate.favourite.ArticleFavouriteRepository;
import io.realworld.backend.domain.aggregate.follow.FollowRelationRepository;
import io.realworld.backend.domain.aggregate.user.User;
import io.realworld.backend.domain.service.AuthenticationService;
import io.realworld.backend.rest.api.NewArticleData;
import io.realworld.backend.rest.api.NewArticleRequestData;
import java.util.Collections;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ArticleServiceTest {
  private @MonotonicNonNull ArticleService articleService;
  @Mock private @MonotonicNonNull ArticleRepository articleRepository;
  @Mock private @MonotonicNonNull FollowRelationRepository followRelationRepository;
  @Mock private @MonotonicNonNull ArticleFavouriteRepository articleFavouriteRepository;
  @Mock private @MonotonicNonNull CommentRepository commentRepository;
  @Mock private @MonotonicNonNull AuthenticationService authenticationService;

  @BeforeEach
  @RequiresNonNull({
    "articleRepository",
    "followRelationRepository",
    "articleFavouriteRepository",
    "commentRepository",
    "authenticationService"
  })
  public void setUp() {
    openMocks(this);
    given(authenticationService.getCurrentUser())
        .willReturn(Optional.of(new User("email@example.com", "example", "hash")));
    articleService =
        new ArticleService(
            articleRepository,
            followRelationRepository,
            articleFavouriteRepository,
            commentRepository,
            authenticationService);
  }

  @Test
  @RequiresNonNull({"articleService"})
  public void testCreateArticle() {
    final var req = new NewArticleRequestData();
    final var newArticle = new NewArticleData();
    newArticle.setTitle("tile");
    newArticle.setDescription("description");
    newArticle.setBody("body");
    newArticle.setTagList(Collections.singletonList("tag"));
    req.setArticle(newArticle);
    final var resp = articleService.createArticle(req);
    final var body = validateBody(resp);
    final var article = body.getArticle();
    assertThat(article.getSlug()).isNotBlank();
    assertThat(article.getTitle()).isNotBlank();
    assertThat(article.getDescription()).isNotBlank();
    assertThat(article.getBody()).isNotBlank();
    assertThat(article.getFavorited()).isFalse();
    assertThat(article.getFavoritesCount()).isEqualTo(0);
    assertThat(article.getTagList()).isEqualTo(Collections.singletonList("tag"));
    assertThat(article.getAuthor()).isNotNull();
    assertThat(article.getAuthor().getUsername()).isEqualTo("example");
    assertThat(article.getAuthor().getFollowing()).isFalse();
    assertThat(article.getCreatedAt()).isNotNull();
    assertThat(article.getUpdatedAt()).isNotNull();
  }

  @Test
  @RequiresNonNull({"articleService", "articleRepository"})
  public void testGetArticleBySlug() {
    final var article = new Article();
    article.setTitle("title");
    given(articleRepository.findBySlug(article.getSlug())).willReturn(Optional.of(article));
    final var resp = articleService.getArticle(article.getSlug());
    final var body = validateBody(resp);
    final var articleData = body.getArticle();
    assertThat(articleData.getSlug()).contains("title-");
  }

  @Test
  @RequiresNonNull({"articleService", "articleRepository", "articleFavouriteRepository"})
  public void testCreateArticleFavorite() {
    final var article = new Article();
    article.setTitle("title");
    given(articleRepository.findBySlug(article.getSlug())).willReturn(Optional.of(article));
    given(articleFavouriteRepository.findById(new ArticleFavouriteId(0, 0)))
        .willReturn(Optional.of(new ArticleFavourite(0, 0)));
    given(articleFavouriteRepository.countByIdArticleId(0)).willReturn(2);
    final var resp = articleService.createArticleFavorite(article.getSlug());
    final var body = validateBody(resp);
    final var articleData = body.getArticle();
    assertThat(articleData.getFavorited()).isTrue();
    assertThat(articleData.getFavoritesCount()).isEqualTo(2);
  }
}
