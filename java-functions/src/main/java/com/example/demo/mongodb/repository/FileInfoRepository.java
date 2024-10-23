package com.example.demo.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.mongodb.entity.FileInfo;

public interface FileInfoRepository extends MongoRepository<FileInfo, String> {
    FileInfo findByHash(String fileHash); // TODO: need to know why this method could run??
}
