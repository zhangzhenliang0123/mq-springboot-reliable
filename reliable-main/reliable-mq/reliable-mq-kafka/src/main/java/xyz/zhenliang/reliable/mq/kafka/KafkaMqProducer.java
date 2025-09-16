package xyz.zhenliang.reliable.mq.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqJsonUtils;
import xyz.zhenliang.reliable.mq.core.support.IMqProducer;
import xyz.zhenliang.reliable.mq.core.constant.MQConstant;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka消息生产者实现类
 * 实现了IMqProducer接口，用于向Kafka发送消息
 */
@Component
public class KafkaMqProducer implements IMqProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaMqProducer.class);

    @Autowired
    protected KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息到Kafka
     *
     * @param topic      消息主题
     * @param tag        消息标签，可为空
     * @param msgId      消息唯一标识
     * @param businessId 业务唯一标识
     * @param msgBody    消息体内容
     */
    @Override
    public void sendMsg(String topic, String tag, String msgId, String businessId, String msgBody) {
        CompletableFuture<SendResult<String, String>> future = null;
        // 使用消息头来标记消息类型
        // 发后不管的方式发送消息
        if (tag != null && !tag.isEmpty()) {
            // 当tag不为空时，将tag作为消息头一起发送
            future = kafkaTemplate.send(MessageBuilder.withPayload(msgBody)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(MQConstant.TAG, tag)
                    .setHeader(MQConstant.MSG_ID, msgId)
                    .setHeader(MQConstant.BUSINESS_ID, businessId)
                    .build());
        } else {
            // 当tag为空时，不设置tag消息头
            future = kafkaTemplate.send(MessageBuilder.withPayload(msgBody)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(MQConstant.MSG_ID, msgId)
                    .setHeader(MQConstant.BUSINESS_ID, businessId)
                    .build());
        }
        // 添加回调处理发送结果
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                // 发送成功
                if (log.isInfoEnabled()) log.info("Sent message to mq success - topic: {}, tag: {}, msgId: {}, businessId: {}\nsendResult: {}"
                        ,topic, tag, msgId, businessId, ReliableMqJsonUtils.toJson(result));
            } else {
                // 发送失败
                log.error("Sent message to mq error - topic: {}, tag: {}, msgId: {}, businessId: {}\nmsgBody: {}"
                        ,topic, tag, msgId, businessId, msgBody,ex);
            }
        });


        // 记录发送日志
        // 根据日志级别决定记录详细程度
        if (log.isInfoEnabled()) {
            log.info("Sent message to mq - topic: {}, tag: {}, msgId: {}, businessId: {}",
                    topic, tag, msgId, businessId);
        } else if (log.isDebugEnabled()) {
            log.info("Sent message to mq - topic: {}, tag: {}, msgId: {}, businessId: {}\nmsgBody: {}",
                    topic, tag, msgId, businessId, msgBody);
        }
    }
}