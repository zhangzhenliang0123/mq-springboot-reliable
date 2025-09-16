package xyz.zhenliang.reliable.mq.core;

import xyz.zhenliang.reliable.mq.core.dto.MqMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsgEntity;

public interface IMqSender {
    /**
     * 发送消息到MQ
     *
     * @param topic        消息主题，用于分类消息类型
     * @param tag          消息标签，用于进一步细化消息分类
     * @param data         消息数据内容，具体业务数据
     * @param businessId   业务唯一标识，用于业务追踪
     * @param confirmTopic 确认消息主题，用于发送确认消息
     * @param confirmTag   确认消息标签，用于发送确认消息的标签
     * @param <T>          消息数据类型，支持泛型
     * @return MqMsg<T> 消息对象，包含消息的完整信息
     */
    public <T> MqMsg<T> sendMsg(String topic, String tag, T data, String businessId, String confirmTopic, String confirmTag);

    /**
     * 根据消息ID重新发送消息
     *
     * @param msgId 消息唯一标识，用于定位需要重发的消息
     */
    public void resendMsg(String msgId);

    /**
     * 根据消息实体重新发送消息
     *
     * @param msgEntity 消息实体对象，包含消息的完整信息
     */
    public void resendMsg(MqMsgEntity msgEntity);
}