package xyz.zhenliang.reliable.mq.mybatisplus.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import xyz.zhenliang.reliable.mq.core.constant.MqIdempotentStatus;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;
import xyz.zhenliang.reliable.mq.core.support.IMqIdempotent;
import xyz.zhenliang.reliable.mq.mybatisplus.support.consumer.MybatisPlusMqPersisterAdapterFactory;

import java.util.concurrent.TimeUnit;

/**
 * 消息幂等性支持组件
 * <p>
 * 该组件负责处理消息的幂等性控制，防止消息被重复消费。
 * 使用Redis分布式锁保证同一消息在同一消费者组中不会被并发处理，
 * 并结合数据库持久化确保消息处理状态的可靠性。
 */
@Component
public class MqIdempotentSupport implements IMqIdempotent {

    /**
     * Redis锁的前缀，用于构建消息锁的key
     * 默认值: "mq:idempotent:"
     */
    @Value("${mq.idempotent.lock-prefix:mq:idempotent:}")
    protected String lockPrefix;

    /**
     * Redis模板，用于操作Redis分布式锁
     */
    @Autowired
    protected RedisTemplate redisTemplate;

    /**
     * Redis锁的过期时间(秒)
     * 默认值: 50秒
     */
    @Value("${mq.idempotent.expire-time:5}")
    protected long expireTime;

    /**
     * MyBatis Plus持久化适配器工厂
     * 用于获取消息处理记录的持久化适配器
     */
    @Autowired
    protected MybatisPlusMqPersisterAdapterFactory mybatisPlusMqPersisterAdapterFactory;

    /**
     * 释放消息锁
     * 当消息处理完成后，删除Redis中的锁标识
     *
     * @param consumerGroup 消费者组名称
     * @param mqMsg         消息对象
     * @param <T>           消息内容类型
     */
    @Override
    public <T> void unlockMessage(String consumerGroup, MqMsg<T> mqMsg) {
        redisTemplate.delete(this.lockPrefix + consumerGroup + ":" + mqMsg.getMsgId());
    }

    /**
     * 检查并锁定消息
     * 通过Redis分布式锁机制确保同一消息不会被并发处理
     *
     * @param consumerGroup 消费者组名称
     * @param mqMsg         消息对象
     * @param <T>           消息内容类型
     * @return 消息幂等状态枚举值
     */
    @Override
    public <T> MqIdempotentStatus checkAndLockMessage(String consumerGroup, MqMsg<T> mqMsg) {
        // 构建Redis锁key并尝试获取锁
        String lockKey = this.lockPrefix + consumerGroup + ":" + mqMsg.getMsgId();
        Boolean lockResult = redisTemplate.opsForValue().setIfAbsent(lockKey, 1, expireTime, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(lockResult)) {
            // 获取锁成功，处理消息幂等记录
            return processMessageIdempotentRecord(consumerGroup, mqMsg);
        } else {
            // 获取锁失败，消息不可消费
            return MqIdempotentStatus.NOT_CONSUMABLE;
        }
    }

    /**
     * 处理消息幂等记录
     * 委托给持久化适配器处理具体的消息幂等性逻辑
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息内容类型
     * @return 消息幂等状态
     */
    protected <T> MqIdempotentStatus processMessageIdempotentRecord(String consumerGroup, MqMsg<T> mqMsg) {
        return mybatisPlusMqPersisterAdapterFactory.getAdapter().processMessageIdempotentRecord(consumerGroup, mqMsg);
    }
}