package xyz.zhenliang.reliable.mq.core.support;

import xyz.zhenliang.reliable.mq.core.constant.ConsumeStatus;
import xyz.zhenliang.reliable.mq.core.constant.MqIdempotentStatus;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;

/**
 * MQ消息幂等性处理接口
 * <p>
 * 该接口定义了MQ消息幂等性处理的核心方法，用于确保消息不会被重复消费。
 * 主要功能包括检查消息消费状态、加锁防止重复消费、以及释放分布式锁等。
 *
 * @author zhenliang
 * @since 1.0.0
 */
public interface IMqIdempotent {

    /**
     * 释放消息ID的分布式锁
     * <p>
     * 当消息消费完成后，需要释放该消息对应的分布式锁，以便其他消费者可以正常消费其他消息。
     * 通常在消息消费成功或消费失败后调用此方法。
     *
     * @param consumerGroup 消费者组名称，用于区分不同的消费组
     * @param mqMsg         消息对象，包含消息ID等关键信息
     * @param <T>           消息体的数据类型
     */
    <T> void unlockMessage(String consumerGroup, MqMsg<T> mqMsg);

    /**
     * 检查消息的消费状态，同时如果能处理则加锁
     * <p>
     * 该方法用于检查指定消息是否可以被当前消费者处理，如果可以处理则会对该消息加锁，
     * 防止其他消费者同时处理同一条消息，确保消息处理的幂等性。
     *
     * @param consumerGroup 消费者组名称，用于区分不同的消费组
     * @param mqMsg         消息对象，包含消息ID等关键信息
     * @param <T>           消息体的数据类型
     * @return MqIdempotentStatus 消息当前的消费状态
     * - CAN_CONSUME: 可以消费，消息未被消费过
     * - CONSUMING: 不能消费，消息正在被其他消费者处理
     * - CONSUMED: 不能消费，消息已经被成功消费过
     * - CONSUME_FAILED: 不能消费，消息消费失败需要重试
     */
    <T> MqIdempotentStatus checkAndLockMessage(String consumerGroup, MqMsg<T> mqMsg);

}