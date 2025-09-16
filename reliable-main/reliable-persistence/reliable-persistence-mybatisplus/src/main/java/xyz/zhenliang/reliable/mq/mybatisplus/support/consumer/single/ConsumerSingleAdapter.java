package xyz.zhenliang.reliable.mq.mybatisplus.support.consumer.single;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.zhenliang.reliable.mq.config.ReliableMqProperties;
import xyz.zhenliang.reliable.mq.core.constant.ConfirmStatus;
import xyz.zhenliang.reliable.mq.core.constant.ConsumeStatus;
import xyz.zhenliang.reliable.mq.core.constant.MqIdempotentStatus;
import xyz.zhenliang.reliable.mq.core.dto.MqConsumeConfirmMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsgEntity;
import xyz.zhenliang.reliable.mq.mybatisplus.constant.MqSavedby;
import xyz.zhenliang.reliable.mq.mybatisplus.entity.*;
import xyz.zhenliang.reliable.mq.mybatisplus.service.impl.MqMessageServiceImpl;
import xyz.zhenliang.reliable.mq.mybatisplus.support.consumer.IMybatisPlusMqPersisterAdapter;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqJsonUtils;

import java.time.LocalDateTime;

/**
 * 消费者单机模式适配器
 * 提供消息的持久化操作，包括消息保存、查询、更新等
 */
@Component
public class ConsumerSingleAdapter implements IMybatisPlusMqPersisterAdapter {
    private static final Logger log = LoggerFactory.getLogger(ConsumerSingleAdapter.class);
    @Autowired
    protected MqMessageServiceImpl mqMessageService;
    @Autowired
    protected ReliableMqProperties reliableMqProperties;


    /**
     * 保存消息到数据库
     *
     * @param mqMsg 消息对象
     * @param <T>   消息体类型
     */
    @Transactional
    @Override
    public <T> void saveMsg(MqMsg<T> mqMsg) {
        // 保存消息
        MqMessage mqMessage = new MqMessage();
        mqMessage.setId(mqMsg.getMsgId());
        mqMessage.setBusinessId(mqMsg.getBusinessId());
        mqMessage.setTopic(mqMsg.getTopic());
        mqMessage.setTag(mqMsg.getTag());
        mqMessage.setMsgBody(ReliableMqJsonUtils.toJson(mqMsg));
        mqMessage.setSendCount(1);
        mqMessage.setSendLastTime(LocalDateTime.now());
        mqMessage.setConsumeStatus(ConsumeStatus.NOT_CONSUMED.getCode());
        mqMessage.setConfirmStatus(ConfirmStatus.UNCONFIRMED.getCode());
        mqMessage.setSavedBy(MqSavedby.SENDER.getCode());
        mqMessage.setCreatedAt(LocalDateTime.now());
        mqMessage.setUpdatedAt(LocalDateTime.now());
        mqMessageService.save(mqMessage);
    }

    /**
     * 根据消息ID获取消息实体
     *
     * @param msgId 消息ID
     * @return 消息实体对象
     */
    @Override
    public MqMsgEntity getMsgById(String msgId) {
        MqMessage mqMessage = mqMessageService.getById(msgId);
        MqMsgEntity mqMsgEntity = null;
        if (mqMessage != null) {
            mqMsgEntity = new MqMsgEntity();
            mqMsgEntity.setMsgId(mqMessage.getId());
            mqMsgEntity.setBusinessId(mqMessage.getBusinessId());
            mqMsgEntity.setTopic(mqMessage.getTopic());
            mqMsgEntity.setTag(mqMessage.getTag());
            mqMsgEntity.setMsgBody(mqMessage.getMsgBody());
            mqMsgEntity.setSendCount(mqMessage.getSendCount());
            mqMsgEntity.setSendLastTime(mqMessage.getSendLastTime());
            mqMsgEntity.setCreatedAt(mqMessage.getCreatedAt());
        }
        return mqMsgEntity;
    }

