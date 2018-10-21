package com.spotifire.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeolocationDTO {

	private double latitude;

	private double longitude;

	@JsonProperty("display_name")
	private String displayName;

	private AddressDTO address;

	public GeolocationDTO() {
		super();
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public AddressDTO getAddress() {
		return address;
	}

	public void setAddress(AddressDTO address) {
		this.address = address;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeolocationDTO [latitude=").append(latitude).append(", longitude=").append(longitude).append(", displayName=")
				.append(displayName).append(", address=").append(address).append("]");
		return builder.toString();
	}

}
