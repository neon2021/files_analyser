package com.example.demo.mongodb;

import com.example.demo.lucenesearch.Utils;
import com.example.demo.lucenesearch.Utils.FileIndexer;
import com.example.demo.mongodb.entity.FileInfo;
import com.example.demo.mongodb.repository.FileInfoRepository;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

        File directory = new File(System.getProperty("user.home") + "/Documents"); // sourced from : java - How to handle ~ in file paths - Stack Overflow  https://stackoverflow.com/questions/7163364/how-to-handle-in-file-paths
        System.out.println("directory: " + directory.getAbsolutePath());
        Deque<File> deque = new LinkedList<>();
        addFileToBeginningAndDirToEnding(FileUtils.listFiles(directory, null, false), deque);

        // save a couple of FileInfo
        while (!deque.isEmpty()) {
            File file = deque.pollFirst();
            if (file.isDirectory()) {
                addFileToBeginningAndDirToEnding(FileUtils.listFiles(directory, null, false), deque);
                continue;
            }
            StopWatch stopWatch = StopWatch.createStarted();
            // sourced from "How to get file last modified date in Java - Mkyong.com" https://mkyong.com/java/how-to-get-the-file-last-modified-date-in-java/
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);

            System.out.println("file: " + file.getName() + ", creationTime: " + attr.creationTime() + ", lastAccessTime: " + attr.lastAccessTime() + ", lastModifiedTime: " + attr.lastModifiedTime());

            Metadata parsedMetaData = FileIndexer.extractMetaInfoFrom(file);

            Map<String, String> metaInfo = Arrays.stream(parsedMetaData.names())
                    .collect(Collectors.toMap(
                            Function.identity(),
                            parsedMetaData::get)
                    );
//            System.out.println("metaInfo: " + metaInfo);
            stopWatch.stop();
            repository.save(FileInfo.builder()
                    .fileName(file.getName())
                    .fileSize(attr.size())
                    .hash(Utils.getFileMD5(file))
                    .hashAlgorithm("MD5")
                    .mimeType(parsedMetaData.get(Metadata.CONTENT_TYPE))
                    .osAccesTime(Date.from(attr.lastAccessTime().toInstant()))
                    .osCreateTime(Date.from(attr.creationTime().toInstant()))
                    .osModifyTime(Date.from(attr.lastModifiedTime().toInstant()))
                    .path(file.getAbsolutePath())
                    .scanProcessUUID("scanProcUUID")
                    .uuid(UUID.randomUUID().toString())
                    .scannedTime(new Date())
                    .scanElapseDuration(stopWatch.getTime(TimeUnit.MILLISECONDS))
                    .metaInfo(metaInfo)
                    .build());
        }

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

    private void addFileToBeginningAndDirToEnding(Collection<File> files, Deque<File> deque) {
        for (File file : files) {
            if (file.isDirectory()) {
                deque.addLast(file);
            }
            if (file.isFile()) {
                deque.addFirst(file);
            }
        }

        System.out.println("after addFileToBeginningAndDirToEnding, deque.size: " + deque.size());
    }

}