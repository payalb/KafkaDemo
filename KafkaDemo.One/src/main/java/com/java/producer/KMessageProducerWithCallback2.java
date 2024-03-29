package com.java.producer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class KMessageProducerWithCallback2 {
	static Logger logger= Logger.getLogger(KMessageProducerWithCallback2.class);;
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
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
		ProducerRecord<String,String> record = new ProducerRecord<>("topic2", "Hello World!");
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

					
					
		// asynchronous call , running in background.
		//flush data
		producer.flush();
		//flush and close producer
		producer.close();

	}
}
