package com.spotifire.spotifireback;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

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
import com.spotifire.web.rest.dto.WeatherDTO;

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
		ResponseEntity<WeatherDTO> response = restTemplate.exchange(uriPetition, HttpMethod.GET, null, WeatherDTO.class);
		
		System.out.println(response.getStatusCode() + ":" + response.getBody());

		System.out.println("Test End");
	}

	@Test
	public void compendiumScoring() {

		System.out.println("Testing compendiumScoring...");
		LOGGER.debug("EEEEES");
		System.out.println("Test Init");

		float latitude = 0;
		float longitude = 0;
		int totalConfidence = 0;
		int confidenceImage = 0;
		int confidenceNASAStellite = 0;
		int confidenceTwitterInfo = 0;

		confidenceImage = scoringImage("C:/Users/Carlos/Desktop/imagenes/no llamas/no_llamas_1.jpg");
		System.out.printf("Confidence of the image being of a fire: %d \n", confidenceImage);

		confidenceNASAStellite = sconringSatellite(latitude, longitude);
		System.out.printf("Confidence of the info provided by NASA satellites: %d \n", confidenceNASAStellite);

		confidenceTwitterInfo = scoringTwitterInfo(latitude, longitude);
		System.out.printf("Confidence of the info provided by Twitter: %d \n", confidenceTwitterInfo);

		totalConfidence = (int) ((int) (confidenceImage * 0.3)) + ((int) (confidenceNASAStellite * 0.4))
				+ ((int) (confidenceTwitterInfo * 0.4));
		System.out.printf("Total confidence: %d \n", totalConfidence);
	}

	private int scoringTwitterInfo(float latitude, float longitude) {
		int confidence = 0;

		return confidence;
	}

	private int sconringSatellite(float latitude, float longitude) {
		int confidence = 0;

		return confidence;
	}

	private int scoringImage(String inputPath) {
		BufferedImage imageInput = null;
		File input_file = null;

		try {
			input_file = new File(inputPath);
			imageInput = ImageIO.read(input_file);
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

		return confidence;
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
