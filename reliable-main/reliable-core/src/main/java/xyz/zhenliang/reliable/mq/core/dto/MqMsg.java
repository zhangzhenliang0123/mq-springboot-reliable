package xyz.zhenliang.reliable.mq.core.dto;

public class MqMsg<T> extends MqMsgMeta {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
