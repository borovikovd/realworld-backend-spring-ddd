package io.realworld.backend.domain.aggregate.favourite;

import com.google.common.base.MoreObjects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

@Entity
@DefaultQualifier(value = Nullable.class, locations = TypeUseLocation.FIELD)
public class ArticleFavourite {
  @EmbeddedId @NonNull private ArticleFavouriteId id = new ArticleFavouriteId(0, 0);

  private ArticleFavourite() {}

  public ArticleFavourite(long userId, long articleId) {
    this.id = new ArticleFavouriteId(userId, articleId);
  }

  public ArticleFavouriteId getId() {
    return id;
  }

  public void setId(ArticleFavouriteId id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).toString();
  }
}
