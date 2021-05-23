package io.realworld.backend.domain.aggregate.follow;

import com.google.common.base.MoreObjects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

@Entity
@DefaultQualifier(value = Nullable.class, locations = TypeUseLocation.FIELD)
public class FollowRelation {
  @EmbeddedId @NonNull private FollowRelationId id = new FollowRelationId(0, 0);

  protected FollowRelation() {}

  public FollowRelation(long followerId, long followeeId) {
    this.id = new FollowRelationId(followerId, followeeId);
  }

  public FollowRelationId getId() {
    return id;
  }

  public void setId(FollowRelationId id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).toString();
  }
}
