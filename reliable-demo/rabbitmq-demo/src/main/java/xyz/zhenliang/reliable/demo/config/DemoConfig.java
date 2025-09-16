package xyz.zhenliang.reliable.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置类
 * 配置交换机、队列、绑定关系以及消息转换器
 */
@Configuration
public class DemoConfig {

    /**
     * 交换机名称
     */
    public static final String ORDER_EXCHANGE_NAME = "demo.order.exchange";

    /**
     * 队列名称
     */
    public static final String ORDER_QUEUE_NAME = "demo.order.queue";

    /**
     * 路由键
     */
    public static final String ORDER_ROUTING_KEY = "demo.order.routing.key";

    /**
     * 交换机名称
     */
    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";

    /**
     * 队列名称
     */
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";

    /**
     * 路由键
     */
    public static final String CONFIRM_ROUTING_KEY = "confirm.routing.key";


    /**
     * 创建持久化直连交换机
     *
     * @return DirectExchange对象
     */
    @Bean
    public DirectExchange orderExchange() {
        // 创建持久化交换机,不需要持久化（durable）,自动删除（如果没有队列绑定到该交换机会被自动删除）
        return new DirectExchange(ORDER_EXCHANGE_NAME, false, true);
    }

    /**
     * 创建死信交换机
     *
     * @return DirectExchange对象
     */
    @Bean
    public DirectExchange confirmExchange() {
        return new DirectExchange(CONFIRM_EXCHANGE_NAME, false, false);
    }

    /**
     * 创建持久化队列
     *
     * @return Queue对象
     */
    @Bean
    public Queue orderQueue() {
        // 创建队列,设置不进行持久化（durable）,队列是否具有排他性（允许多个连接消费），自动删除（没有消费者连接的时候被自动删除）
        return new Queue(ORDER_QUEUE_NAME, false, false, false);
    }

    /**
     * 创建死信队列
     *
     * @return Queue对象
     */
    @Bean
    public Queue confirmQueue() {
        return new Queue(CONFIRM_QUEUE_NAME, false, false, false);
    }

    /**
     * 绑定交换机和队列
     *
     * @param orderQueue    队列对象
     * @param orderExchange 交换机对象
     * @return Binding对象
     */
    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }


    @Bean
    public Binding confirmBinding(Queue confirmQueue, DirectExchange confirmExchange) {
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }
}