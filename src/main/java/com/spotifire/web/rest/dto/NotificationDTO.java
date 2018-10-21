package com.spotifire.web.rest.dto;

public class NotificationDTO {

	private String id;
	private double latitude;
	private double longitude;
	private double radius;
	private double transitionType;
	private String title;
	private String text;

	public NotificationDTO() {
		super();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getRadius() {
		return this.radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getTransitionType() {
		return this.transitionType;
	}

	public void setTransitionType(double transitionType) {
		this.transitionType = transitionType;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
