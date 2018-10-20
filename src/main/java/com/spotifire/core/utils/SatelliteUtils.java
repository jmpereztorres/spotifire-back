package com.spotifire.core.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.spotifire.persistence.constants.ReportType;
import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;

public final class SatelliteUtils {

	private static final Logger LOGGER = LogManager.getLogger(SatelliteUtils.class);

	private SatelliteUtils() {
		throw new IllegalAccessError("Utility class");
	}

	public void checkSatelliteData()
			throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		URI uriPetition = new URI(
				"https://firms.modaps.eosdis.nasa.gov/data/active_fire/c6/csv/MODIS_C6_Europe_24h.csv");
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
					report.setScore(scoringSatelliteData(Double.valueOf(columns[2]), Double.valueOf(columns[10]),
							Double.valueOf(columns[11])));
					report.setLocation(new Location(Double.valueOf(columns[0]), Double.valueOf(columns[1])));
				}
				reportList.add(report);
			});
		}
		System.out.println("Test End");
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

		double calculationBrightness1 = calculateCuadraticMinimum(brightness1, maxBrightness1, minBrightness1)
				* coeficienBrightness1;
		double calculationBrightness2 = calculateCuadraticMinimum(brightness2, maxBrightness2, minBrightness2)
				* coeficienBrightness2;
		double calculationPower = calculateCuadraticMinimum(power, maxPower, minPower) * coeficientPower;

		int result = (int) (calculationBrightness1 + calculationBrightness2 + calculationPower) * 100;

		System.out.printf("Scoring satellite data: %d \n", result);

		return result;
	}

	private double calculateCuadraticMinimum(double input, double maximum, double minimum) {
		return (input - minimum) / (maximum - minimum);
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

	private RestTemplate getNasaRestTemplate()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (chain, authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);
	}
}
