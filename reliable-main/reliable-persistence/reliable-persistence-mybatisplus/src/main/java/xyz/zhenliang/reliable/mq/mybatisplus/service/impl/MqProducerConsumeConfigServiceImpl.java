package xyz.zhenliang.reliable.mq.mybatisplus.service.impl;

import xyz.zhenliang.reliable.mq.mybatisplus.entity.MqProducerConsumeConfig;
import xyz.zhenliang.reliable.mq.mybatisplus.mapper.MqProducerConsumeConfigMapper;
import xyz.zhenliang.reliable.mq.mybatisplus.service.IMqProducerConsumeConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * MQ生产者消费配置表 服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-09-13
 */
@Service
public class MqProducerConsumeConfigServiceImpl extends ServiceImpl<MqProducerConsumeConfigMapper, MqProducerConsumeConfig> implements IMqProducerConsumeConfigService {

}
