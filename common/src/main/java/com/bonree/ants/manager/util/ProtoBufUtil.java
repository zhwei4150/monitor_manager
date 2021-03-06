package com.bonree.ants.manager.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtoBufUtil {

    /**
     * 序列化对象为字节数组
     * 
     * @param obj
     * @return
     * @throws IOException 
     */
    public static <T> byte[] serialize(T obj) throws IOException {
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        ProtostuffIOUtil.writeDelimitedTo(byteArrayOutput, obj, schema, LinkedBuffer.allocate(256));
        
        return byteArrayOutput.toByteArray();
    }
    
    /**
     * 把对象序列化并写入输出流
     * 
     * @param output
     * @param obj
     * @throws IOException
     */
    public static <T> void writeTo(OutputStream output, T obj) throws IOException {
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        ProtostuffIOUtil.writeDelimitedTo(output, obj, schema, LinkedBuffer.allocate(256));
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T newInstance(Class<T> cls) throws Exception {
        Constructor<?> constructor = cls.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (T) constructor.newInstance();
    }
    
    /**
     * 通过字节数组解析对象
     * 
     * @param bytes
     * @param cls
     * @return
     * @throws Exception 
     */
    public static <T> T deserializeThrowable(byte[] bytes, Class<T> cls) throws Exception {
        T obj = newInstance(cls);
        @SuppressWarnings("unchecked")
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        ProtostuffIOUtil.mergeDelimitedFrom(new ByteArrayInputStream(bytes), obj, schema);
        
        return obj;
    }
    
    /**
     * 通过字节数组解析对象
     * 
     * @param bytes
     * @param cls
     * @return
     */
    public static <T> T deserialize(byte[] bytes, Class<T> cls) {
        T obj = null;
        try {
            obj = newInstance(cls);
            @SuppressWarnings("unchecked")
            Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
            ProtostuffIOUtil.mergeDelimitedFrom(new ByteArrayInputStream(bytes), obj, schema);
        } catch (Exception e) {
            obj = null;
        }
        
        return obj;
    }
    
    /**
     * 通过输入流中的数据解析对象
     * 
     * @param input
     * @param cls
     * @return
     */
    public static <T> T readFrom(InputStream input, Class<T> cls) {
        T obj = null;
        try {
            obj = newInstance(cls);
            @SuppressWarnings("unchecked")
            Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());  
            ProtostuffIOUtil.mergeDelimitedFrom(input, obj, schema);
        } catch (Exception e) {
            obj = null;
        }
        
        return obj;
    }
    
    private ProtoBufUtil() {}

}
