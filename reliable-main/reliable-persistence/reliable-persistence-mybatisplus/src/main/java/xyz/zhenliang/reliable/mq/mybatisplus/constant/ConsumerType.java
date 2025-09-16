package xyz.zhenliang.reliable.mq.mybatisplus.constant;

public enum ConsumerType {
    /**
     * 单个消费者
     */
    SINGLE(1, "单个消费者"),

    /**
     * 多个消费者
     */
    MULTIPLE(2, "多个消费者");

    private final int code;
    private final String description;

    ConsumerType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}