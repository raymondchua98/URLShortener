package com.local.shortener;

import java.util.List;

public class UrlReport {
	Long numberOfClicks;
	
	String targetUrl;
	
	List<UrlEvent> urlEvents;
	
	public UrlReport() {
	}
	
	public UrlReport(Long numberOfClicks, String targetUrl, List<UrlEvent> urlEvents) {
		this.numberOfClicks = numberOfClicks;
		this.targetUrl = targetUrl;
		this.urlEvents = urlEvents;
	}
	
	public Long getNumberOfClicks() {
		return numberOfClicks;
	}
	
	public void setNumberOfClicks(Long numberOfClicks) {
		this.numberOfClicks = numberOfClicks;
	}
	
	public String getTargetUrl() {
		return targetUrl;
	}
	
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	
	public List<UrlEvent> getUrlEvents() {
		return urlEvents;
	}
	
	public void setUrlEvents(List<UrlEvent> urlEvents) {
		this.urlEvents = urlEvents;
	}
	
	@Override
	public String toString() {
		return "UrlReport [" +
				"numberOfClicks= " + numberOfClicks +
				", targetUrl= " + targetUrl +
				", urlEvents= " + urlEvents +
				']';
	}
}
