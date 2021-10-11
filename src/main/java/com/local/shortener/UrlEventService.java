package com.local.shortener;

import java.util.List;

public interface UrlEventService {
	/**
	 * Request to create new URL event by Url and client IP
	 * @param url
	 * @param clientIp
	 * @throws Exception
	 */
	void createNewUrlEvent(Url url, String clientIp) throws Exception;
	
	/**
	 * Request to find all events by Short Code
	 * @param shortCode
	 * @return
	 */
	List<UrlEvent> findAllByShortCode(String shortCode);
}
