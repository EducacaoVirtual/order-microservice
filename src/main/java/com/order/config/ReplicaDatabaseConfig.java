package com.order.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.order.repository.replica",
        entityManagerFactoryRef = "replicaEntityManagerFactory",
        transactionManagerRef = "replicaTransactionManager"
)
public class ReplicaDatabaseConfig {
    @Bean(name = "replicaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean replicaEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("replicaDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.order.domain.entity")
                .persistenceUnit("replica")
                .properties(Collections.singletonMap("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"))
                .build();
    }

    @Bean(name = "replicaTransactionManager")
    public PlatformTransactionManager replicaTransactionManager(
            @Qualifier("replicaEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
