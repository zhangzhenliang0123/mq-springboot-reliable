package xyz.zhenliang.reliable.mq.commons.utils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class ReliableMqUtils {

    /**
     * 生成32位UUID字符串（不含连字符）,高性能实现
     *
     * @return 32位的UUID字符串
     */
    public static String generate32UUID() {
        // return UUID.randomUUID().toString().replace("-", "");
        final UUID uuid = UUID.randomUUID();
        return toHexString(uuid.getMostSignificantBits()) +
                toHexString(uuid.getLeastSignificantBits());
    }

    // 将long值转为16个字符的十六进制表示
    private static String toHexString(long value) {
        return String.format("%016x", value);
    }

    /**
     * 将字节数组转为字符串
     *
     * @param array
     * @return
     */
    public static String toString(byte[] array) {
        return new String(array, StandardCharsets.UTF_8);
    }

    /**
     * 将对象转换为字节数组
     *
     * @param obj 需要转换的对象
     * @param <T> 对象类型
     * @return 字节数组，如果对象为空则返回null
     */
    public static <T> byte[] toByteArray(T obj) {
        // 检查输入参数
        if (Objects.isNull(obj)) {
            return null;
        }
        if (obj instanceof String) {
            return ((String) obj).getBytes(StandardCharsets.UTF_8);
        }

        // 将对象转换为JSON字符串
        String json = ReliableMqJsonUtils.toJson(obj);

        // 检查转换结果
        if (Objects.isNull(json)) {
            return null;
        }

        // 将JSON字符串转换为字节数组
        return json.getBytes(StandardCharsets.UTF_8);
    }
}