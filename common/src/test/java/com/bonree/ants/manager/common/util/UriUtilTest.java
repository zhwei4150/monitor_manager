package com.bonree.ants.manager.common.util;

import java.util.Arrays;

import org.junit.Test;

import com.bonree.ants.manager.util.UriUtil;

public class UriUtilTest {

    @Test
    public void testMakeUri() {
        System.out.println(UriUtil.makeUri("aaa"));
        System.out.println(UriUtil.makeUri("aaa", "bbb"));
        System.out.println(UriUtil.makeUri("aaa", "bbb", "ccc"));
    }
    
    @Test
    public void testSplitUri() {
        System.out.println(Arrays.asList(UriUtil.splitUri("/a/b/c/d/e")));
        System.out.println(Arrays.asList(UriUtil.splitUri("a/b/c/d/e/")));
    }
    
    @Test
    public void testRemovePreSuf() {
        System.out.println(UriUtil.removePreSuf("/a/"));
        System.out.println(UriUtil.removePreSuf("/a"));
        System.out.println(UriUtil.removePreSuf("a/"));
        System.out.println(UriUtil.removePreSuf("/a/b/c"));
    }
    
    

}
