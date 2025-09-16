package xyz.zhenliang.reliable.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.zhenliang.reliable.demo.constant.DemoConstant;
import xyz.zhenliang.reliable.demo.dto.OrderDTO;
import xyz.zhenliang.reliable.demo.service.IDemoService;
import xyz.zhenliang.reliable.mq.core.IMqSender;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;

/**
 * 消息发送服务类
 * 提供消息发送和重发功能
 */
@Service
public class DemoService implements IDemoService {
    @Autowired
    private IMqSender mqSender;

    /**
     * 发送订单消息到RabbitMQ
     *
     * @param orderDTO 订单数据传输对象
     * @return RabbitMQ消息对象
     */
    @Override
    public MqMsg<OrderDTO> send(OrderDTO orderDTO) {
        return mqSender.sendMsg(DemoConstant.ORDER_TOPIC, DemoConstant.ORDER_TAG,
                orderDTO, orderDTO.getOrderId(), DemoConstant.CONFIRM_TOPIC, DemoConstant.ORDER_TAG);
    }

    /**
     * 根据消息ID重新发送消息
     *
     * @param msgId 消息唯一标识
     */
    @Override
    public void resendMsg(String msgId) {
        mqSender.resendMsg(msgId);
    }
}