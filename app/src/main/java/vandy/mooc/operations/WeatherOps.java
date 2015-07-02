package vandy.mooc.operations;

import vandy.mooc.activities.MainActivity;
import vandy.mooc.aidl.WeatherData;

/**
 *	This class defines all the weather-related operations.
 */
public interface WeatherOps {
	/**
	 * Initiate the service binding protocol.
	 */
	public void bindService();
	
	/**
	 * Initiate the service unbinding protocol.
	 */
	public void unbindService();
	
	/**
	 * Initiate the synchronous weather lookup when the user presses
	 * the "Get Weather Sync" button.
	 */
	public void expandWeatherSync(String location);
	
	/**
	 * Initiate the asynchronous weather lookup when the user presses
	 * the "Get Weather Async" button.
	 */
	public void expandWeatherAsync(String location);
	
	/**
	 * Called after a runtime configuration change occurs to finish
	 * the initialization steps.
	 */
	public void onConfigurationChange(MainActivity activity);
	
	/**
	 * Show the weather result
	 */
	public void showResult(WeatherData result);
}
