package com.neon.file.analyser;

import com.neon.file.analyser.mongodb.MongoDbTestConfig;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/11/3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MongoDbTestConfig.class)
@TestPropertySource(locations = {"classpath*:/application-ut.properties"})
// TODO: it will work fine if the properties file is placed here
//@SpringBootTest(classes = MongoDbTestConfig.class)
// DONE: java.lang.NoSuchMethodError: 'org.junit.jupiter.api.extension.ExecutableInvoker org.junit.jupiter.api.extension.ExtensionContext.getExecutableInvoker()' "Java, Spring Boot & JUnit 5 NoSuchMethod error on getExecutableInvoker - Stack Overflow" https://stackoverflow.com/questions/78381933/java-spring-boot-junit-5-nosuchmethod-error-on-getexecutableinvoker
public class SpringTestBase {

    protected SoftAssertions softly = new SoftAssertions();

    @Before
    public void beforeAll() {

    }

    @After
    public void afterAll() {
        softly.assertAll();
    }
}
