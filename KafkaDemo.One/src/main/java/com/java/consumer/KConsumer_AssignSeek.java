package com.java.consumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KConsumer_AssignSeek {

	public static void main(String[] args) {
		Logger logger = Logger.getLogger(KConsumer.class.getName());
		Properties prop = new Properties();
		prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		// If using offset, group is mandatory
		// Not using groupid
		// assign and seek are used to replay data or fetch a specific message
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop);
		TopicPartition partition1 = new TopicPartition("topic2", 0);
		//assign to a partition
		consumer.assign(Collections.singleton(partition1));
		long offsetToReadrom = 20L;
		//84Omx2GKC4WIzpLUNvQzye9QM: api key
		//dJ8mbPlmhsMldG59gkOtKNX65m16KUOj92tFuqYzqrtR1P5mcL: api secret key
		//1146110273666744320-5zRmJI2ConxDE0CsUWYNIJ1PZpG3Vq: access token
		//hFNEpzoEpA4wAKs8lO1Q2WOdq7FksN4UTFhjiNgnCKAK5: access token secret

		//seek from an offset
		consumer.seek(partition1, offsetToReadrom);
		int numberOfMessagesToRead = 5;
		int noOfMsgsRead = 0;
		boolean done=false;
		while (!done) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
			for (ConsumerRecord<String, String> record : records) {
				// Since no key was specified while sending message, key is null
				logger.info(record.key() + ":" + record.value());
				logger.info("Topic:" + record.topic());
				logger.info("Partition: " + record.partition());
				logger.info("Offset:" + record.offset());
				noOfMsgsRead++;
				if(noOfMsgsRead >= numberOfMessagesToRead) {
					done=true;
					break;
				}
			}
			
		}
	}
}
