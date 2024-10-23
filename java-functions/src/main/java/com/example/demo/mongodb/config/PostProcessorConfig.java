package com.example.demo.mongodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
@Configuration
public class PostProcessorConfig {
    /**
     * sourced from : "Spring BeanPostProcessor | Baeldung" https://www.baeldung.com/spring-beanpostprocessor
     *
     * @return
     */
    @Bean
    public MongoPostProcessor mongoPostProcessor() {
        return new MongoPostProcessor();
    }

}
