package io.realworld.backend.domain.aggregate.follow;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import org.checkerframework.checker.nullness.qual.Nullable;

@Embeddable
public class FollowRelationId implements Serializable {
  private long followerId = 0;
  private long followeeId = 0;

  protected FollowRelationId() {}

  public FollowRelationId(long followerId, long followeeId) {
    this.followerId = followerId;
    this.followeeId = followeeId;
  }

  public long getFollowerId() {
    return followerId;
  }

  public void setFollowerId(long followerId) {
    this.followerId = followerId;
  }

  public long getFolloweeId() {
    return followeeId;
  }

  public void setFolloweeId(long followeeId) {
    this.followeeId = followeeId;
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
    FollowRelationId that = (FollowRelationId) o;
    return followerId == that.followerId && followeeId == that.followeeId;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(followerId, followeeId);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("followerId", followerId)
        .add("followeeId", followeeId)
        .toString();
  }
}
