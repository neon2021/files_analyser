package com.neon.file.analyser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.neon.file.analyser.lucenesearch.Utils;
import com.neon.file.analyser.mongodb.entity.FileInfo;
import com.neon.file.analyser.mongodb.repository.FileInfoRepository;
import jakarta.annotation.Resource;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * TODO: function description
 *
 * @author neon2021 on 2024/10/23
 */
public class EliminateDuplicateTest extends SpringTestBase {
    static String testFileRelativePath = "/com/example/demo/lucenesearch/apple-products-cn.pdf";

    @Resource
    FileInfoRepository repository;

    @org.junit.Test
    public void test_insertFileInfoAndFindByHash() {
        repository.deleteAll();

        FileInfo fileInfoEntity = Utils.buildFileInfoEntityWithMetrics(Utils.getFileFrom(testFileRelativePath));
        FileInfo savedFileInfoEntity = repository.save(fileInfoEntity);
        System.out.println("after saving, uuid: " + savedFileInfoEntity.getUuid() + ",fileName: " + savedFileInfoEntity.getFileName() + ", path: " + savedFileInfoEntity.getPath());

        FileInfo foundFileInfoEntity = repository.findByHash(savedFileInfoEntity.getHash());
        System.out.println("after calling findByHash, uuid: " + foundFileInfoEntity.getUuid() + ",fileName: " + foundFileInfoEntity.getFileName() + ", path: " + foundFileInfoEntity.getPath());

        System.out.println("\n\nafter saving, savedFileInfoEntity: " + savedFileInfoEntity + ", foundFileInfoEntity: " + foundFileInfoEntity);
    }

    @org.junit.Test
    public void test_extractMetaDataFromFile() {
        File testFile = Utils.getFileFrom(testFileRelativePath);
        Utils.FileInfoExtractor.FileMetadata fileMetadata = Utils.FileInfoExtractor.extractMetaInfoFrom(testFile);
        System.out.println("metadata: " + fileMetadata);
        String fileMD5 = Utils.getFileMD5(testFile);
        System.out.println("fileMD5: " + fileMD5);
    }

