package com.spotifire.web.rest.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spotifire.core.service.IReportService;
import com.spotifire.core.utils.SpotifireUtils;
import com.spotifire.persistence.constants.ReportType;
import com.spotifire.persistence.constants.SourceType;
import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;
import com.spotifire.persistence.repository.ITransactionalRepository;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@Service
public class TwitterClientService implements ITwitterClientService {

	private static final Logger LOGGER = LogManager.getLogger(TwitterClientService.class);

	@Autowired
	private ITransactionalRepository transactionalRepository;

	@Autowired
	private IReportService reportService;

	@Override
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

				List<Report> persistedReports = this.transactionalRepository.findByExample(report);
				if (SpotifireUtils.isNotNullNorEmpty(persistedReports)) {

					report.setCreationDate(tweet.getCreatedAt());
					report.setSource(SourceType.TWITTER);
					report.setType(ReportType.FIRE);
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

	}

}
