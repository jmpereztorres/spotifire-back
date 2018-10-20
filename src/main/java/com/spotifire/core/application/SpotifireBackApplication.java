package com.spotifire.core.application;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@ComponentScan(basePackages = { "com.spotifire.core.service", "com.spotifire.persistence.repository", "com.spotifire.persistence.pojo" })
@EntityScan(basePackages = "com.spotifire.persistence.pojo")
public class SpotifireBackApplication {

	private static final String GENERIC_LOG = "com.spotifire";

	public static void main(String[] args) {
		SpringApplication.run(SpotifireBackApplication.class, args);
	}

	/**
	 * Generic Logger
	 *
	 * @return
	 */
	@Bean
	@Qualifier("genericLogger")
	public Logger createGenericLogger() {
		return org.slf4j.LoggerFactory.getLogger(GENERIC_LOG);
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
