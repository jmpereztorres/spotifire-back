package com.spotifire.web.rest.dto;

public class CurrentlyWeatherDTO {
	private long time;
	private double precipIntensity;
	private double precipProbability;
	private String precipType;
	private double temperature;
	private double humidity;
	private double windSpeed;
	
	public CurrentlyWeatherDTO() {
		super();
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getPrecipIntensity() {
		return precipIntensity;
	}

	public void setPrecipIntensity(double precipIntensity) {
		this.precipIntensity = precipIntensity;
	}

	public double getPrecipProbability() {
		return precipProbability;
	}

	public void setPrecipProbability(double precipProbability) {
		this.precipProbability = precipProbability;
	}

	public String getPrecipType() {
		return precipType;
	}

	public void setPrecipType(String precipType) {
		this.precipType = precipType;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	@Override
	public String toString() {
		return "CurrentlyWeatherDTO [time=" + time + ", precipIntensity=" + precipIntensity + ", precipProbability="
				+ precipProbability + ", precipType=" + precipType + ", temperature=" + temperature + ", humidity="
				+ humidity + ", windSpeed=" + windSpeed + "]";
	}
	
}
