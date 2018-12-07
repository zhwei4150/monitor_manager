package com.bonree.ants.manager.util;

import java.util.Arrays;

public class UriUtil {

    private static final char SPLIT_CHAR = '/';
    private static final String SPLIT_STR = "/";

    private UriUtil() {
    }

    public static String makeUri(String... path) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(path).forEach(str -> sb.append(SPLIT_STR + str));
        return sb.toString();
    }

    public static String makeUriWithSuf(String... path) {
        return makeUriWithSuf(path) + SPLIT_STR;
    }

    public static String[] splitUri(String uri) {
        String tmp = removePreSuf(uri);
        return tmp.split(SPLIT_STR);
    }

    public static String removePreSuf(String uri) {
        if (uri.charAt(0) == SPLIT_CHAR && uri.charAt(uri.length() - 1) == SPLIT_CHAR) {
            return uri.substring(1, uri.length() - 1);
        }
        if (uri.charAt(0) == SPLIT_CHAR && uri.charAt(uri.length() - 1) != SPLIT_CHAR) {
            return uri.substring(1);
        }
        if (uri.charAt(0) != SPLIT_CHAR && uri.charAt(uri.length() - 1) == SPLIT_CHAR) {
            return uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

}
