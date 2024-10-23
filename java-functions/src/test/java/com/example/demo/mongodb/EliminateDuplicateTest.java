package com.example.demo.mongodb;

import com.example.demo.mongodb.entity.FileInfo;
import com.example.demo.mongodb.repository.FileInfoRepository;
import jakarta.annotation.Resource;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MongoDbTestConfig.class)
//@SpringBootTest(classes = MongoDbTestConfig.class)
// DONE: java.lang.NoSuchMethodError: 'org.junit.jupiter.api.extension.ExecutableInvoker org.junit.jupiter.api.extension.ExtensionContext.getExecutableInvoker()' "Java, Spring Boot & JUnit 5 NoSuchMethod error on getExecutableInvoker - Stack Overflow" https://stackoverflow.com/questions/78381933/java-spring-boot-junit-5-nosuchmethod-error-on-getexecutableinvoker
public class EliminateDuplicateTest {
    @Resource
    FileInfoRepository repository;

    @org.junit.Test
    public void testFindByHash() {
        FileInfo fileInfo = repository.findByHash("536ffc4daaf5dbc56902bb3c469759ed");
        System.out.println("fileName: " + fileInfo.getFileName() + ", path: " + fileInfo.getPath());
    }
}
