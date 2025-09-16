package xyz.zhenliang.reliable.mq.mybatisplus.service.impl;

import xyz.zhenliang.reliable.mq.mybatisplus.entity.MqProducerMessage;
import xyz.zhenliang.reliable.mq.mybatisplus.mapper.MqProducerMessageMapper;
import xyz.zhenliang.reliable.mq.mybatisplus.service.IMqProducerMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * MQ生产者消息表 服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-09-13
 */
@Service
public class MqProducerMessageServiceImpl extends ServiceImpl<MqProducerMessageMapper, MqProducerMessage> implements IMqProducerMessageService {

}
