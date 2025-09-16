package xyz.zhenliang.reliable.mq.rabbitmq;

import java.nio.charset.StandardCharsets;

public class RabbitmqUtils {
    /**
     * 将字节数组转为字符串
     *
     * @param array
     * @return
     */
    public static String toString(byte[] array) {
        return new String(array, StandardCharsets.UTF_8);
    }
}
