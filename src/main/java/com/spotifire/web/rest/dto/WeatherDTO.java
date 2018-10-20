package com.spotifire.web.rest.dto;

public class WeatherDTO {
	private double latitude;
	private double longitude;
	private int offset;
	
	private CurrentlyWeatherDTO currently;
	
	public WeatherDTO() {
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

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public CurrentlyWeatherDTO getCurrently() {
		return currently;
	}

	public void setCurrently(CurrentlyWeatherDTO currently) {
		this.currently = currently;
	}

	@Override
	public String toString() {
		return "WeatherDTO [latitude=" + latitude + ", longitude=" + longitude + ", offset=" + offset + ", currently="
				+ currently + "]";
	}

}
