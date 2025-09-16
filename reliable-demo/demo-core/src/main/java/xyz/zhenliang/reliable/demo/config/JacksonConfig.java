package xyz.zhenliang.reliable.demo.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson配置类，用于自定义JSON序列化和反序列化行为
 * 主要处理Java 8时间类型(LocalDateTime、Instant)的格式化
 */
@Configuration
public class JacksonConfig {
    /**
     * 日期时间格式，默认为"yyyy-MM-dd HH:mm:ss"
     */
    @Value("${spring.jackson.date-format}")
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时区设置，默认为"Asia/Shanghai"
     */
    @Value("${spring.jackson.time-zone}")
    private String timeZone = "Asia/Shanghai";

    /**
     * 自定义Jackson ObjectMapper的配置器
     * 用于配置Java 8时间类型的序列化和反序列化规则
     *
     * @return Jackson2ObjectMapperBuilderCustomizer实例
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {

        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.dateFormat)
                .withZone(java.time.ZoneId.of(this.timeZone));

        return builder -> {
            // 创建Java 8时间模块
            JavaTimeModule module = new JavaTimeModule();

            // 配置LocalDateTime序列化格式
            module.addSerializer(
                    java.time.LocalDateTime.class,
                    new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(
                            DateTimeFormatter.ofPattern(this.dateFormat)
                    )
            );
            // 配置LocalDateTime反序列化格式
            module.addDeserializer(LocalDateTime.class,
                    new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateFormat)));

            // 配置Instant序列化格式（带时区转换）
            module.addSerializer(
                    Instant.class,
                    new JsonSerializer<Instant>() {


                        @Override
                        public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                            String formatted = dateTimeFormatter.format(value);
                            gen.writeString(formatted);
                        }
                    }
            );
            // 配置Instant反序列化格式（带时区转换）
            module.addDeserializer(Instant.class, new JsonDeserializer<Instant>() {
                @Override
                public Instant deserialize(JsonParser p, DeserializationContext ctx)
                        throws IOException {
                    return Instant.from(dateTimeFormatter.parse(p.getText()));
                }
            });

            // 注册模块
            builder.modules(module);

            // 设置时区
            builder.timeZone(java.util.TimeZone.getTimeZone(this.timeZone));
        };
    }
}