package com.spotifire.persistence.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AlertLevel {

	@JsonProperty("FIRE")
	FIRE,

	@JsonProperty("ALERT")
	ALERT;

}
