package com.spotifire.web.rest.client;

import java.net.URISyntaxException;

public interface IWeatherClientService {

	int fetchWeatherData(double latitude, double longitude, long timestamp) throws URISyntaxException;

}
