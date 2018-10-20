package com.spotifire.spotifireback;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;

import com.spotifire.config.EmptyConfig;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;
import com.spotifire.web.rest.dto.WeatherDTO;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 *
 * Para probar código suelto, sin referencias al contexto de Spring y sus
 * service, manager y dao
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

	/**
	 * @throws URISyntaxException
	 */
	@Test
	public void testAPIWeather() throws URISyntaxException {
		// https://api.darksky.net/forecast/f0882429cb4afe2fb72984e427e6789f/37.035229,-6.435476,1499860800?units=si&exclude=flags?exclude=alerts?exclude=minutely
		System.out.println("Testing testAPIWeather...");
		System.out.println("Test Init");
		float latitude = 37.035229f;
		float longitude = -6.435476f;
		long timeStamp = (new Date().getTime()) / 1000;

		StringBuilder sb = new StringBuilder();
		sb.append("https://api.darksky.net/forecast/f0882429cb4afe2fb72984e427e6789f/").append(latitude).append(",")
				.append(longitude).append(",").append(timeStamp)
				.append("?units=si&exclude=flags?exclude=alerts?exclude=minutely?exclude=hourly?exclude=daily");
		URI uriPetition = new URI(sb.toString());
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<WeatherDTO> response = restTemplate.exchange(uriPetition, HttpMethod.GET, null,
				WeatherDTO.class);

		System.out.println(response.getStatusCode() + ":" + response.getBody());

		int riskFactor = 0;

		double humidity = response.getBody().getCurrently().getHumidity();
		double precipIntensity = response.getBody().getCurrently().getPrecipIntensity();
		double precipProbability = response.getBody().getCurrently().getPrecipProbability();
		double windSpeed = response.getBody().getCurrently().getWindSpeed();

		if (windSpeed > 11) { // 40 km/h
			riskFactor += 0.2;
			if (windSpeed > 27) { // 100 km/h
				riskFactor += 0.2;
			}
		}

		if (humidity < 0.5) {
			riskFactor += 0.2;
			if (humidity < 0.25) {
				riskFactor += 0.2;
			}
		}

		if (precipProbability > 0.2) {
			if (precipProbability > 0.4) {
				riskFactor -= 0.1;
				if (precipProbability > 0.8) {
					riskFactor -= 0.1;
				}
			}
		} else {
			riskFactor += 0.2;
		}

		if (precipIntensity > 1) {
			riskFactor -= 0.1;
			if (precipIntensity > 3) {
				riskFactor -= 0.1;
				if (precipIntensity > 5) {
					riskFactor -= 0.1;
					if (precipIntensity > 10) {
						riskFactor = 0;
					}
				}
			}
		} else {
			riskFactor += 0.1;
		}

		int riskFactorInt = (int) riskFactor * 100;
		
		System.out.printf("riskFactor: %d \n", riskFactorInt);

		System.out.println("Test End");
	}

	@Test
	public void fetchTwitter() {
		Twitter twitter = new TwitterFactory().getInstance();
		// Twitter Consumer key & Consumer Secret
		twitter.setOAuthConsumer("l3socwjpwuFbis9sDX56PIIxP", "5M8o0Qqj6AWLN2ZBp1LORrCBXziyfUYVgW8WWNky8vwyuDa1gP");
		// Twitter Access token & Access token Secret
		twitter.setOAuthAccessToken(new AccessToken("225173447-OfaoIwrdiBx99UZf3r4vrfFZpZbZSxXMSDUYexTi",
				"U5w21beQoLfSQxb9Zb2fvECFAN8o7jDvRg4FtLABNZFdb"));

		Query query = new Query();
		query.setQuery("#spotifire");
		query.setCount(100);
		List<Status> statuses = null;
		try {
			QueryResult queryResult = twitter.search(query);
			statuses = queryResult.getTweets();
			System.out.println(statuses.size());
			statuses.stream().forEach(tweet -> {
				Report report = new Report();
				report.setTwitterId(tweet.getId());

//			List<Report> persistedReports = this.repo.findByExample(report);
//			if(SpotifireUtils.isNotNullNorEmpty(persistedReports)) {
//
//			}

				report.setCreationDate(tweet.getCreatedAt());
				if (tweet.getGeoLocation() != null) {
					report.setLocation(
							new Location(tweet.getGeoLocation().getLatitude(), tweet.getGeoLocation().getLongitude()));
				}

//			report.setSource(source);
				report.setDescription(tweet.getText());
//			report.setHasImage(hasImage);
				System.out.println(tweet.getText() + "\n");
			});
		} catch (Exception e) {
		}
		System.out.println("OK");
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
}
