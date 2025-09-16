package xyz.zhenliang.reliable.mq.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * mq消息表
 * </p>
 *
 * @author zzl
 * @since 2025-09-14
 */
@TableName("mq_message")
public class MqMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     */
    @TableId(value = "id")
    private String id;

    /**
     * 业务键（用于关联业务数据）
     */
    private String businessId;

    /**
     * Rocketmq/Kafka的Topic,RabbitMQ的exchange
     */
    private String topic;

    /**
     * Rocketmq的tag,RabbitMQ的routing_key
     */
    private String tag;

    /**
     * 消息内容（JSON格式）
     */
    private String msgBody;

    /**
     * 发送次数
     */
    private Integer sendCount;

    /**
     * 最后一次发送时间
     */
    private LocalDateTime sendLastTime;

    /**
     * 消费者组，rocketmq/kafka的consumer group,rabbitmq中的queue
     */
    private String consumerGroup;

    /**
     * 消费状态：0-未消费,1-消费中,2-消费成功,3-消费失败
     */
    private Integer consumeStatus;

    /**
     * 开始消费时间
     */
    private LocalDateTime consumeStartTime;

    /**
     * 消费成功时间
     */
    private LocalDateTime consumeSuccessTime;

    /**
     * 消费成功状态确认，0-未确认, 1-已确认
     */
    private Integer confirmStatus;

    /**
     * 消费成功确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 消息保存方式:1-发送者保存,2-消费者保存
     */
    private Integer savedBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public Integer getSendCount() {
        return sendCount;
    }

    public void setSendCount(Integer sendCount) {
        this.sendCount = sendCount;
    }

    public LocalDateTime getSendLastTime() {
        return sendLastTime;
    }

    public void setSendLastTime(LocalDateTime sendLastTime) {
        this.sendLastTime = sendLastTime;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Integer getConsumeStatus() {
        return consumeStatus;
    }

    public void setConsumeStatus(Integer consumeStatus) {
        this.consumeStatus = consumeStatus;
    }

    public LocalDateTime getConsumeStartTime() {
        return consumeStartTime;
    }

    public void setConsumeStartTime(LocalDateTime consumeStartTime) {
        this.consumeStartTime = consumeStartTime;
    }

    public LocalDateTime getConsumeSuccessTime() {
        return consumeSuccessTime;
    }

    public void setConsumeSuccessTime(LocalDateTime consumeSuccessTime) {
        this.consumeSuccessTime = consumeSuccessTime;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public LocalDateTime getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(LocalDateTime confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Integer getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(Integer savedBy) {
        this.savedBy = savedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "MqMessage{" +
                "id = " + id +
                ", businessId = " + businessId +
                ", topic = " + topic +
                ", tag = " + tag +
                ", msgBody = " + msgBody +
                ", sendCount = " + sendCount +
                ", sendLastTime = " + sendLastTime +
                ", consumerGroup = " + consumerGroup +
                ", consumeStatus = " + consumeStatus +
                ", consumeStartTime = " + consumeStartTime +
                ", consumeSuccessTime = " + consumeSuccessTime +
                ", confirmStatus = " + confirmStatus +
                ", confirmTime = " + confirmTime +
                ", savedBy = " + savedBy +
                ", createdAt = " + createdAt +
                ", updatedAt = " + updatedAt +
                "}";
    }
}
