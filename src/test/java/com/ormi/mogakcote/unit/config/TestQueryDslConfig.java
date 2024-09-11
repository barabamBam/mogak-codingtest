package com.ormi.mogakcote.unit.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

@TestConfiguration
@Transactional
public class TestQueryDslConfig {

    private final TestEntityManager testEntityManager;

    public TestQueryDslConfig(TestEntityManager testEntityManager) {
        this.testEntityManager = testEntityManager;
    }

    @Bean
    @Primary
    public EntityManager entityManager() {
        return testEntityManager.getEntityManager();
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager());
    }
}
