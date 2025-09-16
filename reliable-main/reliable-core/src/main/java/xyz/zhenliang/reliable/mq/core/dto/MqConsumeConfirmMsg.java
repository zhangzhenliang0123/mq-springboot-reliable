package xyz.zhenliang.reliable.mq.core.dto;

public class MqConsumeConfirmMsg {
    private String msgId;
    private String consumerGroup;
    public MqConsumeConfirmMsg() {
    }
    public MqConsumeConfirmMsg(String msgId, String consumerGroup) {
        this.msgId = msgId;
        this.consumerGroup = consumerGroup;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }
}
