package xyz.zhenliang.reliable.demo.service;

import xyz.zhenliang.reliable.demo.dto.OrderDTO;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;

/**
 * 演示服务接口
 * 提供消息发送和重发功能
 */
public interface IDemoService {
    /**
     * 发送订单消息
     *
     * @param orderDTO 订单数据传输对象
     * @return 包装后的消息对象，包含消息ID等信息
     */
    MqMsg<OrderDTO> send(OrderDTO orderDTO);

    /**
     * 重新发送消息
     *
     * @param msgId 需要重发的消息ID
     */
    void resendMsg(String msgId);
}