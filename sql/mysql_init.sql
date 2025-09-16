DROP TABLE IF EXISTS `mq_message` ;
CREATE TABLE `mq_message` (
    `id` varchar(64) NOT NULL COMMENT '消息id',
    `business_id`    varchar(128)          DEFAULT NULL COMMENT '业务键（用于关联业务数据）',
    `topic`          varchar(128) NOT NULL COMMENT 'Rocketmq/Kafka的Topic,RabbitMQ的exchange',
    `tag`            varchar(128) NOT NULL DEFAULT '' COMMENT 'Rocketmq的tag,RabbitMQ的routing_key',
    `msg_body`   text         NOT NULL COMMENT '消息内容（JSON格式）',
    `send_count`     int(11) NOT NULL DEFAULT 0 COMMENT '发送次数',
    `send_last_time` datetime              DEFAULT NULL COMMENT '最后一次发送时间',
    `consumer_group`       varchar(128) COMMENT '消费者组，rocketmq/kafka的consumer group,rabbitmq中的queue',
    `consume_status`       tinyint(1) NOT NULL DEFAULT '0' COMMENT '消费状态：0-未消费,1-消费中,2-消费成功,3-消费失败',
    `consume_start_time`   datetime              DEFAULT NULL COMMENT '开始消费时间',
    `consume_success_time` datetime              DEFAULT NULL COMMENT '消费成功时间',
    `confirm_status` TINYINT      NOT NULL DEFAULT 0 COMMENT '消费成功状态确认，0-未确认, 1-已确认',
    `confirm_time`   DATETIME COMMENT '消费成功确认时间',
    `saved_by` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '消息保存方式:1-发送者保存,2-消费者保存',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='mq消息表';
ALTER TABLE `mq_message` ADD INDEX `ix_mq_message_1`(`business_id`);
ALTER TABLE `mq_message` ADD INDEX `ix_mq_message_2`(`created_at` desc,`consume_status`);


DROP TABLE IF EXISTS `mq_producer_message`;
CREATE TABLE `mq_producer_message`
(
    `id`             varchar(64)  NOT NULL COMMENT '消息id',
    `business_id`    varchar(128)          DEFAULT NULL COMMENT '业务键（用于关联业务数据）',
    `topic`          varchar(128) NOT NULL COMMENT 'Rocketmq/Kafka的Topic,RabbitMQ的exchange',
    `tag`            varchar(128) NOT NULL DEFAULT '' COMMENT 'Rocketmq的tag,RabbitMQ的routing_key',
    `msg_body`   text         NOT NULL COMMENT '消息内容（JSON格式）',
    `send_count`     int(11) NOT NULL DEFAULT 0 COMMENT '发送次数',
    `send_last_time` datetime              DEFAULT NULL COMMENT '最后一次发送时间',
    `confirm_status` TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未确认, 1-已确认',
    `confirm_time`   DATETIME COMMENT '确认时间',
    `created_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='MQ生产者消息表';
ALTER TABLE `mq_producer_message`
    ADD INDEX `ix_mq_producer_message_1`(`business_id`);
ALTER TABLE `mq_producer_message`
    ADD INDEX `ix_mq_producer_message_2`(`created_at` desc,confirm_status);

DROP TABLE IF EXISTS `mq_producer_consume_config`;
CREATE TABLE `mq_producer_consume_config`
(
    `id`             varchar(32)  NOT NULL COMMENT '主键',
    `topic`          varchar(128) NOT NULL COMMENT 'Rocketmq/Kafka的Topic,RabbitMQ的exchange',
    `tag`            varchar(128) NOT NULL DEFAULT '' COMMENT 'Rocketmq的tag,RabbitMQ的routing_key',
    `consumer_group` varchar(128) NOT NULL DEFAULT '' COMMENT '消费者组，rocketmq/kafka的consumer group,rabbitmq中的queue',
    `is_enabled`     tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用,1-启用',
    `created_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_topic_tag_group` (`topic`,`tag`,`consumer_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='MQ生产者消费配置表';

DROP TABLE IF EXISTS `mq_producer_consume_confirm`;
CREATE TABLE `mq_producer_consume_confirm`
(
    `id`             varchar(32)  NOT NULL COMMENT '主键',
    `msg_id`         varchar(64)  NOT NULL DEFAULT '' COMMENT '消息主题',
    `consumer_group` varchar(128) NOT NULL DEFAULT '' COMMENT '消费者组，rocketmq/kafka的consumer group,rabbitmq中的queue',
    `confirm_status` TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未确认, 1-已确认',
    `confirm_time`   DATETIME COMMENT '确认时间',
    `created_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='MQ生产者消费确认表';
ALTER TABLE `mq_producer_consume_confirm`
    ADD INDEX `ix_mq_producer_consume_confirm_1`(`msg_id`,`consumer_group`);
ALTER TABLE `mq_producer_consume_confirm`
    ADD INDEX `ix_mq_producer_consume_confirm_2`(`created_at` desc,`confirm_status`);

DROP TABLE IF EXISTS `mq_consumer_idempotent`;
CREATE TABLE `mq_consumer_idempotent`
(
    `id`                   varchar(32)  NOT NULL COMMENT '主键',
    `msg_id`               varchar(64)  NOT NULL DEFAULT '' COMMENT '消息id',
    `consumer_group`       varchar(128) NOT NULL COMMENT '消费者组，rocketmq/kafka的consumer group,rabbitmq中的queue',
    `consume_status`       tinyint(1) NOT NULL DEFAULT '0' COMMENT '消费状态：0-未消费,1-消费中,2-消费成功,3-消费失败',
    `consume_start_time`   datetime              DEFAULT NULL COMMENT '开始消费时间',
    `consume_success_time` datetime              DEFAULT NULL COMMENT '消费成功时间',
    `created_at`           datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`           datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='MQ消费者幂等表';
ALTER TABLE `mq_consumer_idempotent`
    ADD INDEX `ix_mq_consumer_idempotent_1`(`msg_id`,`consumer_group`);
ALTER TABLE `mq_consumer_idempotent`
    ADD INDEX `ix_mq_consumer_idempotent_2`(`created_at` desc,`consume_status`);