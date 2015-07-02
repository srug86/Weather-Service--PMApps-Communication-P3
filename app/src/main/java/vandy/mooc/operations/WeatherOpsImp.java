package vandy.mooc.operations;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import vandy.mooc.activities.DisplayWeatherActivity;
import vandy.mooc.activities.MainActivity;
import vandy.mooc.aidl.WeatherCall;
import vandy.mooc.aidl.WeatherData;
import vandy.mooc.aidl.WeatherRequest;
import vandy.mooc.aidl.WeatherResults;
import vandy.mooc.services.WeatherServiceAsync;
import vandy.mooc.services.WeatherServiceSync;
import vandy.mooc.utils.GenericServiceConnection;

/**
 * This class implements all the weather-related operations defined in
 * the WeatherOps interface.
 */
public class WeatherOpsImp implements WeatherOps {

	protected final String TAG = getClass().getSimpleName();

	/**
	 * Used to enable garbage collection.
	 */
	protected WeakReference<MainActivity> mActivity;

	/**
	 * This GenericServiceConnection is used to receive results after
	 * binding to the WeatherServiceSync Service using bindService().
	 */
	private GenericServiceConnection<WeatherCall> mServiceConnectionSync;

	/**
	 * This GenericServiceConnection is used to receive results after
	 * binding to the WeatherServiceAsync Service using bindService().
	 */
	private GenericServiceConnection<WeatherRequest> mServiceConnectionAsync;

	/**
	 * List of results to display (if any).
	 */
	protected List<WeatherData> mResults;

	/**
	 * This Handler is used to post Runnables to the UI from the
	 * mWeatherResults callback methods to avoid a dependency on the
	 * Activity, which may be destroyed in the UI Thread during a
	 * runtime configuration change.
	 */
	private final Handler mDisplayHandler = new Handler();

	/**
	 * The implementation of the AcronymResults AIDL Interface, which
	 * will be passed to the Acronym Web service using the
	 * AcronymRequest.expandAcronym() method.
	 * 
	 * This implementation of AcronymResults.Stub plays the role of
	 * Invoker in the Broker Pattern since it dispatches the upcall to
	 * sendResults().
	 */
	private final WeatherResults.Stub mWeatherResults =
			new WeatherResults.Stub() {

		/**
		 * This method is invoked by the AcronymServiceAsync to
		 * return the results back to the AcronymActivity.
		 */
		@Override
		public void sendResults(final List<WeatherData> results)
				throws RemoteException {
			// Since the Android Binder framework dispatches this
			// method in a background Thread we need to explicitly
			// post a runnable containing the results to the UI
			// Thread, where it's displayed.  We use the
			// mDisplayHandler to avoid a dependency on the
			// Activity, which may be destroyed in the UI Thread
			// during a runtime configuration change.
			mDisplayHandler.post(new Runnable() {
				public void run() {
					mResults = results;
					mActivity.get().displayResults(results, null);
				}
			});
		}
	};

	/**
	 * Constructor initializes the fields.
	 */
	public WeatherOpsImp(MainActivity activity) {
		// Initialize the WeakReference.
		mActivity = new WeakReference<>(activity);

		// Initialize the GenericServiceConnection objects.
		mServiceConnectionSync = 
				new GenericServiceConnection<WeatherCall>(WeatherCall.class);

		mServiceConnectionAsync =
				new GenericServiceConnection<WeatherRequest>(WeatherRequest.class);
	}

	/**
	 * Initiate the service binding protocol.
	 */
	@Override
	public void bindService() {
		Log.d(TAG, "calling bindService()");

		// Launch the Weather Bound Services if they aren't already
		// running via a call to bindService(), which binds this
		// activity to the WeatherService* if they aren't already
		// bound.
		if (mServiceConnectionSync.getInterface() == null) { 
			mActivity.get().getApplicationContext().bindService
			(WeatherServiceSync.makeIntent(mActivity.get()),
					mServiceConnectionSync,
					Context.BIND_AUTO_CREATE);
		}

		if (mServiceConnectionAsync.getInterface() == null) {
			mActivity.get().getApplicationContext().bindService
			(WeatherServiceAsync.makeIntent(mActivity.get()),
					mServiceConnectionAsync,
					Context.BIND_AUTO_CREATE);
		}
	}

