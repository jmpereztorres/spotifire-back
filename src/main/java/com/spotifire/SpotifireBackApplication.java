package com.spotifire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpotifireBackApplication {

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

}
