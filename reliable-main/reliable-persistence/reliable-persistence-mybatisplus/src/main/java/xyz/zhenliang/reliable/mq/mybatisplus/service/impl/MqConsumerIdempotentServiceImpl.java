package xyz.zhenliang.reliable.mq.mybatisplus.service.impl;

import xyz.zhenliang.reliable.mq.mybatisplus.entity.MqConsumerIdempotent;
import xyz.zhenliang.reliable.mq.mybatisplus.mapper.MqConsumerIdempotentMapper;
import xyz.zhenliang.reliable.mq.mybatisplus.service.IMqConsumerIdempotentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * MQ消费者幂等表 服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-09-13
 */
@Service
public class MqConsumerIdempotentServiceImpl extends ServiceImpl<MqConsumerIdempotentMapper, MqConsumerIdempotent> implements IMqConsumerIdempotentService {

}
