package com.local.shortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@Service
public class UrlEventServiceImpl implements UrlEventService {
	
	private final UrlEventRepository urlEventRepository;
	
	private final String geoLocApiKey = "8f10dfdc3cf44ef7977b5768a57ffee6";
	
	private final String geoLocApiUrl = "https://api.ipgeolocation.io/ipgeo";
	
	public UrlEventServiceImpl(UrlEventRepository urlEventRepository) {
		this.urlEventRepository = urlEventRepository;
	}
	
	/**
	 * Request to create new URL event by Url and client IP
	 * @param url
	 * @param clientIp
	 * @throws Exception
	 */
	public void createNewUrlEvent(Url url, String clientIp) throws Exception {
		if (url.getExpiredDate().isBefore(LocalDateTime.now())) {
			System.out.println("URL has expired");
			throw new Exception("URL has expired");
		}
		
		// Insert new URL event
		LocalDateTime timeStamp = LocalDateTime.now();
		
		// For local environment, it will return 0.0.0.0.0.0.0.1. Thus, it is advised to replace the IP with your own IP to get geolocation
		System.out.println(clientIp);
		if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
			// Replace your IP here
			clientIp = "60.52.96.127";
		}
		UrlEvent urlEvent = new UrlEvent(url.getUrlCode() + timeStamp, url.getUrlCode(), url.getTargetUrl(), clientIp, getGeoLocation(clientIp), timeStamp);
		System.out.println(urlEvent);
		urlEventRepository.save(urlEvent);
	}
	
	/**
	 * Request to find all events by Short Code
	 * @param shortCode
	 * @return
	 */
	public List<UrlEvent> findAllByShortCode(String shortCode) {
		return urlEventRepository.findAllByShortCode(shortCode);
	}
	
	private String getGeoLocation(String ip) {
		try {
			String charSet = "UTF-8";
			String query = String.format("apiKey=%s&ip=%s",
					URLEncoder.encode(geoLocApiKey, charSet),
					URLEncoder.encode(ip, charSet));
			
			URLConnection connection = new URL(geoLocApiUrl + "?" + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charSet);
			InputStream response = connection.getInputStream();
			try (Scanner scanner = new Scanner(response)) {
				String responseBody = scanner.useDelimiter("\\A").next();
				ObjectMapper objectMapper = new ObjectMapper();
				Geolocation geo = objectMapper.readValue(responseBody, Geolocation.class);
				System.out.println("GeoLocation : " + geo);
				return(geo.generateOriginGeo());
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return "";
	}
}
