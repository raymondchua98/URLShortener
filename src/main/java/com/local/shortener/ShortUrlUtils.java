package com.local.shortener; 

import java.security.SecureRandom;
import java.util.Random;
// Reference: https://github.com/aventrix/jnanoid
public final class ShortUrlUtils {

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
}
