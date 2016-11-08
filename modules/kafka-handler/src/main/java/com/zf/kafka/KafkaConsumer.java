package com.zf.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wangzhiwei on 2016/10/25.
 */

public abstract class KafkaConsumer extends Thread {
	private final ConsumerConnector consumer;
	private final String topic;

	public KafkaConsumer(String topic) {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());
		this.topic = topic;
	}

	private static ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		// zookeeper 配置
		props.put("zookeeper.connect", KafkaProperties.zkConnect);
		// group 代表一个消费组,加入组里面,消息只能被该组的一个消费者消费
		// 如果所有消费者在一个组内,就是传统的队列模式,排队拿消息
		// 如果所有的消费者都不在同一个组内,就是发布-订阅模式,消息广播给所有组
		// 如果介于两者之间,那么广播的消息在组内也是要排队的
		props.put("group.id", KafkaProperties.groupId);
		// ZooKeeper的最大超时时间，就是心跳的间隔，若是没有反映，那么认为已经死了，不易过大
		props.put("zookeeper.session.timeout.ms", "40000");
		// zk follower落后于zk leader的最长时间
		props.put("zookeeper.sync.time.ms", "200");
		// 往zookeeper上写offset的频率
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

	@Override
	public void run() {
		// 描述读取哪个topic，需要几个线程读
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));

		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			String message = new String(it.next().message());
			System.out.println("receive：" + message);
			try {
				handleMessage(message);
				sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract void handleMessage(String message);
}
