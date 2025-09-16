package xyz.zhenliang.reliable.mq.mybatisplus.support.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.zhenliang.reliable.mq.mybatisplus.constant.ConsumerType;
import xyz.zhenliang.reliable.mq.mybatisplus.support.consumer.multiple.ConsumerMultipleAdapter;
import xyz.zhenliang.reliable.mq.mybatisplus.support.consumer.single.ConsumerSingleAdapter;

/**
 * MybatisPlus消息持久化适配器工厂类
 * 用于根据配置创建不同类型的消息消费者适配器
 */
@Component
public class MybatisPlusMqPersisterAdapterFactory {

    /**
     * 单消费者适配器实例
     */
    @Autowired
    protected ConsumerSingleAdapter consumerSingleAdapter;

    /**
     * 多消费者适配器实例
     */
    @Autowired
    protected ConsumerMultipleAdapter consumerMultipleAdapter;

    /**
     * 消费者类型配置
     * 1: 单消费者模式（默认）
     * 2: 多消费者模式
     */
    @Value("${mq.consumer.type:1}")
    protected int consumerType;

    /**
     * 根据配置获取对应的消息持久化适配器
     *
     * @return 消息持久化适配器实例
     */
    public IMybatisPlusMqPersisterAdapter getAdapter() {
        // 根据消费者类型配置返回对应的适配器实例
        if (ConsumerType.MULTIPLE.getCode() == consumerType) {
            return consumerMultipleAdapter;
        } else {
            return consumerSingleAdapter;
        }
    }
}