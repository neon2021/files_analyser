package com.neon.file.analyser.mongodb.entity;

import java.util.Date;
import java.util.Map;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FileInfo {
    @Id
    public String uuid;
    public String fileName;
    public String path;
    public Date scannedTime;
    public Date osCreateTime;
    public Date osModifyTime;
    public Date osAccessTime;
    public String scanProcessUUID;
    public String hash;
    public String hashAlgorithm;
    public Long fileSize;
    public String mimeType;
    public Long scanElapseDuration;
    public Map<String, String> metaInfo;
}
