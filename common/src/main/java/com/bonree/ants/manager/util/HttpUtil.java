package com.bonree.ants.manager.util;

import java.util.Map;

import com.google.common.collect.Maps;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpUtil {
    private static final String DATA_FIELD = "data";
    private static final String CODE_FIELD = "code";


    public static FullHttpRequest constructRequest(String uri, String contentType, ByteBuf buff) {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, buff);
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, buff.readableBytes());
        return request;

    }

    public static FullHttpResponse constructResponse(String contentType, ByteBuf buff) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buff);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buff.readableBytes());
        return response;
    }

    public static Map<String, Object> commandResult(int code, String data) {
        Map<String, Object> result = Maps.newHashMap();
        result.put(CODE_FIELD, code);
        result.put(DATA_FIELD, data);
        return result;
    }
}
