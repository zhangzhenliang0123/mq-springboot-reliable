package xyz.zhenliang.reliable.mq.core.constant;

public enum ConsumeStatus {
    /**
     * 未消费状态
     */
    NOT_CONSUMED(0, "未消费"),

    /**
     * 消费中状态
     */
    CONSUMING(1, "消费中"),

    /**
     * 消费成功状态
     */
    CONSUMED_SUCCESS(2, "消费成功"),

    /**
     * 消费失败状态
     */
    CONSUMED_FAILURE(3, "消费失败");

    private final int code;
    private final String description;

    ConsumeStatus(int code, String description) {
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