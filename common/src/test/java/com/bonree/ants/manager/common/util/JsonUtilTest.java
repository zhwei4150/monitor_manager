package com.bonree.ants.manager.common.util;

import org.junit.Test;

public class JsonUtilTest {

    @Test
    public void testMap() {
        
    }
    
    public static void main(String[] args) {
        StringBuilder sb =new StringBuilder();
        int count = 0;
        for(int i=1;i<=99;i++) {
            sb.append(String.valueOf(i));
        }
        byte[] bytes = sb.toString().getBytes();
        for(byte b : bytes) {
            if(b =='1') {
                count++;
            }
        }
        System.out.println(count);
    }
}
