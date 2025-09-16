package xyz.zhenliang.reliable.mq.rocketmq;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqJsonUtils;
import xyz.zhenliang.reliable.mq.core.support.IMqProducer;

/**
 * RocketMQ消息生产者实现类
 * 实现了IMqProducer接口，用于向RocketMQ发送消息
 */
@Component
public class RocketMqProducer implements IMqProducer {
    private static final Logger log = LoggerFactory.getLogger(RocketMqProducer.class);

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送消息到RocketMQ
     *
     * @param topic      消息主题
     * @param tag        消息标签，用于分类过滤（可为null）
     * @param msgId      消息唯一标识符
     * @param businessId 业务唯一标识符
     * @param msgBody    消息体内容
     */
    @Override
    public void sendMsg(String topic, String tag, String msgId, String businessId, String msgBody) {
        // 构造完整的destination格式: topic:tag
        String destination = tag != null && !tag.isEmpty() ? topic + ":" + tag : topic;

        // 使用sendOneWay方式发送消息，该方式不等待Broker返回确认，性能较高但不保证消息一定送达
        // rocketMQTemplate.sendOneWay(destination, msgBody);

        //异步发送消息，发送情况日志输出
        rocketMQTemplate.asyncSend(destination, msgBody,new SendCallback(){
            @Override
            public void onSuccess(SendResult sendResult) {

                if (log.isInfoEnabled()) log.info("Sent message to mq success - topic: {}, tag: {}, msgId: {}, businessId: {}\nsendResult: {}"
                        ,topic, tag, msgId, businessId, ReliableMqJsonUtils.toJson(sendResult));
            }

            @Override
            public void onException(Throwable e) {
                log.error("Sent message to mq error - topic: {}, tag: {}, msgId: {}, businessId: {}\nmsgBody: {}"
                        ,topic, tag, msgId, businessId, msgBody,e);
            }
        });

        // 根据日志级别记录发送日志
        if (log.isInfoEnabled()) {
            log.info("Sent message to mq - topic: {}, tag: {}, msgId: {}, businessId: {}",
                    topic, tag, msgId, businessId);
        } else if (log.isDebugEnabled()) {
            log.info("Sent message to mq - topic: {}, tag: {}, msgId: {}, businessId: {}\nmsgBody: {}",
                    topic, tag, msgId, businessId, msgBody);
        }
    }
}