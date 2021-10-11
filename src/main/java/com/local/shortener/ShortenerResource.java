package com.local.shortener;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class ShortenerResource {
	
	private final UrlService urlService;
	
	private final UrlEventService urlEventService;
	
	private final ShortUrlUtils shortUrlUtils;
	
	public ShortenerResource(UrlService urlService, UrlEventService urlEventService, ShortUrlUtils shortUrlUtils) {
		this.urlService = urlService;
		this.urlEventService = urlEventService;
		this.shortUrlUtils = shortUrlUtils;
	}
	
	/**
	 * Request to redirect to target URL by short code
	 * @param shortCode
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/r/{shortCode}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectWithParams(@PathVariable String shortCode, HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("REST to redirect to target URL by shortUrl: " + shortCode);
		if (shortCode == null || shortCode.isEmpty()) {
			response.sendRedirect(Constants.DOMAIN);
			return new ResponseEntity<>("Short URL cannot be empty", HttpStatus.BAD_REQUEST);
		}
		String clientIp = shortUrlUtils.getClientIpAddress(request);
		Url url = urlService.findUrlByUrlCode(shortCode);
		if (url == null) {
			return new ResponseEntity<>("<h1>No Short URL found... Please check your URL again</h1>", HttpStatus.BAD_REQUEST);
		}
		
		try {
			urlEventService.createNewUrlEvent(url,  clientIp);
			response.sendRedirect(url.getTargetUrl());
			return new ResponseEntity<>(url.getTargetUrl(), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Request to create new Url
	 * @param newUrl
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/api/createUrl")
	public Url createUrl(@RequestBody Url newUrl) throws Exception {
		System.out.println("REST to create new shortUrl by target URL: " + newUrl);
		String targetUrl = newUrl.getTargetUrl();
		if (targetUrl == null || targetUrl.isEmpty()) {
			throw new IllegalArgumentException("Target URL cannot be null");
		}
		return urlService.createUrl(targetUrl);
	}
	
	/**
	 * Request to generate usage report of Short Code
	 * @param shortCode
	 * @return
	 */
	@RequestMapping(value = "/api/report/short-code", method = RequestMethod.GET)
	public UrlReport generateShortCodeReport(String shortCode) {
		System.out.println("REST to generate usage report by short code: " + shortCode);
		if (shortCode == null || shortCode.isEmpty()) {
			System.out.println("Short code cannot be null");
			return null;
		}
		List<UrlEvent> list = urlEventService.findAllByShortCode(shortCode);
		
		UrlReport urlReport = new UrlReport(0L, null, null);
		if(list.size() > 0) {
			urlReport = new UrlReport((long) list.size(), list.get(0).getTargetUrl(), list);
		}
		return urlReport;
	}
}
