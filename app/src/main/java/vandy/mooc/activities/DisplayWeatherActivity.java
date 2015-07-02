package vandy.mooc.activities;

import vandy.mooc.R;
import vandy.mooc.aidl.WeatherData;
import vandy.mooc.utils.UtilsGUI;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayWeatherActivity extends LifecycleLoggingActivity {

	/**
	 * Constants
	 */
	private final static String LOCATION_NAME = "vandy.mooc.activities.weather.locationname";
	private final static String WEATHER_TEMPERATURE = "vandy.mooc.activities.weather.temperature";
	private final static String MAIN_HUMIDITY = "vandy.mooc.activities.weather.humidity";
	private final static String WIND_SPEED = "vandy.mooc.activities.weather.speed";
	private final static String WIND_DEG = "vandy.mooc.activities.weather.deg";
	private final static String SYS_SUNRISE = "vandy.mooc.activities.weather.sunrise";
	private final static String SYS_SUNSET = "vandy.mooc.activities.weather.sunset";
	
	/**
	 * Widgets
	 */
	protected TextView textViewLocationName, textViewDate, textViewWind,
		textViewCelsius, textViewFahrenheit, textViewHumidity,
		textViewSunrise, textViewSunset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_activity);
		
		textViewLocationName = (TextView) findViewById(R.id.textViewLocation);
		textViewDate = (TextView) findViewById(R.id.textViewDate);
		textViewWind = (TextView) findViewById(R.id.textViewWind);
		textViewCelsius = (TextView) findViewById(R.id.textViewCelsius);
		textViewFahrenheit = (TextView) findViewById(R.id.textViewFahrenheit);
		textViewHumidity = (TextView) findViewById(R.id.textViewHumidity);
		textViewSunrise = (TextView) findViewById(R.id.textViewSunrise);
		textViewSunset = (TextView) findViewById(R.id.textViewSunset);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		textViewLocationName.setText(UtilsGUI.getCurrentLocation(bundle.getString(LOCATION_NAME)));
		textViewDate.setText(UtilsGUI.getCurrentDate());
		textViewWind.setText(UtilsGUI.getWindSpeed(bundle.getDouble(WIND_SPEED), bundle.getDouble(WIND_DEG)));
		double kelvin = bundle.getDouble(WEATHER_TEMPERATURE);
		double celsius = kelvin - 272.15;
		double fahrenheit = celsius * 1.8 + 32;
		textViewFahrenheit.setText(UtilsGUI.getFarhenheitTemperature(fahrenheit));
		textViewCelsius.setText(UtilsGUI.getCelsiusTemperature(celsius));
		textViewHumidity.setText(UtilsGUI.getHumidity(bundle.getLong(MAIN_HUMIDITY)));
		textViewSunrise.setText(UtilsGUI.getSunrise(bundle.getLong(SYS_SUNRISE)));
		textViewSunset.setText(UtilsGUI.getSunset(bundle.getLong(SYS_SUNSET)));
	}

	public static Intent makeIntent(Context context, WeatherData weatherData) {
		Intent intent = new Intent(context, DisplayWeatherActivity.class);
		intent.putExtra(LOCATION_NAME, weatherData.mName);
		intent.putExtra(WIND_SPEED, weatherData.mSpeed);
		intent.putExtra(WIND_DEG, weatherData.mDeg);
		intent.putExtra(WEATHER_TEMPERATURE, weatherData.mTemp);
		intent.putExtra(MAIN_HUMIDITY, weatherData.mHumidity);
		intent.putExtra(SYS_SUNRISE, weatherData.mSunrise);
		intent.putExtra(SYS_SUNSET, weatherData.mSunset);
		
		return intent;
	}
}
