package xyz.zhenliang.reliable.demo.consumer.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import xyz.zhenliang.reliable.demo.constant.DemoConstant;
import xyz.zhenliang.reliable.demo.consumer.handler.OrderHandler;
import xyz.zhenliang.reliable.demo.dto.OrderDTO;
import xyz.zhenliang.reliable.mq.core.IMqConsumer;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;

import java.lang.reflect.Type;

/**
 * 订单消息监听器
 * 用于监听和处理订单相关的Kafka消息
 */
@Component
public class OrderListener {
    private static final Logger log = LoggerFactory.getLogger(OrderListener.class);
    @Autowired
    private OrderHandler orderHandler;
    @Autowired
    private IMqConsumer mqConsumer;

    /**
     * 处理订单消息
     * 监听订单主题，接收并处理新的订单消息
     *
     * @param message 订单消息内容
     */
    @KafkaListener(
            topics = DemoConstant.ORDER_TOPIC,
            groupId = DemoConstant.ORDER_CONSUMER_GROUP
    )
    public void handleOrder(@Payload String message) {
        mqConsumer.consume(DemoConstant.ORDER_CONSUMER_GROUP, message
                , new TypeReference<MqMsg<OrderDTO>>() {
                }, this::handleData);
    }

    /**
     * 处理确认消息
     * 监听确认主题，处理消息消费成功的确认信息
     *
     * @param message 确认消息内容
     */
    @KafkaListener(
            topics = DemoConstant.CONFIRM_TOPIC,
            groupId = DemoConstant.CONFIRM_CONSUMER_GROUP
    )
    public void handleConfirm(@Payload String message) {
        mqConsumer.consumeSuccessConfirm(message);
    }

    /**
     * 订单数据处理方法
     * 将消息中的订单数据提取出来并交由订单处理器进行业务处理
     *
     * @param msgDTO 包含订单数据的消息对象
     */
    public void handleData(MqMsg<OrderDTO> msgDTO) {
        OrderDTO orderDTO = msgDTO.getData();
        //if(true) throw new RuntimeException("xxxx");
        orderHandler.handleOrder(orderDTO);
    }
}