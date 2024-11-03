package com.neon.file.analyser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class TestRuntimeExec {
    @org.junit.jupiter.api.Test
    public void test() {
        try {
            Process exec = Runtime.getRuntime().exec("ls -l");
            ;
            String outputFromTerminal = IOUtils.toString(exec.getInputStream(), StandardCharsets.UTF_8);
            System.out.println("outputFromTerminal: \n" + outputFromTerminal);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
