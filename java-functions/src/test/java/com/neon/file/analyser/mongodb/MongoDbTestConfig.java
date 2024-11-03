package com.neon.file.analyser.mongodb;

import com.neon.file.analyser.main.AccessingDataMongodbApplication;
import com.neon.file.analyser.mongodb.config.MongoConfig;
import com.neon.file.analyser.mongodb.config.PostProcessorConfig;
import com.neon.file.analyser.mongodb.repository.FileInfoRepository;
import com.neon.file.analyser.service.AnalyseStorageService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
@ContextConfiguration
@ComponentScans(@ComponentScan(basePackageClasses = {FileInfoRepository.class, MongoConfig.class, PostProcessorConfig.class, AnalyseStorageService.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AccessingDataMongodbApplication.class)} // "Spring @ComponentScan - Filter Types | Baeldung" https://www.baeldung.com/spring-componentscan-filter-type
)
)
@EnableAutoConfiguration
//@TestPropertySource(locations = {"classpath*:/application-ut.properties"}) // TODO: it will not work if the properties file is placed here
public class MongoDbTestConfig {

}
