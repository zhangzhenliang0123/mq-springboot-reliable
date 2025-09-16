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
 * MQ生产者消费配置表
 * </p>
 *
 * @author zzl
 * @since 2025-09-13
 */
@TableName("mq_producer_consume_config")
public class MqProducerConsumeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String id;

    /**
     * Rocketmq/Kafka的Topic,RabbitMQ的exchange
     */
    private String topic;

    /**
     * Rocketmq的tag,RabbitMQ的routing_key
     */
    private String tag;

    /**
     * 消费者组，rocketmq/kafka的consumer group,rabbitmq中的queue
     */
    private String consumerGroup;

    /**
     * 是否启用：0-禁用,1-启用
     */
    private Integer isEnabled;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Integer getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
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
        return "MqProducerConsumeConfig{" +
                "id = " + id +
                ", topic = " + topic +
                ", tag = " + tag +
                ", consumerGroup = " + consumerGroup +
                ", isEnabled = " + isEnabled +
                ", createdAt = " + createdAt +
                ", updatedAt = " + updatedAt +
                "}";
    }
}
