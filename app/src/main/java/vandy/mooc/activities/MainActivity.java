package vandy.mooc.activities;

import java.util.List;

import vandy.mooc.R;
import vandy.mooc.aidl.WeatherData;
import vandy.mooc.operations.WeatherOps;
import vandy.mooc.operations.WeatherOpsImp;
import vandy.mooc.utils.RetainedFragmentManager;
import vandy.mooc.utils.UtilsGUI;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends LifecycleLoggingActivity {
	/**
	 * Used to retain the WeatherOps state between runtime configuration
	 * changes.
	 */
	protected final RetainedFragmentManager mRetainedFragmentManager = 
			new RetainedFragmentManager(this.getFragmentManager(),
					TAG);

	/**
	 * Provides weather-related operations.
	 */
	private WeatherOps mWeatherOps;
	
	/**
	 * Location entered by the user.
	 */
	protected EditText mEditText;

	/**
	 * Hook method called when a new instance of Activity is created.
	 * One time initialization code goes here, e.g., runtime
	 * configuration changes.
	 *
	 * @param Bundle object that contains saved state information.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Always call super class for necessary
		// initialization/implementation.
		super.onCreate(savedInstanceState);

		// Get references to the UI components.
		setContentView(R.layout.main_activity);

		// Store the EditText that holds the locations entered by the user (if any).
		mEditText = (EditText) findViewById(R.id.editTextLocation);

		// Handle any configuration change.
		handleConfigurationChanges();
	}

	/**
	 * Hook method called by Android when this Activity is
	 * destroyed.
	 */
	@Override
	protected void onDestroy() {
		// Unbind from the Service.
		mWeatherOps.unbindService();

		// Always call super class for necessary operations when an
		// Activity is destroyed.
		super.onDestroy();
	}

	/**
	 * Handle hardware reconfigurations, such as rotating the display.
	 */
	protected void handleConfigurationChanges() {
		// If this method returns true then this is the first time the
		// Activity has been created.
		if (mRetainedFragmentManager.firstTimeIn()) {
			Log.d(TAG, "First time onCreate() call");

			// Create the WeatherOps object one time.  The "true"
			// parameter instructs WeatherOps to use the
			// BoundService.
			mWeatherOps = new WeatherOpsImp(this);

			// Store the WeatherOps into the RetainedFragmentManager.
			mRetainedFragmentManager.put("WEATHER_OPS_STATE", mWeatherOps);

			// Initiate the service binding protocol (which may be a
			// no-op, depending on which type of Service is
			// used).
			mWeatherOps.bindService();
		} else {
			// The RetainedFragmentManager was previously initialized,
			// which means that a runtime configuration change
			// occured.

			Log.d(TAG, "Second or subsequent onCreate() call");

			// Obtain the WeatherOps object from the
			// RetainedFragmentManager.
			mWeatherOps = mRetainedFragmentManager.get("WEATHER_OPS_STATE");

			// This check shouldn't be necessary under normal
			// circumstances, but it's better to lose state than to
			// crash!
			if (mWeatherOps == null) {
				// Create the WeatherOps object one time.  The "true"
				// parameter instructs WeatherOps to use the
				// BoundService.
				mWeatherOps = new WeatherOpsImp(this);

				// Store the WeatherOps into the RetainedFragmentManager.
				mRetainedFragmentManager.put("WEATHER_OPS_STATE", mWeatherOps);

				// Initiate the service binding protocol (which may be
				// a no-op, depending on which type of
				// Service is used).
				mWeatherOps.bindService();
			} else {
				// Inform it that the runtime configuration change has
				// completed.
				mWeatherOps.onConfigurationChange(this);
			}
		}
	}

	/**
	 * Initiate the synchronous weather lookup when the user presses
	 * the "Get Weather Sync" button.
	 */
	public void expandWeatherSync(View v) {
		// Get the location entered by the user.
		final String location = mEditText.getText().toString();

		// Asynchronously show the weather. 
		mWeatherOps.expandWeatherSync(location);
	}

	/**
	 * Initiate the asynchronous weather lookup when the user presses
	 * the "Get Weather Async" button.
	 */
	public void expandWeatherAsync(View v) {
		// Get the location entered by the user.
		final String location = mEditText.getText().toString();
		
		// Asynchronously show the weather. 
		mWeatherOps.expandWeatherAsync(location);
	}

	/**
	 * Display the results to the screen.
	 * 
	 * @param results List of Results to be displayed.
	 */
	public void displayResults(List<WeatherData> results,
			String errorMessage) {
		if (results == null || results.size() == 0) {
			UtilsGUI.showToast(this, errorMessage);
		} else {
			Log.d(TAG, "displayResults()");

			// Set/change data set.
			mWeatherOps.showResult(results.get(0));
		}
	}
}
