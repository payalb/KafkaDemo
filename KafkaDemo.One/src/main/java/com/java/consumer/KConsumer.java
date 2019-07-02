package com.java.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KConsumer {
	public static void main(String[] args) {
		Logger logger= Logger.getLogger(KConsumer.class.getName());
		Properties prop = new Properties();
		prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		//If using offset, group is mandatory
		//If we change the group id, it will again poll all messages. It resets an application if we change group id
		//If we run more consumers on same group, each would exclusively listen to a particular partition, as we add, they will rebalance
		prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "g1");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop);
		consumer.subscribe(Arrays.asList("topic2"));
		while (true) {
			ConsumerRecords<String, String> records=consumer.poll(Duration.ofMillis(5000));
			for(ConsumerRecord< String, String> record: records) {
			//Since no key was specified while sending message, key is null
				logger.info(record.key()+ ":"+ record.value());
				logger.info("Topic:"+record.topic());
				logger.info("Partition: "+record.partition());
				logger.info("Offset:"+ record.offset());
			}
		}
		
	}
}
