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
 * MQ生产者消息表
 * </p>
 *
 * @author zzl
 * @since 2025-09-13
 */
@TableName("mq_producer_message")
public class MqProducerMessage implements Serializable {

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
     * 0-未确认, 1-已确认
     */
    private Integer confirmStatus;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    @Override
    public String toString() {
        return "MqProducerMessage{" +
                "id = " + id +
                ", businessId = " + businessId +
                ", topic = " + topic +
                ", tag = " + tag +
                ", msgBody = " + msgBody +
                ", sendCount = " + sendCount +
                ", sendLastTime = " + sendLastTime +
                ", confirmStatus = " + confirmStatus +
                ", confirmTime = " + confirmTime +
                ", createdAt = " + createdAt +
                "}";
    }
}
