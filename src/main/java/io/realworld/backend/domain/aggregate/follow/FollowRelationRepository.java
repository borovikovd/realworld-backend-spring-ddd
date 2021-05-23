package io.realworld.backend.domain.aggregate.follow;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface FollowRelationRepository extends CrudRepository<FollowRelation, FollowRelationId> {
  List<FollowRelation> findByIdFollowerId(long followerId);
}
