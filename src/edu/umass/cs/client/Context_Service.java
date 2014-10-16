package edu.umass.cs.client;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import edu.umass.cs.accelerometer.ActivityClassifier;
import edu.umass.cs.accelerometer.ActivityFeatureExtractor;
import edu.umass.cs.accelerometer.ReorientAxis;

/**
 * 
 * Context_Service: This is a sample class to reads sensor data (accelerometer).
 * Received sensor data filtered, processed to extract features out of it and
 * finally sent to the classification pipeline to detect steps and identify
 * activity.
 * 
 * @author CS390MB
 * 
 */
public class Context_Service extends Service implements SensorEventListener {

	/**
	 * Notification manager to display notifications
	 */
	private NotificationManager nm;

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;

	/**
	 * Class to orient axis
	 */
	private ReorientAxis orienter = null;
	/**
	 * Feature extractor
	 */
	private ActivityFeatureExtractor extractor = null;

	// List of bound clients/activities to this service
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();

	// Message codes sent and received by the service
	static final int MSG_REGISTER_CLIENT = 1;
	static final int MSG_UNREGISTER_CLIENT = 2;
	static final int MSG_ACTIVITY_VALUE = 3;
	static final int MSG_ACCEL_VALUES = 5;
	static final int MSG_START_ACCELEROMETER = 6;
	static final int MSG_STOP_ACCELEROMETER = 7;
	static final int MSG_ACCELEROMETER_STARTED = 8;
	static final int MSG_ACCELEROMETER_STOPPED = 9;

	static Context_Service sInstance = null;
	private static boolean isRunning = false;
	private static boolean isAccelRunning = false;

	// Messenger used by clients
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * Handler to handle incoming messages
	 */
	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_START_ACCELEROMETER: {
				isAccelRunning = true;
				// TODO:Start Accelerometer Here
				mSensorManager.registerListener(sInstance, mAccelerometer,
						SensorManager.SENSOR_DELAY_GAME);
				orienter = new ReorientAxis();
				long WINDOW_IN_MILLISECONDS = 1000; // 5seconds
				// Set up a feature extractor that extracts features every 5
				// seconds
				extractor = new ActivityFeatureExtractor(WINDOW_IN_MILLISECONDS);
				// Send Message to UI that the accelerometer has been started
				sendMessageToUI(MSG_ACCELEROMETER_STARTED);
				showNotification();
				break;
			}
			case MSG_STOP_ACCELEROMETER: {
				isAccelRunning = false;
				// TODO:Stop Accelerometer Here
				mSensorManager.unregisterListener(sInstance);
				orienter = null;
				extractor = null;
				// Send Message to UI that the accelerometer has been stopped
				sendMessageToUI(MSG_ACCELEROMETER_STOPPED);
				showNotification();
				break;
			}
			default:
				super.handleMessage(msg);
			}
		}
	}

	private void sendMessageToUI(int message) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				// Send message value
				mClients.get(i).send(Message.obtain(null, message));
			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
			}
		}
	}

	private void sendActivityToUI(String activity) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				Bundle b = new Bundle();
				b.putString("activity", activity);
				Message msg = Message.obtain(null, MSG_ACTIVITY_VALUE);
				msg.setData(b);
				mClients.get(i).send(msg);
			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
			}
		}
	}

	private void sendAccelValuesToUI(float accX, float accY, float accZ) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {

				// Send Accel Values
				Bundle b = new Bundle();
				b.putFloat("accx", accX);
				b.putFloat("accy", accY);
				b.putFloat("accz", accZ);
				Message msg = Message.obtain(null, MSG_ACCEL_VALUES);
				msg.setData(b);
				mClients.get(i).send(msg);

			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
			}
		}
	}

	/**
	 * On Binding, return a binder
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	// Start service automatically if we reboot the phone
	public static class Context_BGReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent bootUp = new Intent(context, Context_Service.class);
			context.startService(bootUp);
		}
	}

	@SuppressWarnings("deprecation")
	private void showNotification() {
		// Cancel previous notification
		if (nm != null)
			nm.cancel(777);
		else
			nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		// Use the commented block of code if your target environment is
		// Android-16 or higher
		// Modify appropriately to show correct notifications
		/*
		 * Notification notification = new Notification.Builder(this)
		 * .setContentTitle("Context Service")
		 * .setContentText("Running").setSmallIcon(R.drawable.icon)
		 * .setContentIntent(contentIntent) .build();
		 * 
		 * nm.notify(777, notification);
		 */

		// For lower versions of Android, the following code should work
		Notification notification = new Notification();
		// notification.icon = R.drawable.icon;
		notification.tickerText = "Context Service Running";
		notification.contentIntent = contentIntent;
		notification.when = System.currentTimeMillis();
		if (isAccelerometerRunning())
			notification.setLatestEventInfo(getApplicationContext(),
					"Accelerometer Demo", "Accelerometer Running",
					contentIntent);
		else
			notification.setLatestEventInfo(getApplicationContext(),
					"Accelerometer Demo", "Accelerometer Not Started",
					contentIntent);

		// Send the notification.
		nm.notify(777, notification);
	}

	/* getInstance() and isRunning() are required by the */
	static Context_Service getInstance() {
		return sInstance;
	}

	protected static boolean isRunning() {
		return isRunning;
	}

	protected static boolean isAccelerometerRunning() {
		return isAccelRunning;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float accel[] = event.values;
			sendAccelValuesToUI(accel[0], accel[1], accel[2]);
			// Add the following
			long time = event.timestamp / 1000000; // convert time to
													// milliseconds from
													// nanoseconds
			// Orient accelerometer
			double ortAcc[] = orienter.getReorientedValues(accel[0], accel[1],
					accel[2]);

			// Extract Features now
			Double features[] = extractor.extractFeatures(time, ortAcc[0],
					ortAcc[1], ortAcc[2], accel[0], accel[1], accel[2]);

			// Feature vector is not null only when it has buffered
			// at least 5 seconds of data

			if (features != null) {
				// Classify
				try {
					double classId = ActivityClassifier.classify(features);

					// TODO: 1. The activity labels below will depend on
					// activities in your data set
					String activity = null;
					if (classId == 0.0)
						activity = "walking";
					else if (classId == 1.0)
						activity = "stationary";
					else if (classId == 2.0)
						activity = "running";

					sendActivityToUI(activity);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		showNotification();
		isRunning = true;
		sInstance = this;

		this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		this.mAccelerometer = this.mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		nm.cancel(777); // Cancel the persistent notification.
		isRunning = false;
		// Don't let Context_Service die!
		Intent mobilityIntent = new Intent(this, Context_Service.class);
		startService(mobilityIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY; // run until explicitly stopped.
	}

}
