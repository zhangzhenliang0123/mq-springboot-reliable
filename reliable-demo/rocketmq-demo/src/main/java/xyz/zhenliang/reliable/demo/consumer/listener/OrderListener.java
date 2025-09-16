package xyz.zhenliang.reliable.demo.consumer.listener;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.zhenliang.reliable.demo.constant.DemoConstant;
import xyz.zhenliang.reliable.demo.consumer.handler.OrderHandler;
import xyz.zhenliang.reliable.demo.dto.OrderDTO;
import xyz.zhenliang.reliable.mq.core.IMqConsumer;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;

/**
 * 订单消息监听器
 * 监听RocketMQ中的订单相关消息，并进行处理
 */
@RocketMQMessageListener(
        topic = DemoConstant.ORDER_TOPIC,
        consumerGroup = DemoConstant.ORDER_CONSUMER_GROUP
)
@Component
public class OrderListener implements RocketMQListener<MqMsg<OrderDTO>> {
    private static final Logger log = LoggerFactory.getLogger(OrderListener.class);
    @Autowired
    private OrderHandler orderHandler;
    @Autowired
    private IMqConsumer mqConsumer;

    /**
     * 处理接收到的订单消息
     *
     * @param message 订单消息对象，包含订单数据和消息相关信息
     */
    @Override
    public void onMessage(MqMsg<OrderDTO> message) {
        // 调用通用的消息消费方法，处理订单消息
        mqConsumer.consume(DemoConstant.ORDER_CONSUMER_GROUP, message, this::handleData);
    }

    /**
     * 实际处理订单数据的业务逻辑
     *
     * @param msgDTO 包含订单数据的消息对象
     */
    public void handleData(MqMsg<OrderDTO> msgDTO) {
        OrderDTO orderDTO = msgDTO.getData();
        //if(true) throw new RuntimeException("xxxx");
        // 调用订单处理器处理具体的订单业务逻辑
        orderHandler.handleOrder(orderDTO);
    }
}