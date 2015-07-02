package vandy.mooc.jsonweather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of JsonWeather objects that contain this data.
 */
public class WeatherJSONParser {
	/**
	 * Used for logging purposes.
	 */
	private final String TAG =
			this.getClass().getCanonicalName();

	/**
	 * Parse the @a inputStream and convert it into a List of JsonWeather
	 * objects.
	 */
	public List<JsonWeather> parseJsonStream(InputStream inputStream)
			throws IOException {
		// @@ Done -- you fill in here.
		try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"))) {
			return parseJsonWeatherArray(reader);
		}
	}

	/**
	 * Parse a Json stream and convert it into a List of JsonWeather
	 * objects.
	 */
	public List<JsonWeather> parseJsonWeatherArray(JsonReader reader)
			throws IOException {
		// @@ Done -- you fill in here.
		Log.d(TAG, "parseJsonWeatherArray()");
		
		List<JsonWeather> jsonWeathers = new ArrayList<JsonWeather>();
		jsonWeathers.add(parseJsonWeather(reader));

//		reader.beginArray();
//		try {
//			while (reader.hasNext()) {
//				jsonWeathers.add(parseJsonWeather(reader));		
//			}
//		}
//		finally {
//			reader.endArray();
//		}

		return jsonWeathers;
	}

	/**
	 * Parse a Json stream and return a JsonWeather object.
	 */
	public JsonWeather parseJsonWeather(JsonReader reader) 
			throws IOException {
		// @@ Done -- you fill in here.
		Log.d(TAG, "parseJsonWeather()");
		
		Sys sys = null;
		List<Weather> weathers = null;
		String base = null, name = null;
		Main main = null;
		Wind wind = null;
		long dt = 0, id = 0, cod = 0;

		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals(JsonWeather.sys_JSON)) { 			// "sys"
				sys = parseSys(reader);
			} else if (key.equals(JsonWeather.weather_JSON) 
					&& reader.peek() != JsonToken.NULL) {		// "weather"
				weathers = parseWeathers(reader);
			} else if (key.equals(JsonWeather.base_JSON)) { 	// "base"
				base = reader.nextString();
			} else if (key.equals(JsonWeather.main_JSON)) { 	// "main"
				main = parseMain(reader);
			} else if (key.equals(JsonWeather.wind_JSON)) { 	// "wind"
				wind = parseWind(reader);
			} else if (key.equals(JsonWeather.dt_JSON)) {		// "dt"
				dt = reader.nextLong();
			} else if (key.equals(JsonWeather.id_JSON)) { 		// "id"
				id = reader.nextLong();
			} else if (key.equals(JsonWeather.name_JSON)) { 	// "name"
				name = reader.nextString();
			} else if (key.equals(JsonWeather.cod_JSON)) { 		// "cod"
				cod = reader.nextLong();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();

		return new JsonWeather(sys, base, main, weathers, wind, dt, id, name, cod);
	}

	/**
	 * Parse a Json stream and return a List of Weather objects.
	 */
	public List<Weather> parseWeathers(JsonReader reader) throws IOException {
		// @@ Done -- you fill in here.
		Log.d(TAG, "parseWeathers()");
		
		List<Weather> weathers = new ArrayList<Weather>();

		reader.beginArray();
		while (reader.hasNext()) {
			weathers.add(parseWeather(reader));
		}
		reader.endArray();

		return weathers;
	}

	/**
	 * Parse a Json stream and return a Weather object.
	 */
	public Weather parseWeather(JsonReader reader) throws IOException {
		// @@ Done -- you fill in here.
		Log.d(TAG, "parseWeather()");
		
		Weather weather = new Weather();

		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals(Weather.id_JSON)) {					// "id"
				weather.setId(reader.nextLong());
			} else if (key.equals(Weather.main_JSON)) { 		// "main"
				weather.setMain(reader.nextString());
			} else if (key.equals(Weather.description_JSON)) {	// "description"
				weather.setDescription(reader.nextString());
			} else if (key.equals(Weather.icon_JSON)) {			// "icon"
				weather.setIcon(reader.nextString());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();

		return weather;
	}

	/**
	 * Parse a Json stream and return a Main Object.
	 */
	public Main parseMain(JsonReader reader) 
			throws IOException {
		// @@ Done -- you fill in here.
		Log.d(TAG, "parseMain()");
		
		Main main = new Main();

		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals(Main.temp_JSON)) {				// "temp"
				main.setTemp(reader.nextDouble());
			} else if (key.equals(Main.pressure_JSON)) { 	// "pressure"
				main.setPressure(reader.nextDouble());
			} else if (key.equals(Main.humidity_JSON)) {	// "humidity"
				main.setHumidity(reader.nextLong());
			} else if (key.equals(Main.tempMin_JSON)) {		// "temp_min"
				main.setTempMin(reader.nextDouble());
			} else if (key.equals(Main.tempMax_JSON)) {		// "temp_max"
				main.setTempMax(reader.nextDouble());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();

		return main;
	}

	/**
	 * Parse a Json stream and return a Wind Object.
	 */
	public Wind parseWind(JsonReader reader) throws IOException {
		// @@ Done -- you fill in here.    
		Log.d(TAG, "parseWind()");
		
		Wind wind = new Wind();

		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals(Wind.speed_JSON)) {		// "speed"
				wind.setSpeed(reader.nextDouble());
			} else if (key.equals(Wind.deg_JSON)) {	// "deg"
				wind.setDeg(reader.nextDouble());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();

		return wind;
	}

	/**
	 * Parse a Json stream and return a Sys Object.
	 * @throws IOException 
	 */
	public Sys parseSys(JsonReader reader) throws IOException {
		// @@ Done -- you fill in here.
		Log.d(TAG, "parseSys()");
		
		Sys sys = new Sys();

		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals(Sys.message_JSON)) {			// "message"
				sys.setMessage(reader.nextDouble());
			} else if (key.equals(Sys.country_JSON)) {	// "country"
				sys.setCountry(reader.nextString());
			} else if (key.equals(Sys.sunrise_JSON)) {	// "sunrise"
				sys.setSunrise(reader.nextLong());
			} else if (key.equals(Sys.sunset_JSON)) {	// "sunset"
				sys.setSunset(reader.nextLong());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();

		return sys;
	}
}
