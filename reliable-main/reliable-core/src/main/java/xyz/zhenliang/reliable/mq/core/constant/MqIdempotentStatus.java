package xyz.zhenliang.reliable.mq.core.constant;

public enum MqIdempotentStatus {
    /**
     * 可消费状态
     */
    CONSUMABLE(0, "可消费"),

    /**
     * 不能消费状态
     */
    NOT_CONSUMABLE(1, "不能消费"),

    /**
     * 消费成功状态
     */
    CONSUMED(2, "消费成功");

    private final int code;
    private final String description;

    MqIdempotentStatus(int code, String description) {
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