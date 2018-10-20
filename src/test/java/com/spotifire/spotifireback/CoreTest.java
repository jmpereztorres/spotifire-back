package com.spotifire.spotifireback;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.spotifire.SpotifireBackApplication;
import com.spotifire.persistence.pojo.Evidence;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.repository.ITransactionalRepository;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

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
	public void fetchTwitter() {
		Twitter twitter = new TwitterFactory().getInstance();
		// Twitter Consumer key & Consumer Secret
		twitter.setOAuthConsumer("l3socwjpwuFbis9sDX56PIIxP", "5M8o0Qqj6AWLN2ZBp1LORrCBXziyfUYVgW8WWNky8vwyuDa1gP");
		// Twitter Access token & Access token Secret
		twitter.setOAuthAccessToken(
				new AccessToken("225173447-OfaoIwrdiBx99UZf3r4vrfFZpZbZSxXMSDUYexTi", "U5w21beQoLfSQxb9Zb2fvECFAN8o7jDvRg4FtLABNZFdb"));

		Query query = new Query();
		query.setQuery("fire");

		List<Status> statuses = null;
		try {
			QueryResult queryResult = twitter.search(query);
			// // Getting Twitter Timeline using Twitter4j API
			// ResponseList statusReponseList = twitter.getUserTimeline(new Paging(1, 5));
			// for (Status status : statusReponseList) {
			// System.out.println(status.getText());
			// }
			// // Post a Tweet using Twitter4j API
			// Status status = twitter.updateStatus("Hello");
			// System.out.println("Successfully updated the status to [" + status.getText() + "].");
			statuses = queryResult.getTweets();
		} catch (Exception e) {
		}
		System.out.println("OK");
	}

	@Test
	@Transactional
	public void fillDatabase() {
		double range = 0.000030d;
		double latitudeBase = 39.4532f;
		double altitudeBase = -0.4262f;

		IntStream.range(0, 20).forEach(index -> {
			Location location = new Location(Math.random() * range + latitudeBase, Math.random() * range + altitudeBase);
			Evidence evidence = new Evidence();
			evidence.setCreationDate(new Date());
			evidence.setConfidence(80 + index % 3);
			evidence.setImpact(60 + index % 5);
			evidence.setLocation(location);

			this.repo.save(evidence);
		});
	}

	@Test
	public void checkConfiguration() {

		System.out.println("Testing checkConfiguration...");
		this.repo.save(new Location());

		System.out.println("Test OK");
	}

}
