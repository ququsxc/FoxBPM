package com.zf.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

/**
 * Created by wangzhiwei on 2016/10/25.
 */

public class KafkaProducer extends Thread {
	private final Producer<Integer, String> producer;
	private final String topic;
	private final Properties props = new Properties();

	public KafkaProducer(String topic) {
		// 指定序列化处理类，默认为kafka.serializer.DefaultEncoder,即byte[]
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		// 指定kafka节点列表，用于获取metadata
		props.put("metadata.broker.list", "192.168.1.141:9092");
		producer = new Producer<Integer, String>(new ProducerConfig(props));
		this.topic = topic;
	}

	@Override
	public void run() {
		// int messageNo = 1;
		// while (true) {
		// String messageStr = new
		// String("{\"startFlow\":true,\"code\":\"100\"}");
		// System.out.println("Send:" + messageStr);
		// producer.send(new KeyedMessage<Integer, String>(topic, messageStr));
		// messageNo++;
		// try {
		// sleep(3000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		String messageStr = new String("{\"startFlow\":true,\"code\":\"100\"}");
		System.out.println("Send:" + messageStr);
		producer.send(new KeyedMessage<Integer, String>(topic, messageStr));
	}
}
