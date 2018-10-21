package com.spotifire.persistence.constants;

public class SpotifireConstants {

	private SpotifireConstants() {
	}

	// Reflection
	public static final String METHOD_GET_ID = "getId";
	public static final String METHOD_GET = "get";
	public static final String METHOD_SET = "set";

	// REST
	public static final String REST_ACCEPT_APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";
	public static final String REST_ACCEPT_APPLICATION_XML_UTF_8 = "application/xml;charset=UTF-8";
	public static final String REST_ACCEPT_APPLICATION_MULTIPART_MIXED = "multipart/mixed";
	public static final String REST_ACCEPT_APPLICATION_MULTIPART_FORM_DATA = "multipart/form-data";

	public static final String BOOLEAN_TRUE = "true";

	public static final String OPERATOR_COMMA = ",";
	public static final String REGEX_DOT = "\\.";

	// APIS
	public static final String GEOCODE_API_URL = "https://nominatim.openstreetmap.org/reverse";
	public static final String GEOCODE_API_FORMAT_PARAM = "format";
	public static final String JSON_FORMAT = "json";
	public static final String GEOCODE_API_LATITUDE_PARAM = "lat";
	public static final String GEOCODE_API_LONGITUDE_PARAM = "lon";

	// twitter
	public static final String TWEET_MESSAGE = "Se ha detectado detectado un posible incendio cerca de %s (%s). Por favor, ayúdanos a confirmarlo utilizando el hashtag #Spotifire con tu localización y una foto o desde nuestra app Spotifire";
	public static final String SPOTIFIRE_TWITTER_HASHTAG = "#spotifire";

	// NASA FIRMS
	public static final String NASA_FIRMS_URL = "https://firms.modaps.eosdis.nasa.gov/data/active_fire/c6/csv/MODIS_C6_Europe_24h.csv";
	public static final String NASA_FIRMS_DATE_PATTERN = "yyyy-MM-ddhhmm";

	// WEATHER DARKSKY FORECAST
	public static final String WEATHER_DARKSKY_FORECAST_BASE_URL = "https://api.darksky.net/forecast/f0882429cb4afe2fb72984e427e6789f/";
	public static final String WEATHER_DARKSKY_FORECAST_BASE_URL_EXTRA_PARAMS = "?units=si&exclude=flags?exclude=alerts?exclude=minutely?exclude=hourly?exclude=daily";
}
