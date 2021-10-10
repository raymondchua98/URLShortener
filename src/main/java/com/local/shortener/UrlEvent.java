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
public class UrlEvent {
	
	@Id
	private String id;
	
	private String shortCode;
	
	private String targetUrl;
	
	private String originIp;
	
	private String originGeolocation;
	
	private LocalDateTime timestamp;
	
	public UrlEvent() {
	}
	
	public UrlEvent(String id, String shortCode, String targetUrl, String originIp, String originGeolocation, LocalDateTime timestamp) {
		this.id = id;
		this.shortCode = shortCode;
		this.targetUrl = targetUrl;
		this.originIp = originIp;
		this.originGeolocation = originGeolocation;
		this.timestamp = timestamp;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getShortCode() {
		return shortCode;
	}
	
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	
	public String getTargetUrl() {
		return targetUrl;
	}
	
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	
	public String getOriginIp() {
		return originIp;
	}
	
	public void setOriginIp(String originIp) {
		this.originIp = originIp;
	}
	
	public String getOriginGeolocation() {
		return originGeolocation;
	}
	
	public void setOriginGeolocation(String originGeolocation) {
		this.originGeolocation = originGeolocation;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return "UrlEvent [" +
				"id= " + id +
				", shortCode= " + shortCode +
				", targetUrl= " + targetUrl +
				", originIp= " + originIp +
				", originGeolocation= " + originGeolocation +
				", timestamp= " + timestamp +
				']';
	}
}
