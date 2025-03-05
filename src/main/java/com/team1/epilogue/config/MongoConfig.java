package com.team1.epilogue.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing // MongoDB의 자동 시간 관리를 활성화
@Slf4j
public class MongoConfig {

  @PostConstruct
  public void init() {
    log.info("✅ Mongo Auditing 활성화됨");
  }
}