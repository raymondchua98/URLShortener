package com.local.shortener; 

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Random;

@Service
public final class ShortUrlUtils {
	
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
	
	public static String generateRandomUrl(char[] regex, int size) {
		Random random = new SecureRandom();

		final int mask = (2 << (int) Math.floor(Math.log(regex.length - 1) / Math.log(2))) - 1;
		final int step = (int) Math.ceil(1.6 * mask * size / regex.length);
		
		final StringBuilder idBuilder = new StringBuilder();
		
		while (true) {
			
			final byte[] bytes = new byte[step];
			random.nextBytes(bytes);
			
			for (int i = 0; i < step; i++) {
				
				final int index = bytes[i] & mask;
				
				if (index < regex.length) {
					idBuilder.append(regex[index]);
					if (idBuilder.length() == size) {
						return idBuilder.toString();
					}
				}
				
			}
			
		}
	}
	
	public String getClientIpAddress(HttpServletRequest request) {
		for (String header : IP_HEADER_CANDIDATES) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}
}
