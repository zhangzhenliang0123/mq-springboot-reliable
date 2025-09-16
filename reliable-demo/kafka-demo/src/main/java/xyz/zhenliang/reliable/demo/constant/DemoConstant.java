package xyz.zhenliang.reliable.demo.constant;

/**
 * 演示项目中使用的常量定义类
 * 包含订单相关主题、标签和消费者组的定义
 */
public class DemoConstant {
    /**
     * 订单主题名称 - 用于发送和接收订单消息的Kafka主题
     */
    public static final String ORDER_TOPIC = "demo_order_topic";

    /**
     * 订单标签 - 用于标识订单消息的标签
     */
    public static final String ORDER_TAG = "demo_order_tag";

    /**
     * 订单消费者组 - 处理订单消息的消费者组名称
     */
    public static final String ORDER_CONSUMER_GROUP = "demo_order_consumer_group";

    /**
     * 确认主题名称 - 用于发送和接收确认消息的Kafka主题
     */
    public static final String CONFIRM_TOPIC = "confirm_topic";

    /**
     * 确认标签 - 用于标识确认成功消息的标签
     */
    public static final String CONFIRM_TAG = "confirm_success";

    /**
     * 确认消费者组 - 处理确认消息的消费者组名称
     */
    public static final String CONFIRM_CONSUMER_GROUP = "confirm_consumer_group";
}