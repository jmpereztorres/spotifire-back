package com.spotifire.web.rest.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.spotifire.persistence.constants.SpotifireConstants;
import com.spotifire.web.rest.dto.GeolocationDTO;

@Service
public class GeoLocationClientManager implements IGeoLocationClientService {

	private static final Logger LOGGER = LogManager.getLogger(GeoLocationClientManager.class);

	@Autowired
	@Qualifier("nasaRestTemplate")
	private RestTemplate trustedRestTemplate;

	@Override
	public GeolocationDTO locateCoordenates(double latitude, double longitude) {

		GeolocationDTO res = null;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SpotifireConstants.GEOCODE_API_URL)
				.queryParam(SpotifireConstants.GEOCODE_API_FORMAT_PARAM, SpotifireConstants.JSON_FORMAT)
				.queryParam(SpotifireConstants.GEOCODE_API_LATITUDE_PARAM, latitude)
				.queryParam(SpotifireConstants.GEOCODE_API_LONGITUDE_PARAM, longitude);

		HttpEntity<GeolocationDTO> response = trustedRestTemplate.exchange(builder.toUriString(), HttpMethod.GET, null,
				GeolocationDTO.class);

		if (response != null) {
			res = response.getBody();
		}

		return res;
	}

}