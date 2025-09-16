package xyz.zhenliang.reliable.mq.mybatisplus.constant;

public enum EnableStatus {
    /**
     * 启用状态
     */
    ENABLE(1, "启用"),

    /**
     * 未启用状态
     */
    DISABLE(0, "未启用");

    private final int code;
    private final String description;

    EnableStatus(int code, String description) {
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