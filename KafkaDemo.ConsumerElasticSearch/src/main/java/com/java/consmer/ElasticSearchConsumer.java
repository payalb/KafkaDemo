package com.java.consmer;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class ElasticSearchConsumer {
	public static void main(String[] args) throws IOException {
		
		final CredentialsProvider provider= new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("a6h0ye82el","731twex95s"));
		RestHighLevelClient client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost("es1-476672249.ap-southeast-2.bonsaisearch.net", 443, "https")).setHttpClientConfigCallback(new HttpClientConfigCallback() {
							
							public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
								return httpClientBuilder.setDefaultCredentialsProvider(provider);
							}
						}));
		
		IndexRequest request= new IndexRequest("twitter", "tweets");
		String jsonData="{\" name \": \" payal \"}";
		request.source(jsonData,XContentType.JSON);
		IndexResponse response=client.index(request, RequestOptions.DEFAULT);
		System.out.println(response.getId());
		client.close();
	}
}
