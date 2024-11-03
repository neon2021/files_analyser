package com.neon.file.analyser.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/11/3
 */
@Component
public class AnalyseStorageService {
    public Iterator<File> queryFilesFromLocalDir(String localDir) {
        return FileUtils.iterateFiles(new File(localDir), null, true);
    }

}
