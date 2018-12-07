package com.bonree.ants.manager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.bonree.ants.manager.command.CmdProperty;
import com.bonree.ants.manager.config.AgentConfig;
import com.bonree.ants.manager.config.ServerConfig;
import com.bonree.ants.manager.job.JobProperty;

public class ConfigUtil {
    private final static Logger LOG = LoggerFactory.getLogger(ConfigUtil.class);

    private ConfigUtil() {
    }

    public static <T> T loadConfigFromFile(String fileName) throws Exception {
        File file = new File(fileName);
        InputStream input = new FileInputStream(file);
        Constructor constructor = new Constructor(ServerConfig.class);
        Yaml yaml = new Yaml(constructor);
        @SuppressWarnings("unchecked")
        T config = (T) yaml.load(input);
        return config;
    }

    /** 概述：加载yaml配置文件
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @user <a href=mailto:weizheng@bonree.com>魏征</a>
     */
    public static ServerConfig loadServerConifg(String fileName) throws Exception {

        ServerConfig serverConfig = loadConfigFromFile(fileName);

        File[] jobFiles = listFiles(serverConfig.getJobModulePath());
        for (File jobFile : jobFiles) {
            serverConfig.addJob(constructObjectFromYaml(jobFile, JobProperty.class));
        }

        File[] cmdFiles = listFiles(serverConfig.getCmdModulePath());
        for (File cmdFile : cmdFiles) {
            serverConfig.addCmd(constructObjectFromYaml(cmdFile, CmdProperty.class));
        }

        LOG.info("serverConfig:{}", StringUtils.replace(serverConfig.toString(), "\n", "\\n"));
        return serverConfig;
    }

    public static AgentConfig loadAgentConfig(String fileName) throws Exception {
        AgentConfig agentConfig = loadConfigFromFile(fileName);
        return agentConfig;
    }

    @SuppressWarnings("unchecked")
    public static <T> T constructObjectFromYaml(File file, Class<?> cls) throws Exception {
        Constructor constructor = new Constructor(cls);
        Yaml yaml = new Yaml(constructor);
        InputStream input = new FileInputStream(file);
        T property = (T) yaml.load(input);
        return property;
    }

    @SuppressWarnings("unchecked")
    public static <T> T constructObjectFromYaml(String str, Class<?> cls) {
        Yaml yaml = new Yaml();
        T collectionProperty = (T) yaml.loadAs(str, cls);
        return collectionProperty;
    }

    public static File[] listFiles(String dirPath) throws Exception {
        File dir = new File(dirPath);
        if (!dir.isDirectory() || !dir.exists()) {
            LOG.error("{} is not exists or {} is not a Dir!", dir, dir);
            throw new Exception(dir + " is not exists or " + dir + " is not a Dir!");
        }
        File[] files = dir.listFiles(f -> f.isFile());
        return files;
    }

    public static boolean isKafkaConfig(String str) {
        if (str.startsWith("prop.")) {
            return true;
        }
        return false;
    }

    public static String trimKafkaConfig(String str) {
        if (!isKafkaConfig(str)) {
            return null;
        }
        return str.substring(str.indexOf('.') + 1);
    }

    public static boolean checkServerConfig(ServerConfig config) {
        return true;
    }

    public static boolean checkAgentConfig(AgentConfig config) {
        return true;
    }

}
