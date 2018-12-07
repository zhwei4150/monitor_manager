package com.bonree.ants.manager.job;

import java.util.List;

public class JobProperty {

    public static final String STANARD_LAUNCHER = "standard";
    public static final String TIMED_COLLECTION = "timed";
    public static final String STREAM_COLLECTION = "stream";
    public static final String COMMON_STORAGE = "common";
    public static final String QUEUE_STORAGE = "queue";

    private String name;

    private String agentNodes;

    private String launcherFactory;

    private String collectionType;

    private String collectionConfigClass;

    private String collectionExecClass;

    private String collectionConfig;

    private String dependentFiles;

    private List<StorageProperty> dataStorages;

    public JobProperty() {
        this.launcherFactory = STANARD_LAUNCHER;
        this.collectionType = TIMED_COLLECTION;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public String getCollectionConfigClass() {
        return collectionConfigClass;
    }

    public void setCollectionConfigClass(String collectionConfigClass) {
        this.collectionConfigClass = collectionConfigClass;
    }

    public String getCollectionExecClass() {
        return collectionExecClass;
    }

    public void setCollectionExecClass(String collectionExecClass) {
        this.collectionExecClass = collectionExecClass;
    }

    public String getCollectionConfig() {
        return collectionConfig;
    }

    public void setCollectionConfig(String collectionConfig) {
        this.collectionConfig = collectionConfig;
    }

    public List<StorageProperty> getDataStorages() {
        return dataStorages;
    }

    public void setDataStorages(List<StorageProperty> dataStorages) {
        this.dataStorages = dataStorages;
    }

    public String getAgentNodes() {
        return agentNodes;
    }

    public void setAgentNodes(String agentNodes) {
        this.agentNodes = agentNodes;
    }

    public String getLauncherFactory() {
        return launcherFactory;
    }

    public void setLauncherFactory(String launcherFactory) {
        this.launcherFactory = launcherFactory;
    }

    public String getDependentFiles() {
        return dependentFiles;
    }

    public void setDependentFiles(String dependentFiles) {
        this.dependentFiles = dependentFiles;
    }

    @Override
    public String toString() {
        return "JobProperty [name=" + name + ", agentNodes=" + agentNodes + ", launcherFactory=" + launcherFactory + ", collectionType=" + collectionType + ", collectionConfigClass=" + collectionConfigClass + ", collectionExecClass=" + collectionExecClass + ", collectionConfig=" + collectionConfig + ", dataStorages=" + dataStorages + "]";
    }

    public static class StorageProperty {
        private String storageType;
        private String storageConfigClass;
        private String storageExecClass;
        private String storageConfig;

        public StorageProperty() {
            this.storageType = COMMON_STORAGE;
        }

        public String getStorageType() {
            return storageType;
        }

        public void setStorageType(String storageType) {
            this.storageType = storageType;
        }

        public String getStorageConfigClass() {
            return storageConfigClass;
        }

        public void setStorageConfigClass(String storageConfigClass) {
            this.storageConfigClass = storageConfigClass;
        }

        public String getStorageExecClass() {
            return storageExecClass;
        }

        public void setStorageExecClass(String storageExecClass) {
            this.storageExecClass = storageExecClass;
        }

        public String getStorageConfig() {
            return storageConfig;
        }

        public void setStorageConfig(String storageConfig) {
            this.storageConfig = storageConfig;
        }

        @Override
        public String toString() {
            return "StorageProperty [storageType=" + storageType + ", storageConfigClass=" + storageConfigClass + ", storageExecClass=" + storageExecClass + ", storageConfig=" + storageConfig + "]";
        }

    }
}
