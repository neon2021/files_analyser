package com.neon.file.analyser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Security;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.rfksystems.blake2b.Blake2b;
import com.rfksystems.blake2b.security.Blake2bProvider;

public class HashTest {
    @Test
    public void test_blake2b() throws IOException, NoSuchAlgorithmException {
        Security.addProvider(new Blake2bProvider());
        final MessageDigest digest = MessageDigest.getInstance(Blake2b.BLAKE2_B_512);
        String filePath = "~/Documents/TalkPython_2021_11_17_345__10 Tips and Tools for Developer Productivity Transcript.docx";
        try (FileInputStream fileIn = FileUtils.openInputStream(new File(filePath))) {
            byte[] buffer = new byte[1024 * 1024 * 1024];
            int read;
            while ((read = fileIn.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }

            String encodeHexString = Hex.encodeHexString(digest.digest());

            System.out.println("encodeHexString: " + encodeHexString);
        }

    }

    @Test
    public void test_blake2b_with_empty_string() throws IOException, NoSuchAlgorithmException {
        Security.addProvider(new Blake2bProvider());
        final MessageDigest digest = MessageDigest.getInstance(Blake2b.BLAKE2_B_512);

        byte[] buffer = new byte[0];
        digest.update(buffer, 0, 0);

        String encodeHexString = Hex.encodeHexString(digest.digest());

        System.out.println("encodeHexString: " + encodeHexString);

    }
}
