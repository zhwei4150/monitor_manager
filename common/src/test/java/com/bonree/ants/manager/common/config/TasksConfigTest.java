package com.bonree.ants.manager.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.bonree.ants.manager.config.ServerConfig;
import com.bonree.ants.manager.job.JobProperty;

public class TasksConfigTest {

    @Test
    public void testServerConfig() throws FileNotFoundException {
        File file = new File("D:\\eclipse-workspace\\j2ee\\bonree_ants_manager\\conf\\server.yml");
        InputStream input = new FileInputStream(file);
        Constructor constructor = new Constructor(ServerConfig.class);
        TypeDescription serverDesc = new TypeDescription(ServerConfig.class);
        serverDesc.putListPropertyType("tasks", JobProperty.class);
        constructor.addTypeDescription(serverDesc);
        Yaml yaml = new Yaml(constructor);
        ServerConfig serverConfig = (ServerConfig) yaml.load(input);
        System.out.println(serverConfig);
    }
}
