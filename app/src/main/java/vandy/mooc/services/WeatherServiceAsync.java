package vandy.mooc.services;

import java.util.List;

import vandy.mooc.aidl.WeatherData;
import vandy.mooc.aidl.WeatherRequest;
import vandy.mooc.aidl.WeatherResults;
import vandy.mooc.utils.UtilsNet;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class WeatherServiceAsync extends LifecycleLoggingService {

	public static Intent makeIntent(Context context) {
        return new Intent(context,
        		WeatherServiceAsync.class);
    }
	
	@Override
    public IBinder onBind(Intent intent) {
        return mWeatherRequestImpl;
    }
	
	private final WeatherRequest.Stub mWeatherRequestImpl =
	        new WeatherRequest.Stub() {
		
				@Override
				public void getCurrentWeather(String location,
						WeatherResults callback) throws RemoteException {

					final List<WeatherData> weatherResults = 
		                    UtilsNet.getResults(location);

		                if (weatherResults != null) {
		                    Log.d(TAG, "" 
		                          + weatherResults.size() 
		                          + " results for location: " 
		                          + location);

		                    callback.sendResults(weatherResults);
		                } else {
		                	// TODO sendError()
		                }
				}
		};
}
