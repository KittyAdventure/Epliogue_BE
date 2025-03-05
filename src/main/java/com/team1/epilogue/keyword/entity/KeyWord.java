package com.team1.epilogue.keyword.entity;

import com.team1.epilogue.common.document.BaseTimeDocument;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "key_word")
@Getter
@Setter
@Builder
public class KeyWord extends BaseTimeDocument {

  @Id
  private String id;

  private String keyword;
}
