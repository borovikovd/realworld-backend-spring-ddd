package io.realworld.backend.domain.aggregate.comment;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
  List<Comment> findByArticleId(long articleId);

  void deleteByArticleId(long articleId);
}
