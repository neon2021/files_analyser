package com.neon.file.analyser.main;

import com.neon.file.analyser.lucenesearch.Utils;
import com.neon.file.analyser.mongodb.entity.FileInfo;
import com.neon.file.analyser.mongodb.repository.FileInfoRepository;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

            stopWatch.stop();
            long elapsedMilliseconds = stopWatch.getTime(TimeUnit.MILLISECONDS);
            repository.save(Utils.buildFileInfoEntity(file, elapsedMilliseconds));
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