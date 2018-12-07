package com.bonree.ants.manager.proto;

import java.util.List;

public class FilesRequestProto {

    private String transName;

    private List<String> filePaths; // 可以是单个文件，也可以是一个文件目录

    
    
    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }

}
