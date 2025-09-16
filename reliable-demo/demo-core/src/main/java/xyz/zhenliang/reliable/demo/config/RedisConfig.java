package xyz.zhenliang.reliable.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * 用于配置RedisTemplate及相关序列化策略
 */
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate实例
     *
     * @param redisConnectionFactory Redis连接工厂
     * @return 配置好的RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 设置key的序列化方式为String类型
        template.setKeySerializer(new StringRedisSerializer());
        // 设置value的序列化方式为JSON格式
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置hash结构key的序列化方式为String类型
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置hash结构value的序列化方式为JSON格式
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 初始化RedisTemplate的属性
        template.afterPropertiesSet();
        return template;
    }
}