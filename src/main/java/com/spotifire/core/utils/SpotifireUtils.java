package com.spotifire.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.spotifire.persistence.pojo.Location;
import com.spotifire.persistence.pojo.Report;

public final class SpotifireUtils {

	private static final Logger LOGGER = LogManager.getLogger(SpotifireUtils.class);

	private SpotifireUtils() {
		throw new IllegalAccessError("Utility class");
	}

	/**
	 * Get last item
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T> T getLastItem(T a, T b) {
		return b != null ? b : a;
	}

	/**
	 * Checks if a collection is not null or empty
	 *
	 * @param collection
	 * @return boolean
	 */
	public static boolean isNotNullNorEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}

	/**
	 * Call reflection method not to generate duplicated try catch blocks
	 *
	 * @param pojoFrom
	 * @param methodName
	 * @param valueToSet
	 * @param parametrizedArgs
	 * @return
	 */
	public static Object callReflectionMethod(Object pojoFrom, String methodName, Object valueToSet, Class<?>... parametrizedArgs) {
		Object res = null;

		try {
			// with arguments
			if (parametrizedArgs != null) {
				res = pojoFrom.getClass().getDeclaredMethod(methodName, parametrizedArgs).invoke(pojoFrom, valueToSet);

				// without arguments
			} else {
				res = pojoFrom.getClass().getDeclaredMethod(methodName).invoke(pojoFrom);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return res;
	}

	public static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

		final int R = 6371;

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // -> meters

		double height = el1 - el2;

		distance = Math.pow(distance, 2) + Math.pow(height, 2);

		return Math.sqrt(distance);
	}

	public static double distance(Location location1, Location location2) {
		return distance(location1.getLatitude(), location2.getLatitude(), location1.getLongitude(), location2.getLongitude(), 0d, 0d);
	}

	public static Date parseDateFromCsvFile(String fieldValue, String pattern) {
		Date res = null;

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		try {
			res = sdf.parse(fieldValue);
		} catch (java.text.ParseException e) {
			System.out.println(e.getMessage());
		}

		return res;
	}

	public static Location getCentralReportLocation(List<Report> reportList) {

		if (reportList.size() == 1) {
			return reportList.get(0).getLocation();
		}

		double x = 0;
		double y = 0;
		double z = 0;

		for (Report report : reportList) {

			double latitude = report.getLocation().getLatitude() * Math.PI / 180;
			double longitude = report.getLocation().getLongitude() * Math.PI / 180;

			x += Math.cos(latitude) * Math.cos(longitude);
			y += Math.cos(latitude) * Math.sin(longitude);
			z += Math.sin(latitude);
		}

		double total = reportList.size();

		x = x / total;
		y = y / total;
		z = z / total;

		double centralLongitude = Math.atan2(y, x);
		double centralSquareRoot = Math.sqrt(x * x + y * y);
		double centralLatitude = Math.atan2(z, centralSquareRoot);

		Location center = new Location();
		center.setLatitude(centralLatitude * 180 / Math.PI);
		center.setLongitude(centralLongitude * 180 / Math.PI);

		return center;
	}
}
