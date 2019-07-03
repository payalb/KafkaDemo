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
		
		prop.setProperty("key.serializer", StringSerializer.class.getName());
		prop.setProperty("value.serializer", StringSerializer.class.getName());
		//enable idempotence
		prop.setProperty("acks", "all");
		prop.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		prop.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE+"");
		prop.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
		prop.setProperty(ProducerConfig.LINGER_MS_CONFIG,"20");
		prop.setProperty(ProducerConfig.BATCH_SIZE_CONFIG,32*1024+"");//32 Kb
		prop.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
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
