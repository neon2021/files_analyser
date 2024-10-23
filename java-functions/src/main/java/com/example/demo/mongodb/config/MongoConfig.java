package com.example.demo.mongodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
@Configuration
@EnableMongoRepositories("com.example.demo.mongodb.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "springboot-mongo";
    }
}