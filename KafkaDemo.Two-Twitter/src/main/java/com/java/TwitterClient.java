package com.java;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterClient {
	/** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
	static BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
	public static void main(String[] args) throws InterruptedException {
		//Create twitter client
		
		Client twitterClient=createTwitterClient();
		twitterClient.connect();
		//create kafka producer
		System.out.println("connection acquired");
		
		KafkaProducer< String,String> producer= getProducer();
		//send tweets to kafka
		// on a different thread, or multiple different threads....
		while (!twitterClient.isDone()) {
		  String msg = msgQueue.poll(5, TimeUnit.SECONDS);
		  if(msg==null) {
			  
		  }
		  ProducerRecord< String, String> record= new ProducerRecord<>("twitter_tweets", msg);
		  producer.send(record, new Callback() {
			
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if(exception!= null) {
					System.out.println(exception.getMessage());
				}else {
					System.out.println(metadata.offset()+ ","+ metadata.partition());
				}
				
			}
		});
		  
		System.out.println(msg);
		}
	}

	private static KafkaProducer<String, String> getProducer(){
		Properties properties= new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		KafkaProducer<String, String> producer= new KafkaProducer<>(properties);
		return producer;
	}
	private static Client createTwitterClient() {
		
		/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
		List<String> terms = Lists.newArrayList("kafka");
		//hosebirdEndpoint.followings(followings);
		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1("consumeraccesskey", "consumersecret", "accesstoken", "accesstokensecret");
		
		ClientBuilder builder = new ClientBuilder()
				  .name("Hosebird-Client-01")                              // optional: mainly for the logs
				  .hosts(hosebirdHosts)
				  .authentication(hosebirdAuth)
				  .endpoint(hosebirdEndpoint)
				  
				  .processor(new StringDelimitedProcessor(msgQueue));
				//  .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

				Client hosebirdClient = builder.build();
				return hosebirdClient;
	}
}
