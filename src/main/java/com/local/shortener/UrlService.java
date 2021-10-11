package com.local.shortener;

public interface UrlService {
	/**
	 * Request to find URL by short code
	 * @param shortCode
	 * @return
	 */
	Url findUrlByUrlCode(String shortCode);
	
	/**
	 * Request to create new Url by target Url
	 * @param newUrl
	 * @return
	 * @throws Exception
	 */
	Url createUrl(String newUrl) throws Exception;
}
