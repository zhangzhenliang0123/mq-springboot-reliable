package xyz.zhenliang.reliable.mq.core.constant;

public enum ConfirmStatus {
    /**
     * 未确认状态
     */
    UNCONFIRMED(0, "未确认"),

    /**
     * 已确认状态
     */
    CONFIRMED(1, "已确认");

    private final int code;
    private final String description;

    ConfirmStatus(int code, String description) {
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