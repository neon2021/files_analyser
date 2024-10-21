package com.example.demo.mongodb;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.example.demo.mongodb.entity.FileInfo;
import com.example.demo.mongodb.repository.FileInfoRepository;
import com.google.common.collect.Lists;

@PropertySource("classpath:application.properties")
@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = FileInfoRepository.class) // sourced from: "java - could not found bean for MongoRepository (Spring Boot) - Stack Overflow" https://stackoverflow.com/questions/45006266/could-not-found-bean-for-mongorepository-spring-boot
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

    // save a couple of customers
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

    System.out.println("Customers found with findByHash('123456789012345678901234567890'):");
    System.out.println("--------------------------------");
    FileInfo fileInfo = repository.findByHash("123456789012345678901234567890");
    System.out.println(fileInfo);
  }

}