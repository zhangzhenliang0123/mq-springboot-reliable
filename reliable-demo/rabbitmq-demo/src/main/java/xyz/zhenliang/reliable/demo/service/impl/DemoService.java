package xyz.zhenliang.reliable.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.zhenliang.reliable.demo.config.DemoConfig;
import xyz.zhenliang.reliable.demo.dto.OrderDTO;
import xyz.zhenliang.reliable.demo.service.IDemoService;
import xyz.zhenliang.reliable.mq.core.IMqSender;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;

/**
 * 消息发送服务实现类
 * 提供订单消息的发送和重发功能，确保消息可靠传输
 */
@Service
public class DemoService implements IDemoService {
    @Autowired
    private IMqSender mqSender;

    /**
     * 发送订单消息到RabbitMQ
     * 通过指定的交换机和路由键将订单数据发送到消息队列，并返回封装后的消息对象
     *
     * @param orderDTO 订单数据传输对象，包含订单相关信息
     * @return MqMsg<OrderDTO> RabbitMQ消息对象，包含发送的消息内容和元数据
     */
    @Override
    public MqMsg<OrderDTO> send(OrderDTO orderDTO) {
        return mqSender.sendMsg(DemoConfig.ORDER_EXCHANGE_NAME, DemoConfig.ORDER_ROUTING_KEY,
                orderDTO, orderDTO.getOrderId(), DemoConfig.CONFIRM_EXCHANGE_NAME, DemoConfig.CONFIRM_ROUTING_KEY);
    }

    /**
     * 根据消息ID重新发送消息
     * 当消息发送失败或需要重新处理时，根据消息唯一标识重新发送该消息
     *
     * @param msgId 消息唯一标识，用于定位需要重发的消息
     */
    @Override
    public void resendMsg(String msgId) {
        mqSender.resendMsg(msgId);
    }
}