    /**
     * 重新发送消息，增加发送次数
     *
     * @param msgId 消息ID
     */
    @Override
    public void resendMsg(String msgId) {
        LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MqMessage::getId, msgId)
                .setSql(" send_count = send_count + 1")
                .set(MqMessage::getSendLastTime, LocalDateTime.now())
                .set(MqMessage::getUpdatedAt, LocalDateTime.now())
        ;
        mqMessageService.update(updateWrapper);
    }

    /**
     * 标记消息消费成功
     * 使用REQUIRES_NEW传播级别确保在新事务中执行
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息体类型
     * @return 是否需要发送消息确认。否表示不需要发送消息确认（生产者与消费者同库本地确认），是表示需要发送消息确认
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public <T> boolean markConsumeSuccess(String consumerGroup, MqMsg<T> mqMsg) {
        LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MqMessage::getId, mqMsg.getMsgId())
                .set(MqMessage::getConsumerGroup, consumerGroup)
                .set(MqMessage::getConsumeStatus, ConsumeStatus.CONSUMED_SUCCESS.getCode())
                .set(MqMessage::getConsumeSuccessTime, LocalDateTime.now())
                .set(MqMessage::getUpdatedAt, LocalDateTime.now());
        mqMessageService.update(updateWrapper);
        if(!reliableMqProperties.getConsumer().getSameDatabaseConfirmation()){
            if(mqMessageService.count(new LambdaQueryWrapper<MqMessage>()
                    .eq(MqMessage::getId, mqMsg.getMsgId())
                    .eq(MqMessage::getSavedBy, MqSavedby.SENDER.getCode())
                )>0){
                // 同库直接确认，不通过消息确认
                this.confirmMsgConsumeSuccess(new MqConsumeConfirmMsg(mqMsg.getMsgId(), consumerGroup));
                return false;
            }
        }
        return true;
    }

    /**
     * 标记消息消费失败
     * 使用REQUIRES_NEW传播级别确保在新事务中执行
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息体类型
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public <T> void markConsumeFailed(String consumerGroup, MqMsg<T> mqMsg) {
        LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MqMessage::getId, mqMsg.getMsgId())
                .set(MqMessage::getConsumerGroup, consumerGroup)
                .set(MqMessage::getConsumeStatus, ConsumeStatus.CONSUMED_FAILURE.getCode())
                .set(MqMessage::getUpdatedAt, LocalDateTime.now());
        mqMessageService.update(updateWrapper);
    }

    /**
     * 确认消息消费成功
     * 只有未确认的消息才会被更新为已确认状态
     *
     * @param mqConsumeConfirmMsg 消息确认对象
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void confirmMsgConsumeSuccess(MqConsumeConfirmMsg mqConsumeConfirmMsg) {
        LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MqMessage::getId, mqConsumeConfirmMsg.getMsgId())
                .ne(MqMessage::getConfirmStatus, ConfirmStatus.CONFIRMED.getCode())
                .set(MqMessage::getConsumerGroup, mqConsumeConfirmMsg.getConsumerGroup())
                .set(MqMessage::getConfirmStatus, ConfirmStatus.CONFIRMED.getCode())
                .set(MqMessage::getConfirmTime, LocalDateTime.now())
                .set(MqMessage::getUpdatedAt, LocalDateTime.now());
        mqMessageService.update(updateWrapper);
    }

    /**
     * 处理消息幂等性记录
     * 防止重复消费同一消息
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息体类型
     * @return 消息幂等状态
     */
    @Override
    public <T> MqIdempotentStatus processMessageIdempotentRecord(String consumerGroup, MqMsg<T> mqMsg) {
        // 查询消息幂等记录
        MqMessage mqMessage = mqMessageService.getById(mqMsg.getMsgId());

        if (mqMessage == null) {
            // 消息记录不存在，创建新记录
            mqMessage = new MqMessage();
            mqMessage.setId(mqMsg.getMsgId());
            mqMessage.setBusinessId(mqMsg.getBusinessId());
            mqMessage.setTopic(mqMsg.getTopic());
            mqMessage.setTag(mqMsg.getTag());
            mqMessage.setMsgBody(ReliableMqJsonUtils.toJson(mqMsg));
            mqMessage.setSendCount(0);
            mqMessage.setConsumerGroup(consumerGroup);
            mqMessage.setConsumeStatus(ConsumeStatus.CONSUMING.getCode());
            mqMessage.setConsumeStartTime(LocalDateTime.now());
            mqMessage.setSavedBy(MqSavedby.CONSUMER.getCode());
            mqMessage.setCreatedAt(LocalDateTime.now());
            mqMessage.setUpdatedAt(LocalDateTime.now());
            mqMessageService.save(mqMessage);
        } else {
            // 消息记录已存在
            if (mqMessage.getConsumeStatus() == ConsumeStatus.CONSUMED_SUCCESS.getCode()) {
                // 消息已成功消费，直接返回
                return MqIdempotentStatus.CONSUMED;
            } else {
                // 更新消费状态为消费中
                LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(MqMessage::getId, mqMsg.getMsgId())
                        .ne(MqMessage::getConsumeStatus, ConsumeStatus.CONSUMED_SUCCESS.getCode())
                        .set(MqMessage::getConsumeStatus, ConsumeStatus.CONSUMING.getCode())
                        .set(MqMessage::getConsumeStartTime, LocalDateTime.now())
                        .set(MqMessage::getUpdatedAt, LocalDateTime.now());
                mqMessageService.update(updateWrapper);
            }
        }
        return MqIdempotentStatus.CONSUMABLE;
    }
}