package com.example.demo.mongodb;

import com.example.demo.lucenesearch.Utils;
import com.example.demo.mongodb.entity.FileInfo;
import com.example.demo.mongodb.repository.FileInfoRepository;
import jakarta.annotation.Resource;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MongoDbTestConfig.class)
@TestPropertySource(locations = {"classpath*:/application-ut.properties"}) // TODO: it will work fine if the properties file is placed here
//@SpringBootTest(classes = MongoDbTestConfig.class)
// DONE: java.lang.NoSuchMethodError: 'org.junit.jupiter.api.extension.ExecutableInvoker org.junit.jupiter.api.extension.ExtensionContext.getExecutableInvoker()' "Java, Spring Boot & JUnit 5 NoSuchMethod error on getExecutableInvoker - Stack Overflow" https://stackoverflow.com/questions/78381933/java-spring-boot-junit-5-nosuchmethod-error-on-getexecutableinvoker
public class EliminateDuplicateTest {
    static String testFileRelativePath = "/com/example/demo/lucenesearch/apple-products-cn.pdf";

    @Resource
    FileInfoRepository repository;

    @org.junit.Test
    public void testFindByHash() {
        repository.deleteAll();

        FileInfo fileInfoEntity = Utils.buildFileInfoEntity(Utils.getFileFrom(testFileRelativePath), 0);
        FileInfo saved = repository.save(fileInfoEntity);
        System.out.println("after saving, uuid: " + saved.getUuid() + ",fileName: " + saved.getFileName() + ", path: " + saved.getPath());

        FileInfo fileInfo = repository.findByHash(saved.getHash());
        System.out.println("after calling findByHash, uuid: " + fileInfo.getUuid() + ",fileName: " + fileInfo.getFileName() + ", path: " + fileInfo.getPath());
    }

    @org.junit.Test
    public void testExtractingMetaDataFromFile() {
        File testFile = new File(Utils.getRealPath(testFileRelativePath));
        Utils.FileInfoExtractor.FileMetadata fileMetadata = Utils.FileInfoExtractor.extractMetaInfoFrom(testFile);
        System.out.println("metadata: " + fileMetadata);
        String fileMD5 = Utils.getFileMD5(testFile);
        System.out.println("fileMD5: " + fileMD5);
    }
}
