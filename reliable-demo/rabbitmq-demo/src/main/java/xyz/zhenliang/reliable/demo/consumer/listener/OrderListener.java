package xyz.zhenliang.reliable.demo.consumer.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.zhenliang.reliable.demo.config.DemoConfig;
import xyz.zhenliang.reliable.demo.consumer.handler.OrderHandler;
import xyz.zhenliang.reliable.demo.dto.OrderDTO;
import xyz.zhenliang.reliable.mq.core.IMqConsumer;
import xyz.zhenliang.reliable.mq.core.dto.MqConsumeConfirmMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;


/**
 * 订单消息监听器
 * 处理订单相关的RabbitMQ消息
 */
@Component
public class OrderListener {
    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(OrderListener.class);
    // 订单业务处理器
    @Autowired
    private OrderHandler orderHandler;
    // MQ消费者接口，用于处理消息消费逻辑
    @Autowired
    private IMqConsumer mqConsumer;

    /**
     * 处理订单消息
     * 监听订单队列，接收并处理订单消息
     *
     * @param message 订单消息对象
     */
    @RabbitListener(queues = DemoConfig.ORDER_QUEUE_NAME)
    public void handleOrder(MqMsg<OrderDTO> message) {
        mqConsumer.consume(DemoConfig.ORDER_QUEUE_NAME, message, this::handleData);
    }

    /**
     * 处理消费确认消息
     * 监听确认队列，处理消息消费成功的确认信息
     *
     * @param message 消费确认消息对象
     */
    @RabbitListener(queues = DemoConfig.CONFIRM_QUEUE_NAME)
    public void handleConfirm(MqConsumeConfirmMsg message) {
        mqConsumer.consumeSuccessConfirm(message);
    }

    /**
     * 处理订单业务数据
     * 执行具体的订单处理逻辑
     *
     * @param msgDTO 包含订单数据的消息对象
     */
    public void handleData(MqMsg<OrderDTO> msgDTO) {
        OrderDTO orderDTO = msgDTO.getData();
        //if(true) throw new RuntimeException("xxxx");
        orderHandler.handleOrder(orderDTO);
    }
}