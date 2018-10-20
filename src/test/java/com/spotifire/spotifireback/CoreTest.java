package com.spotifire.spotifireback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.spotifire.core.application.SpotifireBackApplication;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.repository.ITransactionalRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpotifireBackApplication.class)
@SpringBootTest
public class CoreTest {

	private static final Logger LOGGER = LogManager.getLogger(CoreTest.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ITransactionalRepository repo;

	@Test
	public void contextLoads() {
		System.out.println("OK");
	}

	@Test
	public void checkConfiguration() {

		System.out.println("Testing checkConfiguration...");
		this.repo.save(new Location());

		System.out.println("Test OK");
	}

}
