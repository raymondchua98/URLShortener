package com.local.shortener;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UrlEventRepository extends MongoRepository<UrlEvent, String> {
	
	List<UrlEvent> findAllByShortCode(String shortCode);
}
