package xyz.zhenliang.reliable.mq.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class MqMsgMeta {
    protected String msgId;
    protected String businessId; //业务id
    protected String topic;
    protected String tag;
    protected String confirmTopic;
    protected String confirmTag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime createTime = LocalDateTime.now();

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

    public String getConfirmTopic() {
        return confirmTopic;
    }

    public void setConfirmTopic(String confirmTopic) {
        this.confirmTopic = confirmTopic;
    }

    public String getConfirmTag() {
        return confirmTag;
    }

    public void setConfirmTag(String confirmTag) {
        this.confirmTag = confirmTag;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
