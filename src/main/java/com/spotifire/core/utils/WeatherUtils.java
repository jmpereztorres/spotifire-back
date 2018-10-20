package com.spotifire.core.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.spotifire.web.rest.dto.WeatherDTO;

public final class WeatherUtils {

	private static final Logger LOGGER = LogManager.getLogger(WeatherUtils.class);

	private WeatherUtils() {
		throw new IllegalAccessError("Utility class");
	}

	public Integer scoringWeather(double latitude, double longitude, long timeStamp) throws URISyntaxException {
		// https://api.darksky.net/forecast/f0882429cb4afe2fb72984e427e6789f/37.035229,-6.435476,1499860800?units=si&exclude=flags?exclude=alerts?exclude=minutely

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

		int riskFactorInt = riskFactor * 100;

		return riskFactorInt;
	}
}
