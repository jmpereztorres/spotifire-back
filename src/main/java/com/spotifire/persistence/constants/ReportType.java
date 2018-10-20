package com.spotifire.persistence.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReportType {

	@JsonProperty("FIRE")
	FIRE,

	@JsonProperty("PREVENTION")
	PREVENTION;

}
