package com.local.shortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@RestController
public class ShortenerController {
	
	private final UrlRepository urlRepository;
	
	private final UrlEventRepository urlEventRepository;
	
	private final String urlFormat = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	private final String domain = "http://localhost:8080";
	
	private static final String[] IP_HEADER_CANDIDATES = {
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR",
			"HTTP_X_FORWARDED",
			"HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED",
			"HTTP_VIA",
			"REMOTE_ADDR"
	};
	
	public ShortenerController(UrlRepository urlRepository, UrlEventRepository urlEventRepository) {
		this.urlRepository = urlRepository;
		this.urlEventRepository = urlEventRepository;
	}
	
	@RequestMapping(value = "/r/{shortCode}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectWithParams(@PathVariable String shortCode, HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("REST to redirect to target URL by shortUrl: " + shortCode);
		if (shortCode.isEmpty()) {
			response.sendRedirect(domain);
			return new ResponseEntity<>("Short URL cannot be empty", HttpStatus.BAD_REQUEST);
		}
		Url url = urlRepository.findUrlByUrlCode(shortCode);
		if (url != null) {
			if (url.getExpiredDate().isBefore(LocalDateTime.now())) {
				System.out.println("URL has expired");
				return new ResponseEntity<>("URL has expired", HttpStatus.BAD_REQUEST);
			}
			
			// Insert new URL event
			LocalDateTime timeStamp = LocalDateTime.now();
			// For local environment, it will return 0.0.0.0.0.0.0.1. Thus, it is advised to replace the IP with your own IP to get geolocation
			String clientIp = getClientIpAddress(request);
			System.out.println(clientIp);
			if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
				// Replace your IP here
				clientIp = "60.52.96.127";
			}
			UrlEvent urlEvent = new UrlEvent(shortCode + timeStamp, shortCode, url.getTargetUrl(), clientIp, getGeoLocation(clientIp), timeStamp);
			System.out.println(urlEvent);
			urlEventRepository.save(urlEvent);
			response.sendRedirect(url.getTargetUrl());
			return new ResponseEntity<>(url.getTargetUrl(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No Short URL found", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(path = "/api/createUrl")
	public Url createUrl(@RequestBody Url newUrl) {
		System.out.println("REST to create new URL: " + newUrl);
		System.out.println("Url : " + newUrl);
		
		String targetUrl = newUrl.getTargetUrl();
		System.out.println("Target Url : " + targetUrl);
		if ( targetUrl.isEmpty()) {
			throw new IllegalArgumentException("Target URL cannot be null");
		}
		
		// Will generate a short URL of max 15 in size
		String urlCode = ShortUrlUtils.generateRandomUrl(urlFormat.toCharArray(), 10);
		newUrl.setUrlCode(urlCode);
		newUrl.setShortUrl(domain + "/r/" + urlCode);
		newUrl.setUrlTitle(getUrlTitle(targetUrl));
		newUrl.setExpiredDate(LocalDateTime.now().plusDays(30));
		// A URL should only be valid for 30 days
		System.out.println("Saving URL : " + newUrl);
		urlRepository.save(newUrl);
		return newUrl;
	}
	
	@RequestMapping(value = "/report/short-code", method = RequestMethod.GET)
	public UrlReport generateShortCodeReport(String shortCode, HttpServletRequest request) {
		System.out.println("REST to generate usage report by short code: " + shortCode);
		List<UrlEvent> list = urlEventRepository.findAllByShortCode(shortCode);
		System.out.println(list.toString());
		UrlReport urlReport = new UrlReport(0L, null, null);
		if(list.size() > 0) {
			urlReport = new UrlReport((long) list.size(), list.get(0).getTargetUrl(), list);
		}
		System.out.println("URL Report: " + urlReport.toString());
		return urlReport;
	}
	
	private String getUrlTitle(String targetUrl) {
		try {
			URL url = new URL(targetUrl);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			// Fake user agent to bypass 403 Forbidden by some websites
			httpConnection.setRequestMethod("GET");
			httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36");
			int responseCode = httpConnection.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream response = httpConnection.getInputStream();
				Scanner scanner = new Scanner(response);
				String responseBody = scanner.useDelimiter("\\A").next();
				response.close();
				String titleTag = "";
				// Remove <title> and </title> if found
				// Include tags if not generic tag
				if (responseBody.contains("<title>")) {
					titleTag = responseBody.substring(responseBody.indexOf("<title") + 7, responseBody.indexOf("</title>"));
				} else {
					titleTag = responseBody.substring(responseBody.indexOf("<title"), responseBody.indexOf("</title>") + 8);
				}
				return titleTag;
			} else {
				return "";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return ex.getMessage();
		} 
	}
	
	private static String getClientIpAddress(HttpServletRequest request) {
		for (String header : IP_HEADER_CANDIDATES) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}
	
	private static String getGeoLocation(String ip) {
		try {
			String apiKey = "8f10dfdc3cf44ef7977b5768a57ffee6";
			String url = "https://api.ipgeolocation.io/ipgeo";
			String charSet = "UTF-8";
			String query = String.format("apiKey=%s&ip=%s",
					URLEncoder.encode(apiKey, charSet),
					URLEncoder.encode(ip, charSet));
			
			URLConnection connection = new URL(url + "?" + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charSet);
			InputStream response = connection.getInputStream();
			try (Scanner scanner = new Scanner(response)) {
				String responseBody = scanner.useDelimiter("\\A").next();
				System.out.println(responseBody);
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
