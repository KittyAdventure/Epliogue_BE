package com.team1.epilogue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing // MongoDB의 자동 시간 관리를 활성화
public class MongoConfig {

}
