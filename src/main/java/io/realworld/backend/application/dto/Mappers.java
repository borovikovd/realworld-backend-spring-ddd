package io.realworld.backend.application.dto;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.realworld.backend.domain.aggregate.article.Article;
import io.realworld.backend.domain.aggregate.comment.Comment;
import io.realworld.backend.domain.aggregate.user.User;
import io.realworld.backend.rest.api.ArticleData;
import io.realworld.backend.rest.api.CommentData;
import io.realworld.backend.rest.api.MultipleArticlesResponseData;
import io.realworld.backend.rest.api.MultipleCommentsResponseData;
import io.realworld.backend.rest.api.NewArticleData;
import io.realworld.backend.rest.api.NewCommentData;
import io.realworld.backend.rest.api.ProfileData;
import io.realworld.backend.rest.api.ProfileResponseData;
import io.realworld.backend.rest.api.SingleArticleResponseData;
import io.realworld.backend.rest.api.SingleCommentResponseData;
import io.realworld.backend.rest.api.TagsResponseData;
import io.realworld.backend.rest.api.UpdateArticleData;
import io.realworld.backend.rest.api.UpdateUserData;
import io.realworld.backend.rest.api.UserData;
import io.realworld.backend.rest.api.UserResponseData;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Mappers {

  /** Constructs UserResponseData response. */
  public static UserResponseData toUserResponse(User u, @Nullable String token) {
    final var userData = new UserData();
    userData.setUsername(u.getUsername());
    userData.setEmail(u.getEmail());
    u.getBio().ifPresent(userData::setBio);
    u.getImage().ifPresent(userData::setImage);
    if (token != null) {
      userData.setToken(token);
    }
    final var res = new UserResponseData();
    res.setUser(userData);
    return res;
  }

  /** Updates user. * */
  public static void updateUser(User user, UpdateUserData update) {
    final var email = update.getEmail();
    if (email != null) {
      user.setEmail(email);
    }
    final var username = update.getUsername();
    if (username != null) {
      user.setUsername(username);
    }
    final var bio = update.getBio();
    if (bio != null) {
      user.setBio(bio);
    }
    final var image = update.getImage();
    if (image != null) {
      user.setImage(image);
    }
  }

  /** Constructs ProfileResponseData response. */
  public static ProfileResponseData toProfileResponse(User user, boolean isFollowing) {
    final var profileResponse = new ProfileResponseData();
    final ProfileData profile = toProfile(user, isFollowing);
    profileResponse.setProfile(profile);
    return profileResponse;
  }

  /** Constructs ProfileData response. */
  public static ProfileData toProfile(User user, boolean isFollowing) {
    final var profile = new ProfileData();
    profile.setUsername(user.getUsername());
    user.getBio().ifPresent(profile::setBio);
    user.getImage().ifPresent(profile::setImage);
    profile.setFollowing(isFollowing);
    return profile;
  }

  public static class FavouriteInfo {
    private final boolean isFavorited;
    private final int favoritesCount;

    public FavouriteInfo(boolean isFavorited, int favoritesCount) {
      this.isFavorited = isFavorited;
      this.favoritesCount = favoritesCount;
    }

    public boolean isFavorited() {
      return isFavorited;
    }

    public int getFavoritesCount() {
      return favoritesCount;
    }
  }

  /** Constructs SingleArticleResponseData response. */
  public static SingleArticleResponseData toSingleArticleResponse(
      Article article, FavouriteInfo favouriteInfo, boolean isFollowingAuthor) {
    final var resp = new SingleArticleResponseData();
    final ArticleData articleData = toArticleData(article, favouriteInfo, isFollowingAuthor);
    resp.setArticle(articleData);
    return resp;
  }

  /** Constructs Article from the request. */
  public static Article fromNewArticleData(NewArticleData newArticleData, User user) {
    final var article = new Article();
    article.setTitle(newArticleData.getTitle());
    article.setDescription(newArticleData.getDescription());
    article.setBody(newArticleData.getBody());
    article.setAuthor(user);
    article.setTags(ImmutableSet.copyOf(newArticleData.getTagList()));
    return article;
  }

  /** Updates article. */
  public static void updateArticle(Article article, UpdateArticleData updateArticleData) {
    final var title = updateArticleData.getTitle();
    if (title != null) {
      article.setTitle(title);
    }
    final var description = updateArticleData.getDescription();
    if (description != null) {
      article.setDescription(description);
    }
    final var body = updateArticleData.getBody();
    if (body != null) {
      article.setBody(body);
    }
  }

  /** Constructs SingleCommentResponseData response. */
  public static SingleCommentResponseData toSingleCommentResponseData(
      Comment comment, boolean isFollowingAuthor) {
    final var resp = new SingleCommentResponseData();
    final var commentData = new CommentData();
    commentData.setId((int) comment.getId());
    commentData.setAuthor(toProfile(comment.getAuthor(), isFollowingAuthor));
    commentData.setBody(comment.getBody());
    commentData.setCreatedAt(comment.getCreatedAt().atOffset(ZoneOffset.UTC));
    commentData.setUpdatedAt(comment.getUpdatedAt().atOffset(ZoneOffset.UTC));
    resp.setComment(commentData);
    return resp;
  }

  /** Constructs Comment from the request. */
  public static Comment fromNewCommentData(
      NewCommentData commentData, Article article, User author) {
    final var comment = new Comment();
    comment.setArticle(article);
    comment.setAuthor(author);
    comment.setBody(commentData.getBody());
    return comment;
  }

  /** Constructs MultipleCommentsResponseData response. */
  public static MultipleCommentsResponseData toMultipleCommentsResponseData(
      Collection<Comment> comments, Set<Long> followingIds) {
    final var commentsResponseData = new MultipleCommentsResponseData();
    final var commentDataList =
        comments.stream()
            .map(
                c -> {
                  final var commentData = new CommentData();
                  commentData.setId((int) c.getId());
                  commentData.setBody(c.getBody());
                  commentData.setAuthor(
                      toProfile(c.getAuthor(), followingIds.contains(c.getAuthor().getId())));
                  commentData.setCreatedAt(c.getCreatedAt().atOffset(ZoneOffset.UTC));
                  commentData.setUpdatedAt(c.getUpdatedAt().atOffset(ZoneOffset.UTC));
                  return commentData;
                })
            .collect(Collectors.toList());
    commentsResponseData.setComments(commentDataList);
    return commentsResponseData;
  }

  public static class MultipleFavouriteInfo {
    private final Set<Long> favouritedArticleIds;
    private final Map<Long, Long> favouritedCountByArticleId;

    public MultipleFavouriteInfo(
        Set<Long> favouritedArticleIds, Map<Long, Long> favouritedCountByArticleId) {
      this.favouritedArticleIds = favouritedArticleIds;
      this.favouritedCountByArticleId = favouritedCountByArticleId;
    }

    public Set<Long> getFavouritedArticleIds() {
      return favouritedArticleIds;
    }

    public Map<Long, Long> getFavouritedCountByArticleId() {
      return favouritedCountByArticleId;
    }
  }

  /** Constructs MultipleArticlesResponseData response. */
  public static MultipleArticlesResponseData toMultipleArticlesResponseData(
      Collection<Article> articles,
      MultipleFavouriteInfo multipleFavouriteInfo,
      Set<Long> followingIds,
      int count) {
    final var multipleArticlesResponseData = new MultipleArticlesResponseData();
    final var articleDataList =
        articles.stream()
            .map(
                article ->
                    toArticleData(
                        article,
                        new FavouriteInfo(
                            multipleFavouriteInfo
                                .getFavouritedArticleIds()
                                .contains(article.getId()),
                            multipleFavouriteInfo
                                .getFavouritedCountByArticleId()
                                .getOrDefault(article.getId(), 0L)
                                .intValue()),
                        followingIds.contains(article.getAuthor().getId())))
            .collect(Collectors.toList());
    multipleArticlesResponseData.setArticles(articleDataList);
    multipleArticlesResponseData.setArticlesCount(count);
    return multipleArticlesResponseData;
  }

  /** Constructs TagsResponseData response. */
  public static TagsResponseData toTagsResponseData(List<String> tags) {
    final var tagsResponseData = new TagsResponseData();
    tagsResponseData.setTags(tags);
    return tagsResponseData;
  }

  private static ArticleData toArticleData(
      Article article, FavouriteInfo favouriteInfo, boolean isFollowingAuthor) {
    final var articleData = new ArticleData();
    articleData.setSlug(article.getSlug());
    articleData.setTitle(article.getTitle());
    articleData.setDescription(article.getDescription());
    articleData.setBody(article.getBody());
    articleData.setTagList(ImmutableList.copyOf(article.getTags()));
    articleData.setCreatedAt(article.getCreatedAt().atOffset(ZoneOffset.UTC));
    articleData.setUpdatedAt(article.getUpdatedAt().atOffset(ZoneOffset.UTC));
    articleData.setFavorited(favouriteInfo.isFavorited());
    articleData.setFavoritesCount(favouriteInfo.getFavoritesCount());
    articleData.setAuthor(toProfile(article.getAuthor(), isFollowingAuthor));
    return articleData;
  }
}
