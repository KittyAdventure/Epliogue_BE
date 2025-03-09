package com.team1.epilogue.common.document;


import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@MappedSuperclass
@EnableMongoAuditing
public abstract class BaseTimeDocument { // MongoDB 전용
  @CreatedDate
  @Field(name = "created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Field(name = "modified_at")
  private LocalDateTime modifiedAt;
}
