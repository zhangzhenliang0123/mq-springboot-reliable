package xyz.zhenliang.reliable.mq.mybatisplus.service.impl;

import xyz.zhenliang.reliable.mq.mybatisplus.entity.MqProducerConsumeConfirm;
import xyz.zhenliang.reliable.mq.mybatisplus.mapper.MqProducerConsumeConfirmMapper;
import xyz.zhenliang.reliable.mq.mybatisplus.service.IMqProducerConsumeConfirmService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * MQ生产者消费确认表 服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-09-13
 */
@Service
public class MqProducerConsumeConfirmServiceImpl extends ServiceImpl<MqProducerConsumeConfirmMapper, MqProducerConsumeConfirm> implements IMqProducerConsumeConfirmService {

}
