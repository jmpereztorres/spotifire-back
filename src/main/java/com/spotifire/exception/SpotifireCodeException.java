package com.spotifire.exception;

public enum SpotifireCodeException {

	CAUSED_BY("error.CAUSED_BY"), SESSION_EXPIRED("error.SESSION_EXPIRED"), NULL_DATA("error.NULL_DATA");

	private String code;

	public String getCode() {
		return code;
	}

	private SpotifireCodeException(String code) {
		this.code = code;
	}

}