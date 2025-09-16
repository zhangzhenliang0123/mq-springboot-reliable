package xyz.zhenliang.reliable.mq.mybatisplus.support.consumer.multiple;

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
import xyz.zhenliang.reliable.mq.mybatisplus.constant.EnableStatus;
import xyz.zhenliang.reliable.mq.mybatisplus.entity.MqConsumerIdempotent;
import xyz.zhenliang.reliable.mq.mybatisplus.entity.MqProducerConsumeConfig;
import xyz.zhenliang.reliable.mq.mybatisplus.entity.MqProducerConsumeConfirm;
import xyz.zhenliang.reliable.mq.mybatisplus.entity.MqProducerMessage;
import xyz.zhenliang.reliable.mq.mybatisplus.service.IMqConsumerIdempotentService;
import xyz.zhenliang.reliable.mq.mybatisplus.service.IMqProducerConsumeConfigService;
import xyz.zhenliang.reliable.mq.mybatisplus.service.IMqProducerConsumeConfirmService;
import xyz.zhenliang.reliable.mq.mybatisplus.service.IMqProducerMessageService;
import xyz.zhenliang.reliable.mq.mybatisplus.support.consumer.IMybatisPlusMqPersisterAdapter;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqJsonUtils;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消费者多数据源适配器实现类
 * 提供消息的持久化操作，包括消息保存、查询、重发、消费状态标记等
 */
@Component
public class ConsumerMultipleAdapter implements IMybatisPlusMqPersisterAdapter {
    private static final Logger log = LoggerFactory.getLogger(ConsumerMultipleAdapter.class);

    @Autowired
    protected IMqProducerMessageService mqProducerMessageService;
    @Autowired
    protected IMqProducerConsumeConfigService mqProducerConsumeConfigService;
    @Autowired
    protected IMqProducerConsumeConfirmService mqProducerConsumeConfirmService;
    @Autowired
    protected IMqConsumerIdempotentService mqConsumerIdempotentService;
    @Autowired
    private ReliableMqProperties reliableMqProperties;

    /**
     * 保存消息到数据库
     * 1. 保存消息主体信息到mq_producer_message表
     * 2. 根据消息的topic和tag查询对应的消费者配置
     * 3. 为每个消费者配置创建消费确认记录
     *
     * @param mqMsg 消息对象
     * @param <T>   消息体类型
     */
    @Transactional
    @Override
    public <T> void saveMsg(MqMsg<T> mqMsg) {
        // 保存消息
        MqProducerMessage mqProducerMessage = new MqProducerMessage();
        mqProducerMessage.setId(mqMsg.getMsgId());
        mqProducerMessage.setBusinessId(mqMsg.getBusinessId());
        mqProducerMessage.setTopic(mqMsg.getTopic());
        mqProducerMessage.setTag(mqMsg.getTag());
        mqProducerMessage.setMsgBody(ReliableMqJsonUtils.toJson(mqMsg));
        mqProducerMessage.setSendCount(1);
        mqProducerMessage.setSendLastTime(LocalDateTime.now());
        mqProducerMessage.setConfirmStatus(ConfirmStatus.UNCONFIRMED.getCode());
        mqProducerMessage.setCreatedAt(LocalDateTime.now());
        mqProducerMessageService.save(mqProducerMessage);

        List<MqProducerConsumeConfig> mqProducerConsumeConfigs = mqProducerConsumeConfigService.list(
                new LambdaQueryWrapper<MqProducerConsumeConfig>()
                        .eq(MqProducerConsumeConfig::getTopic, mqMsg.getTopic())
                        .eq(MqProducerConsumeConfig::getTag, mqMsg.getTag())
                        .eq(MqProducerConsumeConfig::getIsEnabled, EnableStatus.ENABLE.getCode())
        );
        // 保存消费确认
        mqProducerConsumeConfigs.forEach(mqProducerConsumeConfig -> {
            MqProducerConsumeConfirm mqProducerConsumeConfirm = new MqProducerConsumeConfirm();
            mqProducerConsumeConfirm.setId(ReliableMqUtils.generate32UUID());
            mqProducerConsumeConfirm.setMsgId(mqMsg.getMsgId());
            mqProducerConsumeConfirm.setConsumerGroup(mqProducerConsumeConfig.getConsumerGroup());
            mqProducerConsumeConfirm.setConfirmStatus(ConfirmStatus.UNCONFIRMED.getCode());
            mqProducerConsumeConfirm.setCreatedAt(LocalDateTime.now());
            mqProducerConsumeConfirm.setUpdatedAt(LocalDateTime.now());
            mqProducerConsumeConfirmService.save(mqProducerConsumeConfirm);
        });

    }

