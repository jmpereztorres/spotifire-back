
package com.spotifire.web.rest.dto;

import com.spotifire.persistence.constants.ReportType;

public class ReportRequestDTO {

	private Double latitude;

	private Double longitude;

	private String user;

	private String imageId;

	private String description;

	private ReportType type;

	/**
	 * Default constructor
	 */
	public ReportRequestDTO() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportRequestDTO [latitude=").append(this.latitude).append(", longitude=").append(this.longitude).append(", user=")
				.append(this.user).append(", image=").append(this.imageId).append(", description=").append(this.description)
				.append(", type=").append(this.type).append("]");
		return builder.toString();
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getImageId() {
		return this.imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ReportType getType() {
		return this.type;
	}

	public void setType(ReportType type) {
		this.type = type;
	}

}
