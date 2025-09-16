package xyz.zhenliang.reliable.mq.core.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import xyz.zhenliang.reliable.mq.commons.exception.TechException;
import xyz.zhenliang.reliable.mq.core.IMqSender;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;
import xyz.zhenliang.reliable.mq.core.dto.MqMsgEntity;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqJsonUtils;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqUtils;

/**
 * MQ消息发送支持类
 * 提供可靠的消息发送机制，确保消息发送与业务操作的一致性
 */
@Component
public class MqSenderSupport implements IMqSender {
    private static final Logger log = LoggerFactory.getLogger(MqSenderSupport.class);
    @Value("${mq.id-prefix:}")
    private String idPrefix;

    @Autowired
    private IMqPersister mqPersister;

    @Autowired
    private IMqProducer mqProducer;

    /**
     * 发送MQ消息
     * 采用事务机制确保消息持久化与业务操作的一致性，在事务提交后再发送消息
     *
     * @param topic        消息主题
     * @param tag          消息标签
     * @param data         消息数据
     * @param businessId   业务ID
     * @param confirmTopic 确认消息主题
     * @param confirmTag   确认消息标签
     * @param <T>          消息数据类型
     * @return 消息对象
     */
    @Transactional
    @Override
    public <T> MqMsg<T> sendMsg(String topic, String tag, T data, String businessId, String confirmTopic, String confirmTag) {
        String msgId = idPrefix + ReliableMqUtils.generate32UUID();
        final MqMsg<T> mqMsg = new MqMsg<>();
        mqMsg.setMsgId(msgId);
        mqMsg.setBusinessId(businessId);
        mqMsg.setTopic(topic);
        mqMsg.setTag(tag == null ? "" : tag);
        mqMsg.setData(data);
        mqMsg.setConfirmTopic(confirmTopic);
        mqMsg.setConfirmTag(confirmTag);

        // 保存消息到数据库，确保消息持久化
        mqPersister.saveMsg(mqMsg);

        // 注册事务同步回调，在事务提交后发送消息
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 在事务提交后发送消息，确保数据库操作成功后再发送消息
                mqProducer.sendMsg(mqMsg.getTopic(), mqMsg.getTag(), mqMsg.getMsgId(), mqMsg.getBusinessId(), ReliableMqJsonUtils.toJson(mqMsg));
            }
        });
        return mqMsg;
    }

    /**
     * 根据消息ID重新发送消息
     * 使用新的事务确保重发操作的独立性
     *
     * @param msgId 消息ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void resendMsg(String msgId) {
        final MqMsgEntity mqMsg = mqPersister.getMsgById(msgId);
        if (mqMsg == null) throw new TechException("Message not found. msgId: " + msgId);
        this.resendMsg(mqMsg);
    }

    /**
     * 重新发送消息实体
     * 更新消息状态并注册事务同步回调，在事务提交后发送消息
     *
     * @param msgEntity 消息实体
     */
    @Transactional
    @Override
    public void resendMsg(MqMsgEntity msgEntity) {

        final MqMsgEntity mqMsg = msgEntity;
        // 更新消息重发状态
        mqPersister.resendMsg(mqMsg.getMsgId());
        // 注册事务同步回调，在事务提交后发送消息
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 在事务提交后发送消息，确保数据库操作成功后再发送消息
                mqProducer.sendMsg(mqMsg.getTopic(), mqMsg.getTag(), mqMsg.getMsgId(), mqMsg.getBusinessId(), mqMsg.getMsgBody());
            }
        });
    }
}