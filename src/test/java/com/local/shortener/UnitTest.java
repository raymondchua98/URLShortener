package com.local.shortener;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
	
	private final String defaultRegex = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	@Test
	public void verifyGenerateShortUrlIsUnique() {
		// Test for 10000 times to ensure uniqueness in Short URL
		HashMap<String, String> urlMap = new HashMap<>();
		for (int i = 0; i < 10000; i++) {
			String urlCode = ShortUrlUtils.generateRandomUrl(defaultRegex.toCharArray(), 10);
			urlMap.put(urlCode, urlCode);
		}
		// If hit error, it means duplicate founds and should increase size
		System.out.println(urlMap);
	}
	
	@Test
	public void generateRandomUrlWithEmptyRegex() {
		String regex = "";
		String shortUrl = ShortUrlUtils.generateRandomUrl(regex.toCharArray(), 10);
		assertNull(shortUrl);
	}
	
	@Test
	public void generateRandomUrlWithSizeZero() {
		String shortUrl = ShortUrlUtils.generateRandomUrl(defaultRegex.toCharArray(), 0);
		assertNull(shortUrl);
	}
	
	@Test
	public void generateRandomUrlWithAllAsciiCodeRegex() {
		String regex = " !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
		String shortUrl = ShortUrlUtils.generateRandomUrl(regex.toCharArray(), 100);
		System.out.println(shortUrl);
	}
	
}