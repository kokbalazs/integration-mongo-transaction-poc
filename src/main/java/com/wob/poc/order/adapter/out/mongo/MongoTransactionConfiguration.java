package com.wob.poc.order.adapter.out.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class MongoTransactionConfiguration {

    @Bean
    public MongoTransactionManager transactionManager(final MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

}
