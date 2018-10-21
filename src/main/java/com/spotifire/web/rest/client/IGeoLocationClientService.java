package com.spotifire.web.rest.client;

import com.spotifire.web.rest.dto.GeolocationDTO;

public interface IGeoLocationClientService {

	GeolocationDTO locateCoordenates(double latitude, double longitude);

}
