package com.spotifire.core.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpotifireBackApplication {

	// @Value("${use.proxy}")
	// private boolean useProxy;
	//
	// @Value("${proxy.host}")
	// private String proxyHost;
	//
	// @Value("${proxy.port}")
	// private Integer proxyPort;

	public static void main(String[] args) {
		SpringApplication.run(SpotifireBackApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {

		RestTemplate restTemplate = new RestTemplate();

		// Proxy settings if needed
		// if (this.useProxy) {
		// SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyHost, this.proxyPort));
		// factory.setProxy(proxy);
		// restTemplate.setRequestFactory(factory);
		// }

		return restTemplate;

	}
}
