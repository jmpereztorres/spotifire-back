package com.spotifire.spotifireback;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.spotifire.config.EmptyConfig;
import com.spotifire.persistence.constants.ReportType;
import com.spotifire.persistence.constants.SourceType;
import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

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
		System.out.println("Test Init");
	}

	@Test
	public void scoringImage() {

		System.out.println("Testing scoringImage...");

		BufferedImage imageInput = null;
		File input_file = null;

		try {
			input_file = new File("C:/Users/Carlos/Desktop/imagenes/no llamas/no_llamas_1.jpg");
			imageInput = ImageIO.read(input_file);
			System.out.println("Reading complete.");
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		int width = imageInput.getWidth();
		int height = imageInput.getHeight();

		int histogramReturn[][] = new int[3][256];

		int pixel = 0;
		int red = 0;
		int green = 0;
		int blue = 0;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixel = imageInput.getRGB(i, j);
				red = (pixel >> 16) & 0xFF;
				green = (pixel >> 8) & 0xFF;
				blue = pixel & 0xFF;

				histogramReturn[0][red] += 1;
				histogramReturn[1][green] += 1;
				histogramReturn[2][blue] += 1;
			}
		}

		int confidence = analyzeFire(histogramReturn, width, height);

		System.out.printf("Confidence of the image being of a fire: %d \n", confidence);
		System.out.println("Test OK");
	}

	private int analyzeFire(int histogram[][], int width, int height) {
		int meanHistogramRed = calculateHighMeanHistogram(histogram[0], width, height);
		int meanHistogramGreen = calculateHighMeanHistogram(histogram[1], width, height);
		int meanHistogramBlue = calculateHighMeanHistogram(histogram[2], width, height);

		double numberOfPixels = width * height;

		if ((numberOfPixels / 11000) > meanHistogramRed) {
			meanHistogramRed = 0;
		}

		int confidence = calculateConfidence(meanHistogramRed, meanHistogramGreen, meanHistogramBlue);

		return confidence;
	}

	private int calculateHighMeanHistogram(int histogram[], int width, int height) {
		int meanCalculated = 0;

		for (int i = 192; i < 256; i++) {
			meanCalculated += histogram[i];
		}

		meanCalculated = meanCalculated / (256 - 192);

		return meanCalculated;
	}

	private int calculateConfidence(int red, int green, int blue) {
		int confidence = 0;

		if (red > (blue + green)) {
			float difference = (float) (red - (blue + green)) / (red + blue + green);

			if (difference > 0.1) {
				if (difference < 1) {
					confidence = (int) (difference * 100);
				} else {
					confidence = 99;
				}
			} else {
				confidence = 0;
			}
		} else {
			confidence = 0;
		}

		return confidence;
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
				report.setCreationDate(tweet.getCreatedAt());
				report.setSource(SourceType.TWITTER.toString());
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

			});

		} catch (Exception e) {
		}
		System.out.println("OK");
	}
}