	/**
	 * Initiate the service unbinding protocol.
	 */
	@Override
	public void unbindService() {
		if (mActivity.get().isChangingConfigurations()) {
			Log.d(TAG, "just a configuration change - unbindService() not called");
		} else {
			Log.d(TAG, "calling unbindService()");

			// Unbind the Weather Service if it is connected.
			if (mServiceConnectionAsync.getInterface() != null) {
				mActivity.get().getApplicationContext().unbindService
				(mServiceConnectionAsync);
			}

			// Unbind the Sync Service if it is connected.
			if (mServiceConnectionSync.getInterface() != null) {
				mActivity.get().getApplicationContext().unbindService
				(mServiceConnectionSync);
			}
		}
	}

	/**
	 * Initiate the synchronous weather lookup when the user presses
	 * the "Get Weather Sync" button.
	 */
	@Override
	public void expandWeatherSync(String location) {
		final WeatherCall weatherCall = mServiceConnectionSync.getInterface();

		if (weatherCall != null) {
			// Use an anonymous AsyncTask to download the Acronym data
			// in a separate thread and then display any results in
			// the UI thread.
			new AsyncTask<String, Void, List<WeatherData>> () {
				/**
				 * Location we're trying to expand.
				 */
				private String mLocation;

				/**
				 * Retrieve the location results via a
				 * synchronous two-way method call, which runs in a
				 * background thread to avoid blocking the UI thread.
				 */
				protected List<WeatherData> doInBackground(String... locations) {
					try {
						mLocation = locations[0];
						return weatherCall.getCurrentWeather(mLocation);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					return null;
				}

				/**
				 * Display the results in the UI Thread.
				 */
				protected void onPostExecute(List<WeatherData> weatherDataList) {
					mResults = weatherDataList;
					mActivity.get().displayResults(weatherDataList,
							"no weather for "
									+ mLocation
									+ " found");
				}
				// Execute the AsyncTask to expand the acronym without
				// blocking the caller.
			}.execute(location);
		} else {
			Log.d(TAG, "mWeatherCall was null.");
		}
	}

	/**
	 * Initiate the asynchronous weather lookup when the user presses
	 * the "Get Weather Async" button.
	 */
	@Override
	public void expandWeatherAsync(String location) {
		final WeatherRequest weatherRequest = 
				mServiceConnectionAsync.getInterface();

		if (weatherRequest != null) {
			try {
				// Invoke a one-way AIDL call, which does not block
				// the client.  The results are returned via the
				// sendResults() method of the mWeatherResults
				// callback object, which runs in a Thread from the
				// Thread pool managed by the Binder framework.
				weatherRequest.getCurrentWeather(location, mWeatherResults);
			} catch (RemoteException e) {
				Log.e(TAG, "RemoteException:" + e.getMessage());
			}
		} else {
			Log.d(TAG, "WeatherRequest was null.");
		}
	}

	/**
	 * Called after a runtime configuration change occurs to finish
	 * the initialization steps.
	 */
	@Override
	public void onConfigurationChange(MainActivity activity) {
		Log.d(TAG, "onConfigurationChange() called");

		// Reset the mActivity WeakReference.
		mActivity = new WeakReference<>(activity);

		updateResultsDisplay();
	}

	/**
	 * Display results if any (due to runtime configuration change).
	 */
	private void updateResultsDisplay() {
		if (mResults != null) {
			mActivity.get().displayResults(mResults, null);
		}
	}

	@Override
	public void showResult(WeatherData result) {
		final Intent intent =
				DisplayWeatherActivity.makeIntent(mActivity.get(), result);
		mActivity.get().startActivity(intent);
	}
}
