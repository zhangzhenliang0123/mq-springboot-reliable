package xyz.zhenliang.reliable.mq.core.support;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.zhenliang.reliable.mq.core.IMqConsumer;
import xyz.zhenliang.reliable.mq.core.IMsgHandler;
import xyz.zhenliang.reliable.mq.core.constant.MqIdempotentStatus;
import xyz.zhenliang.reliable.mq.core.dto.MqConsumeConfirmMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqJsonUtils;

/**
 * MQ消费者支持类，提供消息消费的核心逻辑实现
 * 包括消息幂等性检查、消息处理、消费确认等核心功能
 */
@Component
public class MqConsumerSupport implements IMqConsumer {
    private static final Logger log = LoggerFactory.getLogger(MqConsumerSupport.class);

    @Autowired
    protected IMqIdempotent mqIdempotent;

    @Autowired
    protected IMqProducer mqProducer;

    @Autowired
    protected IMqPersister mqPersister;

    /**
     * 消费消息 - 字符串格式消息体版本
     *
     * @param consumerGroup 消费者组
     * @param mqMsgStr      消息体字符串
     * @param typeReference 消息类型引用
     * @param handler       消息处理器
     * @param <T>           消息数据类型
     */
    @Override
    public <T> void consume(String consumerGroup, String mqMsgStr, TypeReference<MqMsg<T>> typeReference, IMsgHandler<T> handler) {
        MqMsg<T> mqMsg = null;
        try {
            mqMsg = ReliableMqJsonUtils.fromJson(mqMsgStr, typeReference);
        } catch (Exception e) {
            log.error("Message Json Parse failed, consumerGroup={}\nmsgBody={}", consumerGroup, mqMsgStr, e);
            throw e;
        }

        consume(consumerGroup, mqMsg, handler);
    }

    /**
     * 消费消息 - MqMsg对象版本
     * 实现消息的幂等性检查、处理和确认流程
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param handler       消息处理器
     * @param <T>           消息数据类型
     */
    @Override
    public <T> void consume(String consumerGroup, MqMsg<T> mqMsg, IMsgHandler<T> handler) {
        try {
            // 检查并锁定消息，确保幂等性
            MqIdempotentStatus mqIdempotentStatus = mqIdempotent.checkAndLockMessage(consumerGroup, mqMsg);
            if (mqIdempotentStatus == MqIdempotentStatus.NOT_CONSUMABLE) {
                log.info("Message not consumable, msgId={}, consumerGroup={}", mqMsg.getMsgId(), consumerGroup);
                return;
            }
            // 消息已消费，发送确认消息
            if (mqIdempotentStatus == MqIdempotentStatus.CONSUMED) {
                log.info("Message already consumed, sending confirmation message, msgId={}, consumerGroup={}", mqMsg.getMsgId(), consumerGroup);
                mqIdempotent.unlockMessage(consumerGroup, mqMsg);
                sendConsumeSuccessMsg(consumerGroup, mqMsg);
                return;
            }
            // 消息可消费，执行消费逻辑
            if (mqIdempotentStatus == MqIdempotentStatus.CONSUMABLE) {
                try {
                    log.info("Start consuming message, msgId={}, consumerGroup={}", mqMsg.getMsgId(), consumerGroup);

                    // 业务处理
                    handler.handle(mqMsg);

                    // 本地标识置消费成功
                    boolean needSendConsumeSuccessMsg = mqPersister.markConsumeSuccess(consumerGroup, mqMsg);
                    // 需要发送消息确认，常用于消费者和生产者不是同一个数据库
                    if (needSendConsumeSuccessMsg) sendConsumeSuccessMsg(consumerGroup, mqMsg);
                    log.info("Message consumption succeeded, msgId={}, consumerGroup={}", mqMsg.getMsgId(), consumerGroup);
                } catch (Exception e) {
                    // log.error("Message consumption failed, msgId={}, consumerGroup={}", mqMsg.getMsgId(), consumerGroup, e);
                    mqPersister.markConsumeFailed(consumerGroup, mqMsg);
                    throw e;
                } finally {
                    mqIdempotent.unlockMessage(consumerGroup, mqMsg);
                }

            }
            return;
        } catch (Exception e) {
            log.error("Message consumption failed, msgId={}, consumerGroup={}\nmsgBody={}", mqMsg.getMsgId(), consumerGroup, ReliableMqJsonUtils.toJson(mqMsg), e);
            throw e;
        }
    }


    /**
     * 发送消费成功确认消息
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息数据类型
     */
    public <T> void sendConsumeSuccessMsg(String consumerGroup, MqMsg<T> mqMsg) {
        MqConsumeConfirmMsg mqConsumeConfirmMsg = new MqConsumeConfirmMsg();
        mqConsumeConfirmMsg.setMsgId(mqMsg.getMsgId());
        mqConsumeConfirmMsg.setConsumerGroup(consumerGroup);
        mqProducer.sendMsg(mqMsg.getConfirmTopic(), mqMsg.getConfirmTag(), mqMsg.getMsgId()
                , mqMsg.getBusinessId(), ReliableMqJsonUtils.toJson(mqConsumeConfirmMsg));
    }

    /**
     * 消费成功确认 - 字符串格式消息体版本
     *
     * @param mqConsumeConfirmMsgStr 消费确认消息字符串
     */
    @Override
    public void consumeSuccessConfirm(String mqConsumeConfirmMsgStr) {
        MqConsumeConfirmMsg mqConsumeConfirmMsg = null;
        try {
            mqConsumeConfirmMsg = ReliableMqJsonUtils.fromJson(mqConsumeConfirmMsgStr, MqConsumeConfirmMsg.class);
        } catch (Exception e) {
            log.error("consumeSuccessConfirm,Message Json Parse failed, \nmsgBody={}", mqConsumeConfirmMsgStr, e);
            throw e;
        }
        consumeSuccessConfirm(mqConsumeConfirmMsg);
    }

    /**
     * 消费成功确认 - MqConsumeConfirmMsg对象版本
     *
     * @param mqConsumeConfirmMsg 消费确认消息对象
     */
    @Override
    public void consumeSuccessConfirm(MqConsumeConfirmMsg mqConsumeConfirmMsg) {
        mqPersister.confirmMsgConsumeSuccess(mqConsumeConfirmMsg);
        if(log.isInfoEnabled()) log.info("consumeSuccessConfirm finished, msgId={}, consumerGroup={}", mqConsumeConfirmMsg.getMsgId(), mqConsumeConfirmMsg.getConsumerGroup());
    }
}