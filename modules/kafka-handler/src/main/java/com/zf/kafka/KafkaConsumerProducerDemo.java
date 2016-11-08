package com.zf.kafka;

import kafka.client.ClientUtils;

/**
 * Created by wangzhiwei on 2016/10/25.
 */
public class KafkaConsumerProducerDemo {
	public static void main(String[] args) {
		KafkaProducer producerThread = new KafkaProducer("top2");
		producerThread.start();
		// KafkaConsumer consumerThread = new
		// KafkaConsumer(KafkaProperties.topic);
		// consumerThread.start();
	}
}
