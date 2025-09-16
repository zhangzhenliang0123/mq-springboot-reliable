package xyz.zhenliang.reliable.demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.zhenliang.reliable.demo.dto.OrderDTO;
import xyz.zhenliang.reliable.demo.dto.Result;
import xyz.zhenliang.reliable.demo.service.IDemoService;
import xyz.zhenliang.reliable.mq.core.dto.MqMsg;
import xyz.zhenliang.reliable.mq.commons.utils.ReliableMqJsonUtils;

/**
 * 消息Demo控制器
 * 提供消息发送和重发接口
 */
@RestController
@Tag(name = "mq示例", description = "消息相关操作接口")
@RequestMapping("/demo")
public class DemoController {
    private static final Logger log = LoggerFactory.getLogger(DemoController.class);
    @Autowired
    private IDemoService demoService;

    /**
     * 发送消息接口
     *
     * @param orderDTO 订单数据传输对象
     * @return 发送结果
     */
    @Operation(summary = "发送消息", description = "发送消息接口")
    @PostMapping("/send")
    public Result<MqMsg<OrderDTO>> send(@RequestBody OrderDTO orderDTO) {
        return Result.ok(demoService.send(orderDTO));
    }

    /**
     * 重发消息接口
     *
     * @param msgId 消息ID
     * @return 重发结果
     */
    @Operation(summary = "重发消息", description = "根据消息id重发消息")
    @GetMapping("/resendMsg")
    public Result<String> resendMsg(@RequestParam String msgId) {
        demoService.resendMsg(msgId);
        return Result.ok();
    }
}