package com.java.consumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KConsumer_Threads implements Runnable {

	static Logger logger = Logger.getLogger(KConsumer_Threads.class.getName());

	Properties prop = new Properties();
	KafkaConsumer<String, String> consumer = null;

	private CountDownLatch latch;

	public KConsumer_Threads(CountDownLatch latch) {
		this.latch = latch;
		prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "g2");
		consumer = new KafkaConsumer<>(prop);
		consumer.subscribe(Arrays.asList("topic2"));
	}

	@Override
	public void run() {
		try {
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
			for (ConsumerRecord<String, String> record : records) {
				// Since no key was specified while sending message, key is null
				logger.info(record.key() + ":" + record.value());
				logger.info("Topic:" + record.topic());
				logger.info("Partition: " + record.partition());
				logger.info("Offset:" + record.offset());
			}
		}
		}catch(WakeupException e) {
			logger.info("Received shutdown signal"); //expected exception
		}finally {
			consumer.close();
			latch.countDown();
		}
	}

	public void shutdown() {
		consumer.wakeup();//this will interrupt consumer.poll
		//it will throw exception: wakeup exception
	}
}

class Demo{
	public static void main(String[] args) {
		CountDownLatch latch= new CountDownLatch(1);
		final KConsumer_Threads consumer= new KConsumer_Threads(latch);
		Thread t= new Thread(consumer);
		t.start();
		Runtime.getRuntime().addShutdownHook(new Thread(()->  {
			System.out.println("caught shutdown hook");
			consumer.shutdown();
		}));
		try {
			latch.await();
		} catch (InterruptedException e) {
			System.out.println("application got interrupted");
		}
	}
}