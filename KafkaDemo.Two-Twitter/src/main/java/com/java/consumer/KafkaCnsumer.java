package com.java.consumer;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class KafkaCnsumer {
	static Logger logger = Logger.getLogger(KafkaCnsumer.class);

	public static void main(String[] args) throws IOException {
		KafkaConsumer<String, String> consumer = getKafkaConsumer();
		RestHighLevelClient client = getElasticSearchProvider();
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
			if (records != null) {
				int recordCount = records.count();
				logger.info("Received messages" + records.count());
				System.out.println("Received messages" + records.count());
				BulkRequest bulkRequest = new BulkRequest();
				for (ConsumerRecord<String, String> record : records) {

					// 2 ways of generating id
					// String id=record.topic()+"_"+ record.partition()+"_"+
					// record.offset();//unique id
					// Or
					if (record != null && record.value() != null) {
						String id = extractIdFromTweet(record.value());
						IndexRequest request = new IndexRequest("twitter", "tweets", id);// to make our consumer
																							// idempotent
						// String jsonData="{\" name \": \" payal \"}";
						bulkRequest.add(request);
						//Instead of sending 1 request at time, send it in bulk.
						/*
						 * 
						 * request.source(record.value(), XContentType.JSON); IndexResponse response =
						 * client.index(request, RequestOptions.DEFAULT);
						 * System.out.println(response.getId());
						 */
					}

				}
				if (recordCount > 0) {
					BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
					// if before commit, it dies , would be reprocessed
					logger.info("Offsets are being committed");
					consumer.commitSync();
					logger.info("Offsets have been committed");
				}
			}
		}

		// client.close();

	}

//get unique id from elasticsearch
	private static String extractIdFromTweet(String value) {
		JsonParser parser = new JsonParser();
		return parser.parse(value).getAsJsonObject().get("id").getAsString();
	}

	private static KafkaConsumer<String, String> getKafkaConsumer() {
		Properties prop = new Properties();
		prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		// diasble auto-commit of offsets
		prop.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		// controls how many records to poll per request
		prop.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
		/*
		 * prop.setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,32*1024+"");
		 * prop.setProperty(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,"20");
		 */
		// If using offset, group is mandatory
		// If we change the group id, it will again poll all messages. It resets an
		// application if we change group id
		// If we run more consumers on same group, each would exclusively listen to a
		// particular partition, as we add, they will rebalance
		prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "kafka-elasticsearch");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop);
		consumer.subscribe(Arrays.asList("twitter_tweets"));// topic name
		return consumer;
	}

	private static RestHighLevelClient getElasticSearchProvider() {
		final CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("a6h0ye82el", "731twex95s"));
		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("es1-476672249.ap-southeast-2.bonsaisearch.net", 443, "https"))
						.setHttpClientConfigCallback(new HttpClientConfigCallback() {

							public HttpAsyncClientBuilder customizeHttpClient(
									HttpAsyncClientBuilder httpClientBuilder) {
								return httpClientBuilder.setDefaultCredentialsProvider(provider);
							}
						}));
		return client;
	}
}
