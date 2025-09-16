package xyz.zhenliang.reliable.demo.consumer.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.zhenliang.reliable.demo.dto.OrderDTO;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqJsonUtils;

@Component
public class OrderHandler {
    private static final Logger log = LoggerFactory.getLogger(OrderHandler.class);

    public void handleOrder(OrderDTO orderDTO) {
        log.info("Order processed successfully: " + ReliableMqJsonUtils.toJson(orderDTO));
    }
}