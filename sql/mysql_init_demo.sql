
INSERT INTO `mq_producer_consume_config` (`id`, `topic`, `tag`, `consumer_group`, `is_enabled`) VALUES ('1', 'demo.order.exchange', 'demo.order.routing.key', 'demo.order.queue', 1);
INSERT INTO `mq_producer_consume_config` (`id`, `topic`, `tag`, `consumer_group`, `is_enabled`) VALUES ('2', 'demo_order_topic', 'demo_order_tag', 'demo_order_consumer_group', 1);
