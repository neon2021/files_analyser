package com.example.demo.mongodb;

import com.example.demo.lucenesearch.Utils;
import com.example.demo.mongodb.entity.FileInfo;
import com.example.demo.mongodb.repository.FileInfoRepository;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MongoDbTestConfig.class)
@TestPropertySource(locations = {"classpath*:/application-ut.properties"})
// TODO: it will work fine if the properties file is placed here
//@SpringBootTest(classes = MongoDbTestConfig.class)
// DONE: java.lang.NoSuchMethodError: 'org.junit.jupiter.api.extension.ExecutableInvoker org.junit.jupiter.api.extension.ExtensionContext.getExecutableInvoker()' "Java, Spring Boot & JUnit 5 NoSuchMethod error on getExecutableInvoker - Stack Overflow" https://stackoverflow.com/questions/78381933/java-spring-boot-junit-5-nosuchmethod-error-on-getexecutableinvoker
public class EliminateDuplicateTest {
    static String testFileRelativePath = "/com/example/demo/lucenesearch/apple-products-cn.pdf";

    @Resource
    FileInfoRepository repository;
    SoftAssertions softly = new SoftAssertions();

    @Before
    public void beforeAll() {

    }

    @After
    public void afterAll() {
        softly.assertAll();
    }

    @org.junit.Test
    public void test_insertFileInfoAndFindByHash() {
        repository.deleteAll();

        FileInfo fileInfoEntity = Utils.buildFileInfoEntity(Utils.getFileFrom(testFileRelativePath), 0);
        FileInfo savedFileInfoEntity = repository.save(fileInfoEntity);
        System.out.println("after saving, uuid: " + savedFileInfoEntity.getUuid() + ",fileName: " + savedFileInfoEntity.getFileName() + ", path: " + savedFileInfoEntity.getPath());

        FileInfo foundFileInfoEntity = repository.findByHash(savedFileInfoEntity.getHash());
        System.out.println("after calling findByHash, uuid: " + foundFileInfoEntity.getUuid() + ",fileName: " + foundFileInfoEntity.getFileName() + ", path: " + foundFileInfoEntity.getPath());

        System.out.println("\n\nafter saving, savedFileInfoEntity: " + savedFileInfoEntity + ", foundFileInfoEntity: " + foundFileInfoEntity);
    }

    @org.junit.Test
    public void test_extractMetaDataFromFile() {
        File testFile = Utils.getFileFrom(testFileRelativePath);
        Utils.FileInfoExtractor.FileMetadata fileMetadata = Utils.FileInfoExtractor.extractMetaInfoFrom(testFile);
        System.out.println("metadata: " + fileMetadata);
        String fileMD5 = Utils.getFileMD5(testFile);
        System.out.println("fileMD5: " + fileMD5);
    }

    @org.junit.Test
    public void test_detectFileChangeOperation() {
        try {
            File testedFile = Paths.get(Utils.getRealPath("/"), "testedFileForChangeOperation.txt").toFile();
            System.out.println("testedFile: " + testedFile.getAbsolutePath());
            FileUtils.touch(testedFile);

            FileUtils.write(testedFile, "test", StandardCharsets.UTF_8);

            Utils.FileInfoExtractor.FileMetadata fileMetadata = Utils.FileInfoExtractor.extractMetaInfoFrom(testedFile);
            System.out.println("metadata: " + fileMetadata);
            String fileMD5 = Utils.getFileMD5(testedFile);
            System.out.println("fileMD5: " + fileMD5);

            Utils.FileInfoExtractor.FileBasicInfo fileBasicInfo1 = Utils.FileInfoExtractor.extractBasicInfoFrom(testedFile);
            System.out.println("fileBasicInfo1: " + fileBasicInfo1);

            String readFileToString = FileUtils.readFileToString(testedFile, StandardCharsets.UTF_8);
            System.out.println("read char length: " + readFileToString.length());

            Utils.FileInfoExtractor.FileBasicInfo fileBasicInfo2 = Utils.FileInfoExtractor.extractBasicInfoFrom(testedFile);
            System.out.println("fileBasicInfo2: " + fileBasicInfo2);

            softly.assertThat(fileBasicInfo1).as("ASSERT: reading operation will not change basic info of a file").isEqualTo(fileBasicInfo2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
