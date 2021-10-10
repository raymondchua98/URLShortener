package com.local.shortener;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UrlRepository extends MongoRepository<Url, String> {
	
	Url findUrlByUrlCode(String urlCode);
	
	List<Url> findUrlByTargetUrl(String targetUrl);
	
}
