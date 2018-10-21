package com.spotifire.persistence.constants;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SourceType {

	@JsonProperty("TWITTER")
	TWITTER,

	@JsonProperty("SPOTIFIRE")
	SPOTIFIRE,

	@JsonProperty("NASA")
	NASA;

}
