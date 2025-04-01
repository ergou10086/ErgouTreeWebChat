package com.ergouwebchat.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSON工具类
 * <p>提供JSON序列化和反序列化功能</p>
 */
public class JsonUtils {
    private static final Logger LOGGER = Logger.getLogger(JsonUtils.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    static {
        // 注册Java 8时间模块，支持LocalDateTime等类型
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private JsonUtils() {
        throw new AssertionError("工具类不应被实例化");
    }
    
    /**
     * 将对象转换为JSON字符串
     * @param object 要转换的对象
     * @return JSON字符串，转换失败则返回"{}"
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "JSON序列化失败: " + e.getMessage(), e);
            return "{}";
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型的对象
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象，转换失败则返回null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "JSON反序列化失败: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 检查字符串是否为有效的JSON格式
     * @param json 要检查的JSON字符串
     * @return 如果是有效的JSON则返回true
     */
    public static boolean isValidJson(String json) {
        try {
            OBJECT_MAPPER.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 美化JSON字符串（格式化）
     * @param json 原始JSON字符串
     * @return 格式化后的JSON字符串
     */
    public static String prettyPrint(String json) {
        try {
            Object jsonObj = OBJECT_MAPPER.readValue(json, Object.class);
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "JSON格式化失败: " + e.getMessage(), e);
            return json;
        }
    }
}
