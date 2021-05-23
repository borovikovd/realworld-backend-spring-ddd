package io.realworld.backend.domain.aggregate.article;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetBasedPageRequest implements Pageable {
  private final int offset;
  private final int limit;
  private final Sort sort;

  /** Creates OffsetBasedPageRequest instance. */
  public OffsetBasedPageRequest(int offset, int limit, Sort sort) {
    this.offset = offset;
    this.limit = limit;
    this.sort = sort;
  }

  @Override
  public int getPageNumber() {
    return offset / limit;
  }

  @Override
  public int getPageSize() {
    return limit;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @Override
  public Sort getSort() {
    return sort;
  }

  @Override
  public Pageable next() {
    return new OffsetBasedPageRequest((int) getOffset() + getPageSize(), getPageSize(), getSort());
  }

  private Pageable previous() {
    // The integers are positive. Subtracting does not let them become bigger than integer.
    return hasPrevious()
        ? new OffsetBasedPageRequest((int) getOffset() - getPageSize(), getPageSize(), getSort())
        : this;
  }

  @Override
  public Pageable previousOrFirst() {
    return hasPrevious() ? previous() : first();
  }

  @Override
  public Pageable first() {
    return new OffsetBasedPageRequest(0, getPageSize(), getSort());
  }

  @Override
  public boolean hasPrevious() {
    return offset > limit;
  }

  public static OffsetBasedPageRequest of(int offset, int limit, Sort sort) {
    return new OffsetBasedPageRequest(offset, limit, sort);
  }
}
