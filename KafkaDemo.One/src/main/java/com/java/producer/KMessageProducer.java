package com.java.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class KMessageProducer {
	public static void main(String[] args) throws InterruptedException {
		// Create producer properties
		Properties prop = new Properties();
		// Can get properties from
		// https://kafka.apache.org/documentation/#producerconfigs
		prop.setProperty("acks", "all");
		prop.setProperty("key.serializer", StringSerializer.class.getName());
		prop.setProperty("value.serializer", StringSerializer.class.getName());
		// can get properties from ProducerConfig class too
		prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		// Create producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(prop);
		// send messages
		ProducerRecord record = new ProducerRecord<String, String>("topic2", "Hello World!");
		producer.send(record);
		// asynchronous call , running in background.
		//flush data
		producer.flush();
		//flush and close producer
		producer.close();
		Thread.sleep(5000);

	}
}
