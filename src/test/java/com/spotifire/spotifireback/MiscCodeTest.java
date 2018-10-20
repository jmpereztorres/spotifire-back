package com.spotifire.spotifireback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.spotifire.config.EmptyConfig;

/**
 *
 * Para probar código suelto, sin referencias al contexto de Spring y sus service, manager y dao
 *
 * @author aars
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmptyConfig.class }, loader = AnnotationConfigContextLoader.class)
public class MiscCodeTest {

	private static final Logger LOGGER = LogManager.getLogger(MiscCodeTest.class);

	@Test
	public void checkConfiguration() {

		System.out.println("Testing checkConfiguration...");
		LOGGER.debug("EEEEES");
		System.out.println("Test OK");
	}

}
