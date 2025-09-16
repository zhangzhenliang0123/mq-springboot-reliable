package xyz.zhenliang.reliable.mq.mybatisplus.support.consumer;

import xyz.zhenliang.reliable.mq.core.constant.MqIdempotentStatus;
import xyz.zhenliang.reliable.mq.core.dto.MqConsumeConfirmMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsgEntity;

/**
 * MybatisPlus消息持久化适配器接口
 * 定义了消息持久化相关的核心操作方法
 */
public interface IMybatisPlusMqPersisterAdapter {
    /**
     * 保存消息到数据库
     *
     * @param mqMsg 消息对象
     * @param <T>   消息内容类型
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
     * @param <T>           消息内容类型
     * @return 是否需要发送消息确认。否表示不需要发送消息确认（生产者与消费者同库本地确认），是表示需要发送消息确认
     */
    <T> boolean markConsumeSuccess(String consumerGroup, MqMsg<T> mqMsg);

    /**
     * 标记消息消费失败
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息内容类型
     */
    <T> void markConsumeFailed(String consumerGroup, MqMsg<T> mqMsg);

    /**
     * 确认消息消费成功
     *
     * @param mqConsumeConfirmMsg 消费确认消息对象
     */
    void confirmMsgConsumeSuccess(MqConsumeConfirmMsg mqConsumeConfirmMsg);

    /**
     * 处理消息幂等性记录
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息内容类型
     * @return 消息幂等性状态
     */
    <T> MqIdempotentStatus processMessageIdempotentRecord(String consumerGroup, MqMsg<T> mqMsg);
}