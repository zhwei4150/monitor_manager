package com.bonree.ants.manager.server.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ServerFileActionTest {

    public static void main(String[] args) throws IOException {
        File file = new File("d:/data/aaa.txt");
        System.out.println(file.getParent());
        System.out.println(file.getName());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 2;
            byte[] conetent = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(conetent, 0, buf_size))) {
                System.out.println(len);
                System.out.println(new String(conetent,0,len));
            }
        } finally {
            in.close();
        }

    }

}
