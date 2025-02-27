package com.team1.epilogue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(
    basePackages = "com.team1.epilogue.chat.repository" // MongoDB 리포지토리가 있는 패키지 지정
)
public class MongoRepositoryConfig {

}
