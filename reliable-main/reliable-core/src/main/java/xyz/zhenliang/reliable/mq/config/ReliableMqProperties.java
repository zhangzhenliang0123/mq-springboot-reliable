package xyz.zhenliang.reliable.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mq")
public class ReliableMqProperties {
    /**
     * 消息ID前缀
     */
    private String idPrefix = "";

    /**
     * 消费者配置
     */
    private Consumer consumer = new Consumer();

    /**
     * 幂等配置
     */
    private Idempotent idempotent = new Idempotent();

    // getter和setter方法
    public String getIdPrefix() {
        return idPrefix;
    }

    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Idempotent getIdempotent() {
        return idempotent;
    }

    public void setIdempotent(Idempotent idempotent) {
        this.idempotent = idempotent;
    }

    /**
     * 消费者配置内部类
     */
    public static class Consumer {
        /**
         * 消费者组类型,1表示单一消费者组，2表示多个消费者组
         */
        private Integer type = 2;

        /**
         * 生产者和消费者是否同库，同库时是否需要消息确认
         */
        private Boolean sameDatabaseConfirmation = false;

        // getter和setter方法
        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Boolean getSameDatabaseConfirmation() {
            return sameDatabaseConfirmation;
        }

        public void setSameDatabaseConfirmation(Boolean sameDatabaseConfirmation) {
            this.sameDatabaseConfirmation = sameDatabaseConfirmation;
        }
    }

    /**
     * 幂等配置内部类
     */
    public static class Idempotent {
        /**
         * redis幂等锁前缀,幂等锁名称为 前缀:消费者组:消息ID
         */
        private String lockPrefix = "mq:idempotent:";

        /**
         * 幂等锁过期时间(毫秒)
         */
        private Long expireTime = 5000L;

        // getter和setter方法
        public String getLockPrefix() {
            return lockPrefix;
        }

        public void setLockPrefix(String lockPrefix) {
            this.lockPrefix = lockPrefix;
        }

        public Long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Long expireTime) {
            this.expireTime = expireTime;
        }
    }
}
