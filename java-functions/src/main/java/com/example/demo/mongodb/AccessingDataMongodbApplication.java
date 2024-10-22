package com.example.demo.mongodb;

import com.example.demo.lucenesearch.Utils;
import com.example.demo.lucenesearch.Utils.FileIndexer;
import com.example.demo.lucenesearch.Utils.ParsedFileInfo;
import com.example.demo.mongodb.entity.FileInfo;
import com.example.demo.mongodb.repository.FileInfoRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@PropertySource("classpath:application.properties")
@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = FileInfoRepository.class) // sourced from: "java - could not found bean
// for MongoRepository (Spring Boot) - Stack
// Overflow"
// https://stackoverflow.com/questions/45006266/could-not-found-bean-for-mongorepository-spring-boot
public class AccessingDataMongodbApplication implements CommandLineRunner {

    @Autowired
    private FileInfoRepository repository;

    public static void main(String[] args) {
        System.out.println("begin");
        SpringApplication.run(AccessingDataMongodbApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("args: " + args);

        repository.deleteAll();

        // save a couple of FileInfo
        String filePath = Utils.getRealPath("/com/example/demo/lucenesearch/apple-products-cn.pdf");
        File file = new File(filePath);
        ParsedFileInfo parsedFileInfo = FileIndexer.extractTextFromPDF(file);

        Map<String, String> metaInfo = Arrays.stream(parsedFileInfo.getMetadata().names())
                .collect(Collectors.toMap(
                        Function.identity(),
                        name -> parsedFileInfo.getMetadata().get(name))
                );
        System.out.println("metaInfo: " + metaInfo);
        repository.save(FileInfo.builder()
                .fileName("testFileName")
                .fileSize(123L)
                .hash("123456789012345678901234567890")
                .hashAlgorithm("nonExistAlgorithm")
                .mimeType("testMimeType")
                .osAccesTime(new Date())
                .osCreateTime(new Date())
                .osModifyTime(new Date())
                .path("path")
                .scanProcessUUID("scanProcUUID")
                .uuid("selfUUID")
                .scannedTime(new Date())
                .metaInfo(metaInfo)
                .build());
        repository.save(FileInfo.builder().uuid("file2UUID").build());

        // fetch all FileInfos
        System.out.println("FileInfos found with findAll():");
        System.out.println("-------------------------------");
        for (FileInfo fileInfo : repository.findAll()) {
            System.out.println(fileInfo);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("FileInfo found with findAllById('selfUUID'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findAllById(Lists.newArrayList("selfUUID")));

        System.out.println("FileInfo found with findByHash('123456789012345678901234567890'):");
        System.out.println("--------------------------------");
        FileInfo fileInfo = repository.findByHash("123456789012345678901234567890");
        System.out.println(fileInfo);
    }

}