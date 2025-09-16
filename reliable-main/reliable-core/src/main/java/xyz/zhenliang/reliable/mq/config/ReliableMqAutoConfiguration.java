package xyz.zhenliang.reliable.mq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@AutoConfiguration
@EnableAsync
@ComponentScan(basePackages = "xyz.zhenliang.reliable.mq")
public class ReliableMqAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ReliableMqAutoConfiguration.class);

    public ReliableMqAutoConfiguration() {
        log.info("mq-springboot-reliable initialized");
    }
}