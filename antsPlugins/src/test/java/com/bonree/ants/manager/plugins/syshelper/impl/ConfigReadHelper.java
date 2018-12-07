package com.bonree.ants.manager.plugins.syshelper.impl;

import com.bonree.ants.manager.plugins.utils.LibUtils;

public class ConfigReadHelper {
    public static String getLibPath(){
        return Class.class.getClass().getResource("/").getPath()+"lib/";
    }
    public static void initClassPath(){
        String path = getLibPath();
        String paths =  System.getProperty("java.library.path");
        if(paths.contains(path)){
            return ;
        }
        try {
            LibUtils.loadLibraryPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
