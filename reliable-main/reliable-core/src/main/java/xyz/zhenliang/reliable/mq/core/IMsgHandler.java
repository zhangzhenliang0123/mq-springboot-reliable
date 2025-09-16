package xyz.zhenliang.reliable.mq.core;

import xyz.zhenliang.reliable.mq.core.dto.MqMsg;

/**
 * 消息处理器接口，用于处理MQ消息
 *
 * @param <T> 消息体的数据类型
 */
@FunctionalInterface
public interface IMsgHandler<T> {
    /**
     * 处理MQ消息的方法
     *
     * @param mqMsg MQ消息对象
     */
    public void handle(MqMsg<T> mqMsg);
}