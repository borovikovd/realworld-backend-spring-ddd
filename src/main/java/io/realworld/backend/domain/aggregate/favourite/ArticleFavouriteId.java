package io.realworld.backend.domain.aggregate.favourite;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import org.checkerframework.checker.nullness.qual.Nullable;

@Embeddable
public class ArticleFavouriteId implements Serializable {
  private long userId;
  private long articleId;

  protected ArticleFavouriteId() {}

  public ArticleFavouriteId(long userId, long articleId) {
    this.userId = userId;
    this.articleId = articleId;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public long getArticleId() {
    return articleId;
  }

  public void setArticleId(long articleId) {
    this.articleId = articleId;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArticleFavouriteId that = (ArticleFavouriteId) o;
    return userId == that.userId && articleId == that.articleId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, articleId);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("userId", userId)
        .add("articleId", articleId)
        .toString();
  }
}
