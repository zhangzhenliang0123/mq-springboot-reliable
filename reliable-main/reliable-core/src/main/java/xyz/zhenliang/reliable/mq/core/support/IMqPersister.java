package xyz.zhenliang.reliable.mq.core.support;

import xyz.zhenliang.reliable.mq.core.dto.MqConsumeConfirmMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsgEntity;

/**
 * 消息持久化接口
 * 定义了消息的存储、查询、重发以及消费状态管理等核心操作
 */
public interface IMqPersister {
    /**
     * 保存消息
     *
     * @param mqMsg 消息对象
     * @param <T>   消息体类型
     */
    <T> void saveMsg(MqMsg<T> mqMsg);

    /**
     * 根据消息ID获取消息实体
     *
     * @param msgId 消息ID
     * @return 消息实体对象
     */
    MqMsgEntity getMsgById(String msgId);

    /**
     * 重新发送消息
     *
     * @param msgId 消息ID
     */
    void resendMsg(String msgId);

    /**
     * 标记消息消费成功
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息体类型
     * @return 是否需要发送消息确认。否表示不需要发送消息确认（生产者与消费者同库本地确认），是表示需要发送消息确认
     */
    <T> boolean markConsumeSuccess(String consumerGroup, MqMsg<T> mqMsg);

    /**
     * 标记消息消费失败
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息体类型
     */
    <T> void markConsumeFailed(String consumerGroup, MqMsg<T> mqMsg);

    /**
     * 确认消息消费成功
     *
     * @param mqConsumeConfirmMsg 消费确认消息对象
     */
    void confirmMsgConsumeSuccess(MqConsumeConfirmMsg mqConsumeConfirmMsg);
}