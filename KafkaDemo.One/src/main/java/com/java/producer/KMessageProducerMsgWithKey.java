package com.java.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;

public class KMessageProducerMsgWithKey {
static	Logger logger= Logger.getLogger(KMessageProducerMsgWithKey.class);

	public static void main(String[] args) throws InterruptedException {
		
	
		// Create producer properties
		Properties prop = new Properties();
		// Can get properties from
		// https://kafka.apache.org/documentation/#producerconfigs
		prop.setProperty("acks", "all");
		prop.setProperty("key.serializer", IntegerSerializer.class.getName());
		prop.setProperty("value.serializer", StringSerializer.class.getName());
		// can get properties from ProducerConfig class too
		prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		// Create producer
		KafkaProducer<Integer, String> producer = new KafkaProducer<>(prop);
		// send messages
		for(int i=1; i<=100; i++) {
			
		//If we provide a key, it is guaranteed that the same key will go to the same partition
			ProducerRecord<Integer,String> record = new ProducerRecord<>("topic2", i%3, "Hello World!"+ i);
		producer.send(record, new Callback() {
			
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if(exception==null) {
				logger.info("Offset value:"+metadata.offset());
				logger.info("Partition number:"+metadata.partition());
				logger.info("Timestamp"+ metadata.timestamp());
				logger.info("Topic:"+ metadata.topic());
				
			}else {
				logger.error(exception.getMessage());
			}
			}
		});
		}
		//flush data
		producer.flush();
		//flush and close producer
		producer.close();
		Thread.sleep(5000);
		
	}
}
