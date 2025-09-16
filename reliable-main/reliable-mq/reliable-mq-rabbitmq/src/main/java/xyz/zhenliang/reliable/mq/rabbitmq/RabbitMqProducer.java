package xyz.zhenliang.reliable.mq.rabbitmq;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.zhenliang.reliable.mq.core.support.IMqProducer;
import xyz.zhenliang.reliable.mq.core.constant.MQConstant;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqUtils;

/**
 * RabbitMQ消息生产者实现类
 * 提供可靠的消息发送功能，支持消息确认机制和路由失败处理
 *
 * @author zhenliang
 * @since 1.0.0
 */
@Component
public class RabbitMqProducer implements IMqProducer {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqProducer.class);

    @Autowired
    protected RabbitTemplate rabbitTemplate;

    /**
     * 初始化RabbitTemplate回调函数
     * 设置消息发送确认回调和消息返回回调
     * confirmCallback: 处理消息发送到Broker的成功/失败情况
     * returnsCallback: 处理消息无法路由到队列的情况
     */
    @PostConstruct
    public void init() {
        // 设置消息发送确认回调，处理消息发送到Exchange的成功/失败情况
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData != null) {
                String msgId = correlationData.getId();
                if (ack) {
                    log.info("Message sent successfully to Exchange,msgId: {}", msgId);
                } else {
                    log.error("Message failed to send message to Exchange,msgId: {}, cause: {}", msgId, cause);
                }
            }
        });
        // 设置消息返回回调，处理消息无法路由到队列的情况
        rabbitTemplate.setReturnsCallback(returned -> {
            // 获取消息的关联ID
            returned.getMessage().getMessageProperties().getCorrelationId();
            log.error("Message routing to queue failed.No suitable queue can be routed to. \nReply code: {}, Reason: {}, Exchange: {}, Routing key: {}, Message: {}",
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    RabbitmqUtils.toString(returned.getMessage().getBody()));
        });

    }

    /**
     * 发送消息到RabbitMQ
     *
     * @param topic      交换机名称
     * @param tag        路由键
     * @param msgId      消息唯一标识符
     * @param businessId 业务标识符
     * @param msgBody    消息体内容
     */
    @Override
    public void sendMsg(String topic, String tag, String msgId, String businessId, String msgBody) {
        // 1.设置消息属性
        MessageProperties properties = new MessageProperties();
        // properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT); // 消息持久化
        properties.setMessageId(msgId); // 设置消息ID
        properties.setCorrelationId(msgId); // 设置关联ID用于确认机制
        if (businessId != null)
            properties.setHeader(MQConstant.BUSINESS_ID, businessId); // 设置业务ID头部信息

        // 构建AMQP消息对象
        Message amqpMessage = MessageBuilder.withBody(
                        ReliableMqUtils.toByteArray(msgBody))
                .andProperties(properties)
                .build();
        // 创建关联数据用于消息确认
        CorrelationData correlationData = new CorrelationData(msgId);

        // 2.消息发送
        rabbitTemplate.send(topic, tag, amqpMessage, correlationData);
        // 记录发送日志
        if (log.isInfoEnabled()) {
            log.info("Sent message to mq - topic: {}, tag: {}, msgId: {}, businessId: {}",
                    topic, tag, msgId, businessId);
        } else if (log.isDebugEnabled()) {
            log.info("Sent message to mq - topic: {}, tag: {}, msgId: {}, businessId: {}\nmsgBody: {}",
                    topic, tag, msgId, businessId, msgBody);
        }
    }
}