package com.spotifire.web.rest.dto;

import java.util.List;

import com.spotifire.persistence.pojo.Evidence;

public class FireDTO {

	private List<Evidence> evidences;

	private List<NasaFireDTO> nasaFires;

	public FireDTO() {
		super();
	}

	public List<Evidence> getEvidences() {
		return this.evidences;
	}

	public void setEvidences(List<Evidence> evidences) {
		this.evidences = evidences;
	}

	public List<NasaFireDTO> getNasaFires() {
		return this.nasaFires;
	}

	public void setNasaFires(List<NasaFireDTO> nasaFires) {
		this.nasaFires = nasaFires;
	}

}
