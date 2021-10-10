package com.local.shortener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Url {
	
	@Id
	private String urlCode;
	
	private String shortUrl;
	
	private String targetUrl;
	
	private String urlTitle;
	
	private LocalDateTime expiredDate;
	
	public Url() {
	}
	
	public Url(String urlCode, String shortUrl, String targetUrl, String urlTitle, LocalDateTime expiredDate) {
		this.urlCode = urlCode;
		this.shortUrl = shortUrl;
		this.targetUrl = targetUrl;
		this.urlTitle = urlTitle;
		this.expiredDate = expiredDate;
	}
	
	public String getUrlCode() {
		return urlCode;
	}
	
	public void setUrlCode(String urlCode) {
		this.urlCode = urlCode;
	}
	
	public String getShortUrl() {
		return shortUrl;
	}
	
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	
	public String getTargetUrl() {
		return targetUrl;
	}
	
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	
	public String getUrlTitle() {
		return urlTitle;
	}
	
	public void setUrlTitle(String urlTitle) {
		this.urlTitle = urlTitle;
	}
	
	public LocalDateTime getExpiredDate() {
		return expiredDate;
	}
	
	public void setExpiredDate(LocalDateTime expiredDate) {
		this.expiredDate = expiredDate;
	}
	
	@Override
	public String toString() {
		return "Url [" +
				"urlCode= " + urlCode +
				", shortUrl= " + shortUrl +
				", targetUrl= " + targetUrl +
				", urlTitle= " + urlTitle +
				", expiredDate= " + expiredDate +
				']';
	}
}
