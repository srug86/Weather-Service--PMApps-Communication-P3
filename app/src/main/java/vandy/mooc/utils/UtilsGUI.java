package vandy.mooc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.widget.Toast;

public class UtilsGUI {

	private static enum Months
	{
		January, February, March, April, May, Jun, 
		July, August, September, October, November, December
	}

	public static String getCurrentLocation(String locationName) {
		return locationName;
	}

	public static String getCurrentDate() {
		// Get the current date
		final Calendar c = Calendar.getInstance();
		int cMonth = c.get(Calendar.MONTH);
		int cDay = c.get(Calendar.DAY_OF_MONTH);

		return Months.values()[cMonth] + " " + cDay;
	}

	public static String getCelsiusTemperature(double celsius) {
		return round(celsius, 2) + " ºC";
	}

	public static String getFarhenheitTemperature(double fahrenheit) {
		return round(fahrenheit, 2) + " ºF";
	}

	public static String getWindSpeed(double ms, double deg) {
		String direction = "NaN";
		if (deg > -22.5 && deg <= 22.5) { direction = "N"; }
		else if (deg > 22.5 && deg <= 67.5) { direction = "NE"; }
		else if (deg > 67.5 && deg <= 112.5) { direction = "E"; }
		else if (deg > 112.5 && deg <= 157.5) { direction = "SE"; }
		else if (deg > 157.5 && deg <= 180) { direction = "S"; }
		else if (deg >= -180 && deg <= -157.5) { direction = "S"; }
		else if (deg > -157.5 && deg <= -112.5) { direction = "SW"; }
		else if (deg > -112.5 && deg <= -67.5) { direction = "W"; }
		else if (deg > -67.5 && deg <= -22.5) { direction = "NW"; }

		double kmh = ms * 3.6;
		return "Wind: " + round(kmh, 2) + " Km/h, " + direction;
	}

	public static String getHumidity(long humidity) {
		return "Humidity: " + humidity + " %";
	}

	public static String getSunrise(long sunrise) {
		return "Sunrise: " + ConvertUnixTimeToDate(sunrise);
	}

	public static String getSunset(long sunset) {
		return "Sunset: " + ConvertUnixTimeToDate(sunset);
	}

	/**
	 * Show a toast message.
	 */
	public static void showToast(Context context,
			String message) {
		Toast.makeText(context,
				message,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Ensure this class is only used as a utility.
	 */
	private UtilsGUI() {
		throw new AssertionError();
	} 

	private static String ConvertUnixTimeToDate(long unixSeconds) {
		Date date = new Date(unixSeconds*1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z", Locale.US);
		return sdf.format(date);
	}

	private static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
