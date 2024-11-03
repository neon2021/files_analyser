package com.neon.file.analyser.service;

import com.neon.file.analyser.SpringTestBase;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/11/3
 */
public class AnalyseStorageServiceTest extends SpringTestBase {
    @Resource
    AnalyseStorageService analyseStorageService;

    @Test
    public void test() {
        String userHome = System.getProperty("user.home");
        System.out.println("userHome: " + userHome);
        Path wait2ScanFolder = Paths.get(userHome, "Documents");
        System.out.println("wait2ScanFolder: " + wait2ScanFolder);
        StopWatch stopWatch = StopWatch.createStarted();
        Iterator<File> fileIterator = analyseStorageService.queryFilesFromLocalDir(wait2ScanFolder.toString());
        stopWatch.stop();
        long usedSeconds = TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime());
        softly.assertThat(usedSeconds).isLessThan(1);
        int fileCount = 0;

        while (fileIterator.hasNext()) {
//            File file = fileIterator.next();
//            System.out.println(file.toString());
            fileCount++;
            if (fileCount > 1000) {
                break;
            }
        }
        softly.assertThat(fileCount).isGreaterThan(1000);
    }
}