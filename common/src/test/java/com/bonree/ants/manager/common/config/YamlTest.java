package com.bonree.ants.manager.common.config;

import java.util.List;

import org.yaml.snakeyaml.Yaml;

public class YamlTest {

    public YamlTest() {
        // TODO Auto-generated constructor stub
    }
    
    public static void main(String[] args) {
        testList();
    }
    
    public static void testList() {
        Yaml yaml = new Yaml();
        String document = "\n- aaa\n- bbb\n- ccc";
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>)yaml.load(document);
        System.out.println(list);
    }
    
    

}
