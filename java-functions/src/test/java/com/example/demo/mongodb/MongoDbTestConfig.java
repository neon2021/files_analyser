package com.example.demo.mongodb;

import com.example.demo.mongodb.config.MongoConfig;
import com.example.demo.mongodb.config.PostProcessorConfig;
import com.example.demo.mongodb.repository.FileInfoRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
@ContextConfiguration
@ComponentScans(@ComponentScan(basePackageClasses = {FileInfoRepository.class, MongoConfig.class, PostProcessorConfig.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AccessingDataMongodbApplication.class)} // "Spring @ComponentScan - Filter Types | Baeldung" https://www.baeldung.com/spring-componentscan-filter-type
)
)
@EnableAutoConfiguration
//@TestPropertySource(locations = {"classpath*:/application-ut.properties"}) // TODO: it will not work if the properties file is placed here
public class MongoDbTestConfig {

}
