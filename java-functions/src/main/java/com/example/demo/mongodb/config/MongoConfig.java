package com.example.demo.mongodb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.beans.ConstructorProperties;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
@Configuration
@EnableMongoRepositories("com.example.demo.mongodb.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database}")
    String mongodbName;

//    @Autowired
//    Environment env;

    @Override
    protected String getDatabaseName() {
        return mongodbName;
//        return env.getProperty("spring.data.mongodb.database");
    }
}