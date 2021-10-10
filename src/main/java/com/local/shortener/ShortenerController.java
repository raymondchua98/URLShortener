package com.local.shortener;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@RestController
public class ShortenerController {
	
	private final UrlRepository urlRepository;
	
	private final UrlEventRepository urlEventRepository;
	
	private final String urlFormat = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-0123456789";
	
	private final String domain = "localhost:8080";
	
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
	
	@RequestMapping(value = "/{shortUrl}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectWithParams(@PathVariable String shortUrl, HttpServletRequest request) {
		if (shortUrl.isEmpty()) {
			return new ResponseEntity<>("Short URL cannot be empty", HttpStatus.BAD_REQUEST);
		}
		Url url = urlRepository.findUrlByUrlCode(shortUrl);
		if (url != null) {
			if (url.getExpiredDate().isBefore(LocalDateTime.now())) {
				System.out.println("URL has expired");
				return new ResponseEntity<>("URL has expired", HttpStatus.BAD_REQUEST);
			}
			
			// Insert new URL event
			LocalDateTime timeStamp = LocalDateTime.now();
			UrlEvent urlEvent = new UrlEvent(shortUrl + timeStamp, shortUrl, url.getTargetUrl(), getClientIpAddress(request), timeStamp);
			urlEventRepository.save(urlEvent);
			
			return new ResponseEntity<>(url.getTargetUrl(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No Short URL found", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@PostMapping(path = "/api/createUrl")
	public Url createUrl(@RequestBody Url newUrl) {
		System.out.println("Url : " + newUrl);
		
		String targetUrl = newUrl.getTargetUrl();
		System.out.println("Target Url : " + targetUrl);
		if ( targetUrl.isEmpty()) {
			throw new IllegalArgumentException("Target URL cannot be null");
		}
		
		// Will generate a short URL of max 15 in size
		String urlCode = ShortUrlUtils.generateRandomUrl(urlFormat.toCharArray(), 15);
		newUrl.setUrlCode(urlCode);
		newUrl.setShortUrl(domain + "/" + urlCode);
		newUrl.setUrlTitle(getUrlTitle(targetUrl));
		newUrl.setExpiredDate(LocalDateTime.now().plusDays(30));
		// A URL should only be valid for 30 days
		System.out.println("Saving URL : " + newUrl);
		urlRepository.save(newUrl);
		return newUrl;
	}
	
	@RequestMapping(value = "/report/short-code", method = RequestMethod.GET)
	public String generateShortCodeReport(String shortCode, HttpServletRequest request) {
		List<UrlEvent> list = urlEventRepository.findAllByShortCode(shortCode);
		System.out.println(list.toString());
		UrlReport urlReport = new UrlReport((long) list.size(), list.get(0).getTargetUrl(), list);
		StringBuilder report = new StringBuilder(("==== Short URL Report [" + shortCode + "] ====" + "\n"));
		report.append("Number of Clicks : ").append(urlReport.numberOfClicks).append("\n");
		report.append("Target URL : ").append(list.get(0).getTargetUrl()).append("\n\n");
		report.append("====  Access Events ====" + "\n");
		for (UrlEvent urlEvent: list) {
			report.append("Accessed IP : ").append(urlEvent.getOriginIp()).append("\n");
			report.append("Accessed Timestamp : ").append(urlEvent.getTimestamp()).append("\n\n");
		}
		return report.toString();
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
			return "";
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
}
