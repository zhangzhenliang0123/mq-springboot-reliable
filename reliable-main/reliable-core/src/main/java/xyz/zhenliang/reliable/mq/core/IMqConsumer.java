package xyz.zhenliang.reliable.mq.core;

import com.fasterxml.jackson.core.type.TypeReference;
import xyz.zhenliang.reliable.mq.core.dto.MqConsumeConfirmMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;

/**
 * MQ消费者接口
 * 定义了消息消费和消费确认的相关方法
 */
public interface IMqConsumer {

    /**
     * 消费消息
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param handler       消息处理器
     * @param <T>           消息内容类型
     */
    default <T> void consume(String consumerGroup, MqMsg<T> mqMsg, IMsgHandler<T> handler) {

    }

    /**
     * 消费消息
     *
     * @param consumerGroup 消费者组
     * @param mqMsgStr      消息字符串
     * @param typeReference 消息类型引用
     * @param handler       消息处理器
     * @param <T>           消息内容类型
     */
    default <T> void consume(String consumerGroup, String mqMsgStr, TypeReference<MqMsg<T>> typeReference, IMsgHandler<T> handler) {

    }

    /**
     * 消费成功确认
     *
     * @param mqConsumeConfirmMsg 消费确认消息
     */
    default void consumeSuccessConfirm(MqConsumeConfirmMsg mqConsumeConfirmMsg) {

    }

    /**
     * 消费成功确认
     *
     * @param mqConsumeConfirmMsgStr 消费确认消息字符串
     */
    default void consumeSuccessConfirm(String mqConsumeConfirmMsgStr) {

    }
}