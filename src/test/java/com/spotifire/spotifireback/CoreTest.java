package com.spotifire.spotifireback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.spotifire.core.application.SpotifireBackApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpotifireBackApplication.class)
@SpringBootTest
public class CoreTest {

	@Test
	public void contextLoads() {
		System.out.println("OK");
	}

}
