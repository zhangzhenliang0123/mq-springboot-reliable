package xyz.zhenliang.reliable.mq.core.support;

/**
 * MQ生产者接口
 * 定义了消息发送的标准方法，用于向消息队列发送可靠消息
 *
 * @author zhenliang
 * @since 1.0.0
 */
public interface IMqProducer {

    /**
     * 发送消息到指定的主题
     *
     * @param topic      消息主题，用于分类消息类型
     * @param tag        消息标签，用于进一步细分消息
     * @param msgId      消息唯一标识符，用于追踪和去重
     * @param businessId 业务标识符，关联具体业务场景
     * @param msgBody    消息体内容，实际要传输的数据
     */
    void sendMsg(String topic, String tag, String msgId, String businessId, String msgBody);
}