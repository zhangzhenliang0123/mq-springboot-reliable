package xyz.zhenliang.reliable.mq.mybatisplus.service.impl;

import xyz.zhenliang.reliable.mq.mybatisplus.entity.MqMessage;
import xyz.zhenliang.reliable.mq.mybatisplus.mapper.MqMessageMapper;
import xyz.zhenliang.reliable.mq.mybatisplus.service.IMqMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * mq消息表 服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-09-14
 */
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements IMqMessageService {

}
