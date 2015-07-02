package vandy.mooc.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import vandy.mooc.aidl.WeatherData;
import vandy.mooc.jsonweather.JsonWeather;
import vandy.mooc.jsonweather.Main;
import vandy.mooc.jsonweather.Sys;
import vandy.mooc.jsonweather.WeatherJSONParser;
import vandy.mooc.jsonweather.Wind;

public class UtilsNet {

	/**
     * Logging tag used by the debugger. 
     */
    private final static String TAG = UtilsNet.class.getCanonicalName();
    
    /** 
     * URL to the Weather web service.
     */
    private final static String sWeather_Web_Service_URL =
        "http://api.openweathermap.org/data/2.5/weather?q=";

    /**
     * Obtain the Weather information.
     * 
     * @return The information that responds to your current location search.
     */
    public static List<WeatherData> getResults(final String location) {
    	Log.d(TAG, "getResults(location:" + location + ")");
    	
    	final List<WeatherData> returnList =
    			new ArrayList<WeatherData>();
    	
    	List<JsonWeather> jsonWeathers = null;
    	
    	try {
    		final URL url = new URL(sWeather_Web_Service_URL + location);
    		
    		HttpURLConnection urlConnection =
                (HttpURLConnection) url.openConnection();
		
		 	try (InputStream inputStream =
                 new BufferedInputStream(urlConnection.getInputStream())) {
                 // Create the parser.
                 final WeatherJSONParser parser = new WeatherJSONParser();

                 jsonWeathers = parser.parseJsonStream(inputStream);
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    	// See if we parsed any valid data.
        if (jsonWeathers != null && jsonWeathers.size() > 0) {
            for (JsonWeather jsonWeather : jsonWeathers) {
            	Wind wind = jsonWeather.getWind();
            	Main main = jsonWeather.getMain();
            	Sys sys = jsonWeather.getSys();
                returnList.add(
                		new WeatherData(
                				jsonWeather.getName(),
                				wind.getSpeed(),
                				wind.getDeg(),
                				main.getTemp(),
                				main.getHumidity(),
                				sys.getSunrise(),
                				sys.getSunset()));
            }
            return returnList;
        }  else {
            return null;
    	}
    }
}
