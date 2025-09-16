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
 *
 * @author zhenliang
 * @since 1.0.0
 */
@Service
public class DemoService implements IDemoService {
    @Autowired
    private IMqSender mqSender;

    /**
     * 发送订单消息到消息队列
     *
     * @param orderDTO 订单数据传输对象，包含订单相关信息
     * @return MqMsg<OrderDTO> 消息队列消息对象，包含发送的消息内容和元数据
     */
    @Override
    public MqMsg<OrderDTO> send(OrderDTO orderDTO) {
        // 调用消息发送器发送消息
        // 参数说明：
        //   1. DemoConstant.ORDER_TOPIC - 消息主题
        //   2. DemoConstant.ORDER_TAG - 消息标签
        //   3. orderDTO - 消息内容
        //   4. orderDTO.getOrderId() - 消息唯一标识
        //   5. DemoConstant.CONFIRM_TOPIC - 确认消息主题
        //   6. DemoConstant.ORDER_TAG - 确认消息标签
        return mqSender.sendMsg(DemoConstant.ORDER_TOPIC, DemoConstant.ORDER_TAG,
                orderDTO, orderDTO.getOrderId(), DemoConstant.CONFIRM_TOPIC, DemoConstant.ORDER_TAG);
    }

    /**
     * 根据消息ID重新发送消息
     * 用于处理消息发送失败的情况，支持消息重发机制
     *
     * @param msgId 消息唯一标识，用于定位需要重发的消息
     */
    @Override
    public void resendMsg(String msgId) {
        // 调用消息发送器的重发接口
        mqSender.resendMsg(msgId);
    }
}