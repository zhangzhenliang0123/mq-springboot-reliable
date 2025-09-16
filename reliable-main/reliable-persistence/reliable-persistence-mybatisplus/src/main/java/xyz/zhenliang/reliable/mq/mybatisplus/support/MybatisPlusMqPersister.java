package xyz.zhenliang.reliable.mq.mybatisplus.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.zhenliang.reliable.mq.core.dto.MqConsumeConfirmMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsgEntity;
import xyz.zhenliang.reliable.mq.core.support.IMqPersister;
import xyz.zhenliang.reliable.mq.mybatisplus.support.consumer.MybatisPlusMqPersisterAdapterFactory;

/**
 * 基于MybatisPlus的消息持久化实现类
 * 提供消息的存储、查询、重发以及消费状态管理等功能
 *
 * @author zhenliang
 */
@Component
public class MybatisPlusMqPersister implements IMqPersister {
    private static final Logger log = LoggerFactory.getLogger(MybatisPlusMqPersister.class);

    @Autowired
    protected MybatisPlusMqPersisterAdapterFactory mybatisPlusMqPersisterAdapterFactory;

    /**
     * 保存消息到数据库
     * 使用默认事务传播机制
     *
     * @param mqMsg 消息对象
     * @param <T>   消息内容类型
     */
    @Transactional
    @Override
    public <T> void saveMsg(MqMsg<T> mqMsg) {
        mybatisPlusMqPersisterAdapterFactory.getAdapter().saveMsg(mqMsg);
    }

    /**
     * 根据消息ID获取消息实体
     *
     * @param msgId 消息ID
     * @return 消息实体对象
     */
    @Override
    public MqMsgEntity getMsgById(String msgId) {
        return mybatisPlusMqPersisterAdapterFactory.getAdapter().getMsgById(msgId);
    }

    /**
     * 重新发送消息
     *
     * @param msgId 消息ID
     */
    @Override
    public void resendMsg(String msgId) {
        mybatisPlusMqPersisterAdapterFactory.getAdapter().resendMsg(msgId);
    }

    /**
     * 标记消息消费成功
     * 使用REQUIRES_NEW事务传播机制，确保消费状态独立提交
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息内容类型
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public <T> boolean markConsumeSuccess(String consumerGroup, MqMsg<T> mqMsg) {
        return mybatisPlusMqPersisterAdapterFactory.getAdapter().markConsumeSuccess(consumerGroup, mqMsg);
    }

    /**
     * 标记消息消费失败
     * 使用REQUIRES_NEW事务传播机制，确保消费状态独立提交
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息内容类型
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public <T> void markConsumeFailed(String consumerGroup, MqMsg<T> mqMsg) {
        mybatisPlusMqPersisterAdapterFactory.getAdapter().markConsumeFailed(consumerGroup, mqMsg);
    }

    /**
     * 确认消息消费成功
     * 使用REQUIRES_NEW事务传播机制，确保消费确认独立提交
     *
     * @param mqConsumeConfirmMsg 消费确认消息对象
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void confirmMsgConsumeSuccess(MqConsumeConfirmMsg mqConsumeConfirmMsg) {
        mybatisPlusMqPersisterAdapterFactory.getAdapter().confirmMsgConsumeSuccess(mqConsumeConfirmMsg);
    }
}