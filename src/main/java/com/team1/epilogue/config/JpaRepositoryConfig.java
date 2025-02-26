package com.team1.epilogue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.team1.epilogue.repositories.jpa" // JPA용 Repository만 포함
)
public class JpaRepositoryConfig {

}
