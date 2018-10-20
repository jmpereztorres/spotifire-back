package com.spotifire.spotifireback;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
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
import com.spotifire.core.service.IReportService;
import com.spotifire.core.utils.SpotifireUtils;
import com.spotifire.persistence.constants.ReportType;
import com.spotifire.persistence.constants.SourceType;
import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.persistence.pojo.Evidence;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;
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

	@Autowired
	private IReportService reportService;

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
		query.setQuery(SpotifireConstants.SPOTIFIRE_TWITTER_HASHTAG);
		query.setCount(100);
		List<Status> statuses = null;
		try {
			QueryResult queryResult = twitter.search(query);
			statuses = queryResult.getTweets();
			statuses.stream().forEach(tweet -> {

				Report report = new Report();
				report.setTwitterId(tweet.getId());

				List<Report> persistedReports = this.repo.findByExample(report);
				if (SpotifireUtils.isNotNullNorEmpty(persistedReports)) {

					report.setCreationDate(tweet.getCreatedAt());
					report.setSource(SourceType.TWITTER);
					report.setType(ReportType.FIRE.toString());
					report.setDescription(tweet.getText());

					if (tweet.getGeoLocation() != null) {
						report.setLocation(new Location(tweet.getGeoLocation().getLatitude(), tweet.getGeoLocation().getLongitude()));
					}

					if (tweet.getMediaEntities() != null && tweet.getMediaEntities().length > 0
							&& tweet.getMediaEntities()[0].getType().equals("photo")) {

						try {
							URL url = new URL(tweet.getMediaEntities()[0].getMediaURL());
							BufferedImage image = ImageIO.read(url);

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(image, "jpg", baos);
							baos.flush();
							report.setHasImage(true);
							report.setImage(baos.toByteArray());
						} catch (IOException e) {
							LOGGER.error("Error reading tweet image");
						}

					}

					this.reportService.processReport(report);
				}
			});

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
	public void asdads() {
		List<Evidence> list = this.repo.findByExample(new Evidence());
		System.out.println("Test OK");
	}

	@Test
	public void checkConfiguration() {

		System.out.println("Testing checkConfiguration...");
		this.repo.save(new Location());

		System.out.println("Test OK");
	}

}
