package com.spotifire.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressDTO {

	private String village;

	private String town;

	private String suburb;

	private String region;

	private String county;

	private String state;

	private String postcode;

	private String country;

	@JsonProperty("country_code")
	private String countryCode;

	public AddressDTO() {
		super();
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getSuburb() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AddressDTO [village=").append(village).append(", town=").append(town).append(", suburb=").append(suburb)
				.append(", region=").append(region).append(", county=").append(county).append(", state=").append(state)
				.append(", postcode=").append(postcode).append(", country=").append(country).append(", countryCode=").append(countryCode)
				.append("]");
		return builder.toString();
	}

}
