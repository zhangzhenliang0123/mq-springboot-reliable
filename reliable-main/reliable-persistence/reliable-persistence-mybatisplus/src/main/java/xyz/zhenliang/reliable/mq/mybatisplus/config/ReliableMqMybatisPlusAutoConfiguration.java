package xyz.zhenliang.reliable.mq.mybatisplus.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("xyz.zhenliang.reliable.mq.mybatisplus.mapper")
public class ReliableMqMybatisPlusAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ReliableMqMybatisPlusAutoConfiguration.class);

    public ReliableMqMybatisPlusAutoConfiguration() {
        log.info("mq-springboot-reliable-mybatisplus initialized");
    }
}
