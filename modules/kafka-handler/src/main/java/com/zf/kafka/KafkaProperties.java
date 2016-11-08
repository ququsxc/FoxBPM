package com.zf.kafka;

/**
 * Created by wangzhiwei on 2016/10/25.
 */
public interface KafkaProperties {
	final static String zkConnect = "192.168.1.141:2181";
	final static String groupId = "group1";
	final static String topic = "top1";
	final static String kafkaServerURL = "192.168.1.141";
	final static int kafkaServerPort = 9092;
	final static int kafkaProducerBufferSize = 64 * 1024;
	final static int connectionTimeOut = 20000;
	final static int reconnectInterval = 10000;
	final static String topic2 = "topic2";
	final static String topic3 = "topic3";
	final static String clientId = "SimpleConsumerDemoClient";
}
