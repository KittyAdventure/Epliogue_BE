package com.team1.epilogue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = {"com.team1.epilogue.auth.repository" // JPA용 Repository만 포함
        , "com.team1.epilogue.transaction.repository"
        , "com.team1.epilogue.book.repository"
    }
)
public class JpaRepositoryConfig {

}
