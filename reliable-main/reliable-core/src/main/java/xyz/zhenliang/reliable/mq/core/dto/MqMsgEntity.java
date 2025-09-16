package xyz.zhenliang.reliable.mq.core.dto;

import java.time.LocalDateTime;

public class MqMsgEntity {
    protected String msgId;
    protected String businessId; //业务id
    protected String topic;
    protected String tag;
    protected LocalDateTime createdAt = LocalDateTime.now();
    protected String data;
    private String msgBody;
    private int sendCount;
    private LocalDateTime sendLastTime;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public LocalDateTime getSendLastTime() {
        return sendLastTime;
    }

    public void setSendLastTime(LocalDateTime sendLastTime) {
        this.sendLastTime = sendLastTime;
    }
}
