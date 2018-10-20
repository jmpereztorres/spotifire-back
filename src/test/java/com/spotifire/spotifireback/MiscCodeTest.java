package com.spotifire.spotifireback;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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

	@Test
	public void scoringImage() {

		System.out.println("Testing scoringImage...");
		LOGGER.debug("EEEEES");
		System.out.println("Test Init");

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
