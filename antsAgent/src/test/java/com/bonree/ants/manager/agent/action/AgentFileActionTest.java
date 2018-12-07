package com.bonree.ants.manager.agent.action;

import java.io.File;
import java.io.IOException;

import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

public class AgentFileActionTest {

    public AgentFileActionTest() {
    }
    public static void main(String[] args) throws IOException {
        File file = new File("d:/data/aaa.txt");
        if(!file.exists()) {
            Files.createParentDirs(file);
        }
        Files.write("aaaaa".getBytes(), file);
        Files.asByteSink(file, FileWriteMode.APPEND).write("bbbbb".getBytes());
        Files.asByteSink(file, FileWriteMode.APPEND).write("ccccc".getBytes());
        
    }

}
