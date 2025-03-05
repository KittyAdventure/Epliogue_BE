package com.team1.epilogue.keyword.repository;

import com.team1.epilogue.keyword.entity.KeyWord;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyWordRepository extends MongoRepository<KeyWord,String> {

  // createdAt이 특정 날짜보다 이전인 데이터를 삭제
  void deleteByCreatedAtBefore(LocalDateTime date);
}
