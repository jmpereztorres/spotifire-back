package com.spotifire.web.rest.dto;

import java.util.Date;

import com.spotifire.persistence.pojo.Location;

public class NasaFireDTO {

	private Date creationDate;

	private Location location;

	public NasaFireDTO() {
		super();
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
