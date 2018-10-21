package com.spotifire.core.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ImageUtils {

	private static final Logger LOGGER = LogManager.getLogger(ImageUtils.class);

	private ImageUtils() {
		throw new IllegalAccessError("Utility class");
	}

	public static Integer scoringImage(byte[] image) {

		Integer score = null;

		ByteArrayInputStream bis = new ByteArrayInputStream(image);
		BufferedImage imageInput;
		try {
			imageInput = ImageIO.read(bis);

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

			score = analyzeFire(histogramReturn, width, height);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		System.out.println("Image score is: " + score);
		return score;
	}

	private static int analyzeFire(int histogram[][], int width, int height) {
		int meanHistogramRed = calculateHighMeanHistogram(histogram[0]);
		int meanHistogramGreen = calculateHighMeanHistogram(histogram[1]);
		int meanHistogramBlue = calculateHighMeanHistogram(histogram[2]);

		double numberOfPixels = (double) width * height;

		if ((numberOfPixels / 11000) > meanHistogramRed) {
			meanHistogramRed = 0;
		}

		return calculateConfidence(meanHistogramRed, meanHistogramGreen, meanHistogramBlue);
	}

	private static int calculateHighMeanHistogram(int histogram[]) {
		int meanCalculated = 0;

		for (int i = 192; i < 256; i++) {
			meanCalculated += histogram[i];
		}

		meanCalculated = meanCalculated / (256 - 192);

		return meanCalculated;
	}

	private static int calculateConfidence(int red, int green, int blue) {
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

}
