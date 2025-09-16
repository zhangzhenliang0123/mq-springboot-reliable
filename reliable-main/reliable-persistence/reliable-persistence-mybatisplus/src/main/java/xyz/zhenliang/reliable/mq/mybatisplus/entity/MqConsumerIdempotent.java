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
 * MQ消费者幂等表
 * </p>
 *
 * @author zzl
 * @since 2025-09-13
 */
@TableName("mq_consumer_idempotent")
public class MqConsumerIdempotent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * 消息id
     */
    private String msgId;

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
        return "MqConsumerIdempotent{" +
                "id = " + id +
                ", msgId = " + msgId +
                ", consumerGroup = " + consumerGroup +
                ", consumeStatus = " + consumeStatus +
                ", consumeStartTime = " + consumeStartTime +
                ", consumeSuccessTime = " + consumeSuccessTime +
                ", createdAt = " + createdAt +
                ", updatedAt = " + updatedAt +
                "}";
    }
}