    @org.junit.Test
    public void test_detectFileChangeOperation() {
        try {
            File testedFile = Paths.get(Utils.getRealPath("/"), "testedFileForChangeOperation.txt").toFile();
            System.out.println("testedFile: " + testedFile.getAbsolutePath());
            FileUtils.touch(testedFile);

            FileUtils.write(testedFile, "test", StandardCharsets.UTF_8);

            Utils.FileInfoExtractor.FileMetadata fileMetadata = Utils.FileInfoExtractor.extractMetaInfoFrom(testedFile);
            System.out.println("metadata: " + fileMetadata);
            String fileMD5 = Utils.getFileMD5(testedFile);
            System.out.println("fileMD5: " + fileMD5);

            Utils.FileInfoExtractor.FileBasicInfo fileBasicInfo1 = Utils.FileInfoExtractor.extractBasicInfoFrom(testedFile);
            System.out.println("fileBasicInfo1: " + fileBasicInfo1);

            String readFileToString = FileUtils.readFileToString(testedFile, StandardCharsets.UTF_8);
            System.out.println("read char length: " + readFileToString.length());

            Utils.FileInfoExtractor.FileBasicInfo fileBasicInfo2 = Utils.FileInfoExtractor.extractBasicInfoFrom(testedFile);
            System.out.println("fileBasicInfo2: " + fileBasicInfo2);

            softly.assertThat(fileBasicInfo1).as("ASSERT: reading operation will not change basic info of a file").isEqualTo(fileBasicInfo2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void test_detectDuplicatedFolder() throws IOException {
        FileUtils.deleteDirectory(getFileInTestFolder("tmp"));
        FileUtils.deleteDirectory(getFileInTestFolder("tmpA"));
        FileUtils.deleteDirectory(getFileInTestFolder("tmpB"));

        getFileInTestFolder("tmp").mkdir();
        getFileInTestFolder("tmpA").mkdir();
        getFileInTestFolder("tmpB").mkdir();

        System.out.printf("before writing files in them,\ntmpA: %s,\ntmpB: %s\n\n",
                Utils.FileInfoExtractor.extractBasicInfoFrom(getFilePathInTestFolder("tmpA")),
                Utils.FileInfoExtractor.extractBasicInfoFrom(getFilePathInTestFolder("tmpB"))
        );

        FileUtils.write(getFileInTestFolder("tmpA/testA1"), "test", StandardCharsets.UTF_8);
        FileUtils.write(getFileInTestFolder("tmpA/testA2"), "test", StandardCharsets.UTF_8);
        sleep(2);
        FileUtils.write(getFileInTestFolder("tmpB/testB1"), "test", StandardCharsets.UTF_8);
        FileUtils.write(getFileInTestFolder("tmpB/testB2"), "test", StandardCharsets.UTF_8);
        sleep(3);

        System.out.printf("after writing files in them,\ntmpA: %s,\ntmpB: %s\n\n",
                Utils.FileInfoExtractor.extractBasicInfoFrom(getFilePathInTestFolder("tmpA")),
                Utils.FileInfoExtractor.extractBasicInfoFrom(getFilePathInTestFolder("tmpB"))
        );

        Utils.FileInfoExtractor.FileBasicInfo testA1BasicInfo = Utils.FileInfoExtractor.extractBasicInfoFrom(getFilePathInTestFolder("tmpA/testA1"));
        Utils.FileInfoExtractor.FileBasicInfo testB1BasicInfo = Utils.FileInfoExtractor.extractBasicInfoFrom(getFilePathInTestFolder("tmpB/testB1"));
        softly.assertThat(testA1BasicInfo).usingRecursiveComparison().ignoringCollectionOrderInFields("size").isNotEqualTo(testB1BasicInfo);
        softly.assertThat(testA1BasicInfo).usingRecursiveComparison().comparingOnlyFields("size").isEqualTo(testB1BasicInfo);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> stringObjectMapForTestA1 = objectMapper.convertValue(testA1BasicInfo, new TypeReference<>() {
        });
        Map<String, Object> stringObjectMapForTestB1 = objectMapper.convertValue(testB1BasicInfo, new TypeReference<>() {
        });
        MapDifference<String, Object> difference = Maps.difference(stringObjectMapForTestA1, stringObjectMapForTestB1);
        softly.assertThat(difference.entriesInCommon()).containsKeys("size");
        System.out.printf("testA1BasicInfo=%s,\ntestB1BasicInfo=%s\ndifference: %s\n\n", testA1BasicInfo.toString(), testB1BasicInfo.toString(), difference);


        softly.assertThat(testA1BasicInfo.toString()).isNotEqualTo(testB1BasicInfo.toString());

        softly.assertThat(Utils.getFileMD5(getFilePathInTestFolder("tmpA/testA1")))
                .isEqualTo(Utils.getFileMD5(getFilePathInTestFolder("tmpA/testA2")))
                .isEqualTo(Utils.getFileMD5(getFilePathInTestFolder("tmpB/testB1")))
                .isEqualTo(Utils.getFileMD5(getFilePathInTestFolder("tmpB/testB2")));

        sleep(3);

        Path zipA_filePath = compressFile(getFileInTestFolder("tmp/testA.zip").toPath(), getFileInTestFolder("tmpA/testA1").toPath(), getFileInTestFolder("tmpA/testA2").toPath());
        Path zipB_filePath = compressFile(getFileInTestFolder("tmp/testB.zip").toPath(), getFileInTestFolder("tmpB/testB1").toPath(), getFileInTestFolder("tmpB/testB2").toPath());

        String fileMD5ForZipA = Utils.getFileMD5(zipA_filePath.toFile());
        String fileMD5ForZipB = Utils.getFileMD5(zipB_filePath.toFile());
        System.out.printf("fileMD5ForZipA:%s\nfileMD5ForZipB:%s\n\n", fileMD5ForZipA, fileMD5ForZipB);

        System.out.printf("after compressing files in them,\ntmpA: %s,\ntmpB: %s\n\n",
                Utils.FileInfoExtractor.extractBasicInfoFrom(getFilePathInTestFolder("tmpA")),
                Utils.FileInfoExtractor.extractBasicInfoFrom(getFilePathInTestFolder("tmpB"))
        );
    }

    private Path compressFile(Path destination, Path... files) {
        String format = FileNameUtils.getExtension(destination);

        if (StringUtils.equalsIgnoreCase("zip", format)) {
            try {
                final FileOutputStream zipFileFullPath = new FileOutputStream(destination.toString());
                ZipOutputStream zipOut = new ZipOutputStream(zipFileFullPath);

                for (Path srcFile : files) {
                    File fileToZip = new File(srcFile.toString());
                    FileInputStream fis = new FileInputStream(fileToZip);
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fis.close();
                }

                zipOut.close();
                zipFileFullPath.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return destination;
        }

        try (OutputStream out = Files.newOutputStream(destination);
             BufferedOutputStream buffer = new BufferedOutputStream(out);
             CompressorOutputStream compressor = new CompressorStreamFactory().createCompressorOutputStream(format, buffer)) {
            for (Path file : files) {
                IOUtils.copy(Files.newInputStream(file), compressor);
            }
        } catch (CompressorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destination;
    }

    private File getFileInTestFolder(String fileName) {
        return Paths.get(Utils.getRealPath("/"), fileName).toFile();
    }

    private String getFilePathInTestFolder(String fileName) {
        return Paths.get(Utils.getRealPath("/"), fileName).toString();
    }

    private void sleep(int i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
