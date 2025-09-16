package xyz.zhenliang.reliable.mq.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.zhenliang.reliable.mq.commons.exception.TechException;

public class ReliableMqJsonUtils {

    /**
     * 将对象转换为JSON字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            ObjectMapper objectMapper = ReliableMqSpringUtils.getBean(ObjectMapper.class);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new TechException(e);
        }

    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            ObjectMapper objectMapper = ReliableMqSpringUtils.getBean(ObjectMapper.class);
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new TechException("JSON 转换失败: " + e.getOriginalMessage(), e);
        }
    }

    /**
     * 新增：支持泛型转换的方法
     *
     * @param json          JSON字符串
     * @param typeReference 类型引用(如 new TypeReference<List<User>>(){})
     * @param <T>           目标类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            ObjectMapper objectMapper = ReliableMqSpringUtils.getBean(ObjectMapper.class);
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new TechException("JSON泛型转换失败: " + e.getOriginalMessage(), e);
        }
    }
}
