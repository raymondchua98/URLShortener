package com.local.shortener;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class ShortenerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ShortenerApplication.class, args);
	}
	
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb+srv://root:root@urlshortenercluster0.3lxio.mongodb.net/test?authSource=admin&replicaSet=atlas-1wjy41-shard-0&readPreference=primary&appname=MongoDB%20Compass&ssl=true\n");
	}
	
	public @Bean MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoClient(), "url-shortener");
	}
}
