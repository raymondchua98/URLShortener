package com.local.shortener;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Scanner;

@Service
public class UrlServiceImpl implements UrlService {
	
	private final String urlFormat = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	
	private final UrlRepository urlRepository;
	
	public UrlServiceImpl(UrlRepository urlRepository) {
		this.urlRepository = urlRepository;
	}
	
	/**
	 * Request to find URL by short code
	 * @param shortCode
	 * @return
	 */
	@Override
	public Url findUrlByUrlCode(String shortCode) {
		return urlRepository.findUrlByUrlCode(shortCode);
	}
	
	/**
	 * Request to create new Url by target Url
	 * @param targetUrl
	 * @return
	 */
	@Override
	public Url createUrl(String targetUrl) {
		// Will generate a short URL of max 10 in size
		String urlCode = ShortUrlUtils.generateRandomUrl(urlFormat.toCharArray(), 10);
		
		// Create a new URL that valid for 30 days
		Url url = new Url(urlCode, Constants.DOMAIN + "/r/" + urlCode, targetUrl, getUrlTitle(targetUrl), LocalDateTime.now().plusDays(30));
		
		System.out.println("Saving URL : " + url);
		urlRepository.save(url);
		return url;
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
}
