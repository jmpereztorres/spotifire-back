package com.spotifire.web.rest.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.web.rest.dto.WeatherDTO;

@Service
public class WeatherDarkskyForecastClientManager implements IWeatherClientService {

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Override
	public int fetchWeatherData(double latitude, double longitude, long timestamp) throws URISyntaxException {

		StringBuilder sb = new StringBuilder();
		sb.append(SpotifireConstants.WEATHER_DARKSKY_FORECAST_BASE_URL).append(latitude).append(SpotifireConstants.OPERATOR_COMMA)
				.append(longitude).append(SpotifireConstants.OPERATOR_COMMA).append(timestamp)
				.append(SpotifireConstants.WEATHER_DARKSKY_FORECAST_BASE_URL_EXTRA_PARAMS);
		URI uriPetition = new URI(sb.toString());
		ResponseEntity<WeatherDTO> response = this.restTemplate.exchange(uriPetition, HttpMethod.GET, null, WeatherDTO.class);

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

		return riskFactor * 100;

	}
}
