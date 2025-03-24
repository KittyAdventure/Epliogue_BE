package com.team1.epilogue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class TestMongoConfig {
    @Bean
    public MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }
}