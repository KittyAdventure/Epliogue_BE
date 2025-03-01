package com.team1.epilogue.book.entity;

import com.team1.epilogue.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity {

  @Id
  private String id;

  private String title;

  private String author;

  @Lob
  private String description;

  private double avgRating;

  private String coverUrl;

}