    /**
     * 根据消息ID获取消息实体
     *
     * @param msgId 消息ID
     * @return 消息实体对象，如果不存在返回null
     */
    @Override
    public MqMsgEntity getMsgById(String msgId) {
        MqProducerMessage mqProducerMessage = mqProducerMessageService.getById(msgId);
        MqMsgEntity mqMsgEntity = null;
        if (mqProducerMessage != null) {
            mqMsgEntity = new MqMsgEntity();
            mqMsgEntity.setMsgId(mqProducerMessage.getId());
            mqMsgEntity.setBusinessId(mqProducerMessage.getBusinessId());
            mqMsgEntity.setTopic(mqProducerMessage.getTopic());
            mqMsgEntity.setTag(mqProducerMessage.getTag());
            mqMsgEntity.setMsgBody(mqProducerMessage.getMsgBody());
            mqMsgEntity.setSendCount(mqProducerMessage.getSendCount());
            mqMsgEntity.setSendLastTime(mqProducerMessage.getSendLastTime());
            mqMsgEntity.setCreatedAt(mqProducerMessage.getCreatedAt());
        }
        return mqMsgEntity;
    }

    /**
     * 重新发送消息，增加发送次数并更新最后发送时间
     *
     * @param msgId 消息ID
     */
    @Override
    public void resendMsg(String msgId) {
        LambdaUpdateWrapper<MqProducerMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MqProducerMessage::getId, msgId)
                .setSql(" send_count = send_count + 1")
                .set(MqProducerMessage::getSendLastTime, LocalDateTime.now());
        mqProducerMessageService.update(updateWrapper);
    }

    /**
     * 标记消息消费成功
     * 更新消费者幂等表中的消费状态为成功，并记录成功时间
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息体类型
     * @return 是否需要发送消息确认。否表示不需要发送消息确认（生产者与消费者同库本地确认），是表示需要发送消息确认
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public <T> boolean markConsumeSuccess(String consumerGroup, MqMsg<T> mqMsg) {

        LambdaUpdateWrapper<MqConsumerIdempotent> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MqConsumerIdempotent::getMsgId, mqMsg.getMsgId())
                .eq(MqConsumerIdempotent::getConsumerGroup, consumerGroup)
                .set(MqConsumerIdempotent::getConsumeStatus, ConsumeStatus.CONSUMED_SUCCESS.getCode())
                .set(MqConsumerIdempotent::getConsumeSuccessTime, LocalDateTime.now())
                .set(MqConsumerIdempotent::getUpdatedAt, LocalDateTime.now());
        mqConsumerIdempotentService.update(updateWrapper);

        if(!reliableMqProperties.getConsumer().getSameDatabaseConfirmation()){
            // 判断消息是否在本库
            if(mqProducerMessageService.count(new LambdaQueryWrapper<MqProducerMessage>()
                    .eq(MqProducerMessage::getId, mqMsg.getMsgId())
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
     * 更新消费者幂等表中的消费状态为失败
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息体类型
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public <T> void markConsumeFailed(String consumerGroup, MqMsg<T> mqMsg) {
        LambdaUpdateWrapper<MqConsumerIdempotent> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MqConsumerIdempotent::getMsgId, mqMsg.getMsgId())
                .eq(MqConsumerIdempotent::getConsumerGroup, consumerGroup)
                .set(MqConsumerIdempotent::getConsumeStatus, ConsumeStatus.CONSUMED_FAILURE.getCode())
                .set(MqConsumerIdempotent::getUpdatedAt, LocalDateTime.now());
        mqConsumerIdempotentService.update(updateWrapper);
    }

    /**
     * 确认消息消费成功
     * 更新生产者消费确认表中的确认状态为已确认，并记录确认时间
     *
     * @param mqConsumeConfirmMsg 消费确认消息对象
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void confirmMsgConsumeSuccess(MqConsumeConfirmMsg mqConsumeConfirmMsg) {
        LambdaUpdateWrapper<MqProducerConsumeConfirm> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MqProducerConsumeConfirm::getMsgId, mqConsumeConfirmMsg.getMsgId())
                .eq(MqProducerConsumeConfirm::getConsumerGroup, mqConsumeConfirmMsg.getConsumerGroup())
                .ne(MqProducerConsumeConfirm::getConfirmStatus, ConfirmStatus.CONFIRMED.getCode())
                .set(MqProducerConsumeConfirm::getConfirmStatus, ConfirmStatus.CONFIRMED.getCode())
                .set(MqProducerConsumeConfirm::getConfirmTime, LocalDateTime.now())
                .set(MqProducerConsumeConfirm::getUpdatedAt, LocalDateTime.now());
        mqProducerConsumeConfirmService.update(updateWrapper);
        if(mqProducerConsumeConfirmService.count(new LambdaQueryWrapper<MqProducerConsumeConfirm>()
                .eq(MqProducerConsumeConfirm::getMsgId, mqConsumeConfirmMsg.getMsgId())
                .ne(MqProducerConsumeConfirm::getConfirmStatus, ConfirmStatus.CONFIRMED.getCode())
            )==0){
            // 没有未确认消息，确认消息完成
            LambdaUpdateWrapper<MqProducerMessage> updateMsgWrapper = new LambdaUpdateWrapper<>();
            updateMsgWrapper.eq(MqProducerMessage::getId, mqConsumeConfirmMsg.getMsgId())
                    .ne(MqProducerMessage::getConfirmStatus, ConfirmStatus.CONFIRMED.getCode())
                    .set(MqProducerMessage::getConfirmStatus, ConfirmStatus.CONFIRMED.getCode())
                    .set(MqProducerMessage::getConfirmTime, LocalDateTime.now());
            mqProducerMessageService.update(updateMsgWrapper);
        }
    }

    /**
     * 处理消息幂等性记录
     * 1. 查询消息幂等记录是否存在
     * 2. 如果不存在则创建新记录，状态为消费中
     * 3. 如果存在且已消费成功，则返回已消费状态
     * 4. 如果存在但未消费成功，则更新状态为消费中
     *
     * @param consumerGroup 消费者组
     * @param mqMsg         消息对象
     * @param <T>           消息体类型
     * @return 消息幂等状态
     */
    @Override
    public <T> MqIdempotentStatus processMessageIdempotentRecord(String consumerGroup, MqMsg<T> mqMsg) {
        // 查询消息幂等记录
        MqConsumerIdempotent mqConsumerIdempotent = mqConsumerIdempotentService.getOne(new LambdaQueryWrapper<MqConsumerIdempotent>()
                .eq(MqConsumerIdempotent::getMsgId, mqMsg.getMsgId())
                .eq(MqConsumerIdempotent::getConsumerGroup, consumerGroup)
        );

        if (mqConsumerIdempotent == null) {
            // 消息记录不存在，创建新记录
            mqConsumerIdempotent = new MqConsumerIdempotent();
            mqConsumerIdempotent.setId(ReliableMqUtils.generate32UUID());
            mqConsumerIdempotent.setMsgId(mqMsg.getMsgId());
            mqConsumerIdempotent.setConsumerGroup(consumerGroup);
            mqConsumerIdempotent.setConsumeStatus(ConsumeStatus.CONSUMING.getCode());
            mqConsumerIdempotent.setConsumeStartTime(LocalDateTime.now());
            mqConsumerIdempotent.setCreatedAt(LocalDateTime.now());
            mqConsumerIdempotent.setUpdatedAt(LocalDateTime.now());
            mqConsumerIdempotentService.save(mqConsumerIdempotent);
        } else {
            // 消息记录已存在
            if (mqConsumerIdempotent.getConsumeStatus() == ConsumeStatus.CONSUMED_SUCCESS.getCode()) {
                // 消息已成功消费，直接返回
                return MqIdempotentStatus.CONSUMED;
            } else {
                // 更新消费状态为消费中
                LambdaUpdateWrapper<MqConsumerIdempotent> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(MqConsumerIdempotent::getMsgId, mqMsg.getMsgId())
                        .eq(MqConsumerIdempotent::getConsumerGroup, consumerGroup)
                        .ne(MqConsumerIdempotent::getConsumeStatus, ConsumeStatus.CONSUMED_SUCCESS.getCode())
                        .set(MqConsumerIdempotent::getConsumeStatus, ConsumeStatus.CONSUMING.getCode())
                        .set(MqConsumerIdempotent::getConsumeStartTime, LocalDateTime.now())
                        .set(MqConsumerIdempotent::getUpdatedAt, LocalDateTime.now());
                mqConsumerIdempotentService.update(updateWrapper);
            }
        }
        return MqIdempotentStatus.CONSUMABLE;
    }
}