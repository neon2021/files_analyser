package com.example.demo.mongodb.entity;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileInfo {
    @Id
    public String uuid;
    public String fileName;
    public String path;
    public Date scannedTime;
    public Date osCreateTime;
    public Date osModifyTime;
    public Date osAccesTime;
    public String scanProcessUUID;
    public String hash;
    public String hashAlgorithm;
    public Long fileSize;
    public String mimeType;
    public Map<String, String> metaInfo;
}
