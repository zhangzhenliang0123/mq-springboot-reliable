package xyz.zhenliang.reliable.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DemoKafkaApplication {
    private static Logger log = LoggerFactory.getLogger(DemoKafkaApplication.class);

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext ctx = SpringApplication.run(DemoKafkaApplication.class, args);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
