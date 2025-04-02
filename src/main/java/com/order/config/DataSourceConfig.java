package com.order.config;

import com.order.domain.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {
    @Bean(name = "mainDataSource")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://172.21.0.8:6432/orderapi")
                .username("postgres")
                .password("educacaovirtual2025")
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean(name = "replicaDataSource")
    public DataSource replicaDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://172.21.0.8:6432/orderapi")
                .username("postgres")
                .password("educacaovirtual2025")
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean(name = "routingDataSource")
    public DataSource routingDataSource(
            @Qualifier("mainDataSource") DataSource mainDataSource,
            @Qualifier("replicaDataSource") DataSource replicaDataSource
    ) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MAIN, mainDataSource);
        targetDataSources.put(DataSourceType.REPLICA, replicaDataSource);

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(mainDataSource);

        return routingDataSource;
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
