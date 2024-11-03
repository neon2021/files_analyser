package com.neon.file.analyser.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.neon.file.analyser.mongodb.entity.FileInfo;

public interface FileInfoRepository extends MongoRepository<FileInfo, String> {
    FileInfo findByHash(String fileHash); // TODO: need to know why this method could run??
}
