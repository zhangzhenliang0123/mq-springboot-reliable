package xyz.zhenliang.reliable.mq.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 用于配置RabbitMQ相关的组件和bean
 */
@Configuration
public class RabbitMQConfig {
    /**
     * 配置JSON消息转换器
     * 用于在发送和接收消息时将对象序列化为JSON格式或从JSON格式反序列化为对象
     *
     * @param objectMapper JSON对象映射器
     * @return 消息转换器实例
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}