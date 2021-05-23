package io.realworld.backend.domain.aggregate.article;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import io.realworld.backend.domain.aggregate.user.User;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

@Entity
@DefaultQualifier(value = Nullable.class, locations = TypeUseLocation.FIELD)
public class Article {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id = 0;

  private @NotNull String slug = "";
  private @NotNull String title = "";
  private @NotNull String description = "";
  private @NotNull String body = "";

  @ElementCollection(fetch = FetchType.EAGER)
  private @NotNull Set<String> tags = ImmutableSet.of();

  @ManyToOne private @NotNull User author = new User("", "", "");
  private @NotNull Instant createdAt = Instant.now();
  private @NotNull Instant updatedAt = Instant.now();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getSlug() {
    return slug;
  }

  public String getTitle() {
    return title;
  }

  /** Sets title and generate a slug. */
  public void setTitle(String title) {
    this.slug =
        title.toLowerCase().replaceAll("[\\&|[\\uFE30-\\uFFA0]|\\’|\\”|\\s\\?\\,\\.]+", "-")
            + "-"
            + ThreadLocalRandom.current().nextInt();
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = ImmutableSet.copyOf(tags);
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  @PreUpdate
  public void onUpdate() {
    updatedAt = Instant.now();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("slug", slug)
        .add("title", title)
        .add("description", description)
        .add("tags", tags)
        .add("author", author)
        .add("createdAt", createdAt)
        .add("updatedAt", updatedAt)
        .toString();
  }
}
