package com.spotifire.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

}
