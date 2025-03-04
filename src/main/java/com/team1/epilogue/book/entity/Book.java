package com.team1.epilogue.book.entity;

import com.team1.epilogue.common.entity.BaseEntity;
import com.team1.epilogue.rating.entity.Rating;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

  private Integer price;

  @Lob
  private String description;

  private double avgRating;

  private String coverUrl;

  private String publisher;

  private LocalDate pubDate;
}
