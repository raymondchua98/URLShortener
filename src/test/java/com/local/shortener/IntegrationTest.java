package com.local.shortener;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void testCreateUrl() {
		Url url = new Url(null, null, "https://www.coingecko.com", null, null);
		ResponseEntity<Url> responseEntity = this.restTemplate
				.postForEntity("http://localhost:" + port + "/api/createUrl", url, Url.class);
		Assertions.assertNotNull(responseEntity.getBody());
		Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
		Assertions.assertNotNull(responseEntity.getBody().getUrlCode());
		Assertions.assertNotNull(responseEntity.getBody().getShortUrl());
		Assertions.assertNotNull(responseEntity.getBody().getTargetUrl());
		Assertions.assertNotNull(responseEntity.getBody().getUrlTitle());
		Assertions.assertNotNull(responseEntity.getBody().getExpiredDate());
	}
	
	@Test
	public void testGenerateShortCodeReportIfShortCodeIsNull() {
		ResponseEntity<UrlReport> responseEntity = this.restTemplate
				.getForEntity("http://localhost:" + port + "/api/report/short-code", UrlReport.class);
		Assertions.assertNull(responseEntity.getBody());
	}
	
	@Test
	public void testGenerateShortCodeReportIfShortCodeIsEmpty() {
		String shortCode = "";
		ResponseEntity<UrlReport> responseEntity = this.restTemplate
				.getForEntity("http://localhost:" + port + "/api/report/short-code?shortCode=" + shortCode, UrlReport.class);
		Assertions.assertNull(responseEntity.getBody());
	}
	
	@Test
	public void testGenerateShortCodeReportIfShortCodeIsValid() {
		String shortCode = "123";
		ResponseEntity<UrlReport> responseEntity = this.restTemplate
				.getForEntity("http://localhost:" + port + "/api/report/short-code?shortCode=" + shortCode, UrlReport.class);
		Assertions.assertNotNull(responseEntity.getBody());
	}
}