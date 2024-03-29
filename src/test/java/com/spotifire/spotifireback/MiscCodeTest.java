package com.spotifire.spotifireback;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.spotifire.config.EmptyConfig;
import com.spotifire.core.utils.SpotifireUtils;
import com.spotifire.persistence.constants.ReportType;
import com.spotifire.persistence.constants.SourceType;
import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;
import com.spotifire.web.rest.dto.GeolocationDTO;
import com.spotifire.web.rest.dto.WeatherDTO;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

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
	@Rollback(false)
	public void publishTweet() throws RestClientException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("l3socwjpwuFbis9sDX56PIIxP", "5M8o0Qqj6AWLN2ZBp1LORrCBXziyfUYVgW8WWNky8vwyuDa1gP");
		twitter.setOAuthAccessToken(
				new AccessToken("225173447-OfaoIwrdiBx99UZf3r4vrfFZpZbZSxXMSDUYexTi", "U5w21beQoLfSQxb9Zb2fvECFAN8o7jDvRg4FtLABNZFdb"));

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SpotifireConstants.GEOCODE_API_URL)
				.queryParam(SpotifireConstants.GEOCODE_API_FORMAT_PARAM, SpotifireConstants.JSON_FORMAT)
				.queryParam(SpotifireConstants.GEOCODE_API_LATITUDE_PARAM, 39.420143)
				.queryParam(SpotifireConstants.GEOCODE_API_LONGITUDE_PARAM, -0.457176);

		HttpEntity<GeolocationDTO> response = this.getNasaRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, null,
				GeolocationDTO.class);

		if (response != null) {

			GeolocationDTO geolocation = response.getBody();

			String tweet = String.format(SpotifireConstants.TWEET_MESSAGE,
					geolocation.getAddress().getVillage() != null ? geolocation.getAddress().getVillage()
							: geolocation.getAddress().getTown(),

					geolocation.getAddress().getCounty());
			try {
				StatusUpdate statusTweet = new StatusUpdate(tweet);
				twitter.updateStatus(statusTweet);
			} catch (TwitterException e) {
				System.err.println(e.getMessage());
			}
		}

	}

	@Test
	public void testSatelliteData() throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		System.out.println("Testing testSatelliteData...");
		LOGGER.debug("EEEEES");
		System.out.println("Test Init");
		URI uriPetition = new URI("https://firms.modaps.eosdis.nasa.gov/data/active_fire/c6/csv/MODIS_C6_Europe_24h.csv");
		RestTemplate nasaRestTemplate = this.getNasaRestTemplate();
		ResponseEntity<String> response = nasaRestTemplate.exchange(uriPetition, HttpMethod.GET, null, String.class);

		List<Report> reportList = new ArrayList<>();
		String[] lines = response.getBody().split("\n");

		if (lines != null) {
			Arrays.asList(lines).forEach(line -> {
				String[] columns = line.split(",");
				Report report = new Report();
				if (columns != null) {
					report.setCreationDate(parseDateFromCsvFile(columns[5] + columns[6], "yyyy-MM-ddhhmm"));
					report.setType(ReportType.FIRE);
					report.setScore(this.scoringSatelliteData(Double.valueOf(columns[2]), Double.valueOf(columns[10]),
							Double.valueOf(columns[11])));

					report.setLocation(new Location(Double.valueOf(columns[0]), Double.valueOf(columns[1])));
				}
				reportList.add(report);
			});
		}
		System.out.println("Test End");
	}

	private static Date parseDateFromCsvFile(String fieldValue, String pattern) {
		Date res = null;

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		try {
			res = sdf.parse(fieldValue);
		} catch (java.text.ParseException e) {
			System.out.println(e.getMessage());
		}

		return res;
	}

	private RestTemplate getNasaRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (chain, authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);
	}

	private int scoringSatelliteData(double brightness1, double brightness2, double power) {
		System.out.println("Testing scoringSatelliteData...");
		LOGGER.debug("EEEEES");
		System.out.println("Test Init");

		double maxBrightness1 = 503.2;
		double minBrightness1 = 300.0;
		double maxBrightness2 = 400.1;
		double minBrightness2 = 264.9;
		double maxPower = 400.1;
		double minPower = 2;
		double coeficienBrightness1 = 0.3045;
		double coeficienBrightness2 = 0.1297;
		double coeficientPower = 0.1747;

		double calculationBrightness1 = this.calculateCuadraticMinimum(brightness1, maxBrightness1, minBrightness1) * coeficienBrightness1;
		double calculationBrightness2 = this.calculateCuadraticMinimum(brightness2, maxBrightness2, minBrightness2) * coeficienBrightness2;
		double calculationPower = this.calculateCuadraticMinimum(power, maxPower, minPower) * coeficientPower;

		int result = (int) (calculationBrightness1 + calculationBrightness2 + calculationPower) * 100;

		System.out.printf("Scoring satellite data: %d \n", result);

		return result;
	}

	private double calculateCuadraticMinimum(double input, double maximum, double minimum) {
		return (input - minimum) / (maximum - minimum);
	}

	/**
	 * @throws URISyntaxException
	 */
	@Test
	public void testAPIWeather() throws URISyntaxException {
		// https://api.darksky.net/forecast/f0882429cb4afe2fb72984e427e6789f/37.035229,-6.435476,1499860800?units=si&exclude=flags?exclude=alerts?exclude=minutely
		System.out.println("Testing testAPIWeather...");
		System.out.println("Test Init");
		double latitude = 37.035229f;
		double longitude = -6.435476f;
		long timeStamp = (new Date().getTime()) / 1000;

		StringBuilder sb = new StringBuilder();
		sb.append("https://api.darksky.net/forecast/f0882429cb4afe2fb72984e427e6789f/").append(latitude).append(",").append(longitude)
				.append(",").append(timeStamp)
				.append("?units=si&exclude=flags?exclude=alerts?exclude=minutely?exclude=hourly?exclude=daily");
		URI uriPetition = new URI(sb.toString());
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<WeatherDTO> response = restTemplate.exchange(uriPetition, HttpMethod.GET, null, WeatherDTO.class);

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

		int riskFactorInt = riskFactor * 100;

		System.out.printf("riskFactor: %d \n", riskFactorInt);

		System.out.println("Test End");
	}

	@Test
	public void scoringImage() {

		System.out.println("Testing scoringImage...");

		BufferedImage imageInput = null;
		File input_file = null;

		try {
			input_file = new File("C:/Users/Carlos/Desktop/imagenes/llamas/fotoTwitter.jpg");
			imageInput = ImageIO.read(input_file);
			System.out.println("Reading complete.");
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}

		int width = imageInput.getWidth();
		int height = imageInput.getHeight();

		BufferedImage imageOutputRed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage imageOutputGreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage imageOutputBlue = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		int histogramReturn[][] = new int[3][256];

		int pixel = 0;
		int pixelRed = 0;
		int pixelGreen = 0;
		int pixelBlue = 0;
		int red = 0;
		int green = 0;
		int blue = 0;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixelRed = 0;
				pixelGreen = 0;
				pixelBlue = 0;
				pixel = imageInput.getRGB(i, j);
				red = (pixel >> 16) & 0xFF;
				green = (pixel >> 8) & 0xFF;
				blue = pixel & 0xFF;

				pixelRed = (red << 16);
				pixelGreen = (green << 8);
				pixelBlue = blue;

				imageOutputRed.setRGB(i, j, pixelRed);
				imageOutputGreen.setRGB(i, j, pixelGreen);
				imageOutputBlue.setRGB(i, j, pixelBlue);

				histogramReturn[0][red] += 1;
				histogramReturn[1][green] += 1;
				histogramReturn[2][blue] += 1;
			}
		}

		try {
			File outputfileRed = new File("imageTwitterRed.jpg");
			ImageIO.write(imageOutputRed, "jpg", outputfileRed);
		} catch (IOException e) {

		}

		try {
			File outputfileGreen = new File("imageTwitterGreen.jpg");
			ImageIO.write(imageOutputGreen, "jpg", outputfileGreen);
		} catch (IOException e) {

		}

		try {
			File outputfileBlue = new File("imageTwitterBlue.jpg");
			ImageIO.write(imageOutputBlue, "jpg", outputfileBlue);
		} catch (IOException e) {

		}

		int confidence = this.analyzeFire(histogramReturn, width, height);

		System.out.println("Histogram Red.");

		for (int i = 0; i < 256; i++) {
			System.out.printf("%d : %d \n", i, histogramReturn[0][i]);
		}

		System.out.println("Histogram Green.");

		for (int i = 0; i < 256; i++) {
			System.out.printf("%d : %d \n", i, histogramReturn[1][i]);
		}

		System.out.println("Histogram Blue.");

		for (int i = 0; i < 256; i++) {
			System.out.printf("%d : %d \n", i, histogramReturn[2][i]);
		}

		System.out.printf("Confidence of the image being of a fire: %d \n", confidence);
		System.out.println("Test OK");
	}

	private int analyzeFire(int histogram[][], int width, int height) {
		int meanHistogramRed = this.calculateHighMeanHistogram(histogram[0], width, height);
		int meanHistogramGreen = this.calculateHighMeanHistogram(histogram[1], width, height);
		int meanHistogramBlue = this.calculateHighMeanHistogram(histogram[2], width, height);

		double numberOfPixels = width * height;

		System.out.printf("Histogram Red mean %d \n", meanHistogramRed);

		if ((numberOfPixels / 11000) > meanHistogramRed) {
			meanHistogramRed = 0;
		}

		System.out.printf("Histogram Red mean %d \n", meanHistogramRed);
		System.out.printf("Histogram Green mean %d \n", meanHistogramGreen);
		System.out.printf("Histogram Blue mean %d \n", meanHistogramBlue);

		int confidence = this.calculateConfidence(meanHistogramRed, meanHistogramGreen, meanHistogramBlue);

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
				if (difference < 0.9) {
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

	private static Twitter getTwitterClient() {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("l3socwjpwuFbis9sDX56PIIxP", "5M8o0Qqj6AWLN2ZBp1LORrCBXziyfUYVgW8WWNky8vwyuDa1gP");
		twitter.setOAuthAccessToken(
				new AccessToken("225173447-OfaoIwrdiBx99UZf3r4vrfFZpZbZSxXMSDUYexTi", "U5w21beQoLfSQxb9Zb2fvECFAN8o7jDvRg4FtLABNZFdb"));
		return twitter;
	}

	@Test
	public void fetchTwitter() {

		Twitter twitter = getTwitterClient();

		Query query = new Query();
		query.setQuery(SpotifireConstants.SPOTIFIRE_TWITTER_HASHTAG);
		query.setCount(100);
		List<Status> statuses = null;
		try {
			QueryResult queryResult = twitter.search(query);
			statuses = queryResult.getTweets();
			statuses.stream().filter(tweet -> Double.compare(tweet.getUser().getId(), 1053697168136122368L) != 0).forEach(tweet -> {
				Report report = new Report();
				report.setTwitterId(tweet.getId());

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

			});

		} catch (Exception e) {
		}
	}

	@Test
	public void testCentralLongitude() {

		Report r1 = new Report();
		Report r2 = new Report();
		Report r3 = new Report();
		Report r4 = new Report();
		Report r5 = new Report();

		Location l1 = new Location();
		l1.setLatitude(42.9336785);
		l1.setLongitude(-72.2272968);
		r1.setLocation(l1);

		Location l2 = new Location();
		l2.setLatitude(42.9185950);
		l2.setLongitude(-72.2249794);
		r2.setLocation(l2);

		Location l3 = new Location();
		l3.setLatitude(42.9180293);
		l3.setLongitude(-72.2531748);
		r3.setLocation(l3);

		Location l4 = new Location();
		l4.setLatitude(42.9335843);
		l4.setLongitude(-72.2560072);
		r4.setLocation(l4);

		Location l5 = new Location();
		l5.setLatitude(42.9337728);
		l5.setLongitude(-72.2284555);
		r5.setLocation(l5);

		List<Report> reportList = new ArrayList<>();
		reportList.add(r1);
		reportList.add(r2);
		reportList.add(r3);
		reportList.add(r4);
		reportList.add(r5);

		Location center = SpotifireUtils.getCentralReportLocation(reportList);

		System.out.println(center.getLatitude());
		System.out.println(center.getLongitude());

	}

}
