package com.example.demo.mongodb.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
public class MongoPostProcessor  implements BeanPostProcessor {
    /**
     * sourced from : How to customize MappingMongoConverter
     * (setMapKeyDotReplacement) in Spring-Boot without breaking the
     * auto-configuration? - Stack Overflow
     * https://stackoverflow.com/questions/35598595/how-to-customize-mappingmongoconverter-setmapkeydotreplacement-in-spring-boot
     * <p>
     * to solve error:
     *
     * <pre>
     * org.springframework.data.mapping.MappingException: Map key pdf:docinfo:custom:PTEX.Fullbanner contains dots but no replacement was configured; Make sure map keys don't contain dots in the first place or configure an appropriate replacement
     * </pre>
     */
    // Converts . into a mongo friendly char
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("bean: " + bean + ", beanName: " + beanName);
        if (bean instanceof MappingMongoConverter) {
            MappingMongoConverter mongoConverter = (MappingMongoConverter) bean;
            mongoConverter.setMapKeyDotReplacement("_");
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

    }
}
