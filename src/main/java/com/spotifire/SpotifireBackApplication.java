package com.spotifire;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;

import java.security.KeyStoreException;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import org.apache.http.conn.ssl.SSLContexts;

import org.apache.http.conn.ssl.TrustStrategy;

import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;

@SpringBootApplication
public class SpotifireBackApplication {

	private static final String GENERIC_LOG = "com.spotifire";

	public static void main(String[] args) {
		SpringApplication.run(SpotifireBackApplication.class, args);
	}

	/**
	 * Loads a rest template client
	 *
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	@Qualifier("nasaRestTemplate")
	public RestTemplate getRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		TrustStrategy acceptingTrustStrategy = (chain, authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);

	}

}
