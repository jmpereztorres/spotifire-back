package com.spotifire.web.rest.client;

import java.net.URISyntaxException;

public interface IWeatherClientService {

	int fechtWeatherData(double latitude, double longitude, long timestamp) throws URISyntaxException;

}
