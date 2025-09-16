package xyz.zhenliang.reliable.mq.mybatisplus.constant;

public enum MqSavedby {
    /**
     * 发送者保存
     */
    SENDER(1, "发送者保存"),

    /**
     * 消费者保存
     */
    CONSUMER(2, "消费者保存");

    private final int code;
    private final String description;

    MqSavedby(int code, String description) {
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