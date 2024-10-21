package com.example.demo.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.mongodb.entity.FileInfo;

public interface FileInfoRepository extends MongoRepository<FileInfo, String> {
    public FileInfo findByHash(String fileHash);
}
