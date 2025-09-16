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
import xyz.zhenliang.reliable.mq.core.dto.MqConsumeConfirmMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;

/**
 * 消费确认消息监听器
 * 用于监听和处理消费确认消息，确保消息的可靠消费
 */
@RocketMQMessageListener(
        topic = DemoConstant.CONFIRM_TOPIC,
        consumerGroup = DemoConstant.CONFIRM_CONSUMER_GROUP
)
@Component
public class ConfirmListener implements RocketMQListener<MqConsumeConfirmMsg> {
    private static final Logger log = LoggerFactory.getLogger(ConfirmListener.class);

    @Autowired
    private IMqConsumer mqConsumer;

    /**
     * 监听并处理消费确认消息
     *
     * @param message 消费确认消息对象
     */
    @Override
    public void onMessage(MqConsumeConfirmMsg message) {
        // 调用MQ消费者组件处理消费成功的确认逻辑
        mqConsumer.consumeSuccessConfirm(message);
    }
}