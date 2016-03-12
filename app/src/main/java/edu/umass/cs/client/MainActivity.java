package edu.umass.cs.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.webkit.*;

public class MainActivity extends Activity {
    
	
	/** 
	 * Variable to check if accelerometer is running
	 */
	private boolean accelStarted = false;
	
	/**
	 * Instance of this activity
	 */
	private MainActivity activity;
	
	
	/*
	 * Various UI components 
	 */
	private TextView accelXView, accelYView, accelZView, activityView;
	private WebView activityImage;
	private TextView statusView;
	private CompoundButton accelButton;
	
	/**
	 * Messenger service for exchanging messages with the background service
	 */
	private Messenger mService = null;
    /**
     * Variable indicating if this activity is connected to the service
     */
	private boolean mIsBound;
    /**
     * Messenger receiving messages from the background service to  UI
     */
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    /**
     * Handler to handle incoming messages
     */
    @SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Context_Service.MSG_ACTIVITY_VALUE:
            {
            	activityImage.clearCache(true);
            	String activity = msg.getData().getString("activity");
            	String capitalized_activity = activity.substring(0, 1).toUpperCase() + activity.substring(1);
            	activityView.setText(capitalized_activity);
            	activityImage.loadUrl("file:///android_asset/" + activity + ".gif");
            	break;
            }
            case Context_Service.MSG_ACCEL_VALUES:
            {
            	float accX = msg.getData().getFloat("accx");
            	float accY = msg.getData().getFloat("accy");
            	float accZ = msg.getData().getFloat("accz");
            	activity.setAccelValues(accX,accY,accZ);
            	break;
            }
            case Context_Service.MSG_ACCELEROMETER_STARTED:
            {
            	if(accelButton!=null) {
            		accelButton.setChecked(true);
            		accelStarted = true;
            		statusView.setText("Accelerometer Started");
            	}
            	break;
            }
            case Context_Service.MSG_ACCELEROMETER_STOPPED:
            {
            	if(accelButton!=null) {
            		accelButton.setChecked(false);
            		accelStarted = false;
            		statusView.setText("Accelerometer Stopped");
            	}
            	break;
            }
            default:
                super.handleMessage(msg);
            }
        }
    }
    
    /**
     * Connection with the service
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            statusView.setText("Attached to Service");
            mIsBound = true;
            try {
                Message msg = Message.obtain(null, Context_Service.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
        	mIsBound = false;
            mService = null;
            statusView.setText("Disconnected from Service");
        }
    };
	
    /* Invoked when an activity is created
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	activity = this;
    	super.onCreate(savedInstanceState);
        //Set Layout
        setContentView(R.layout.main);
        
        //Setting up text views
        statusView = (TextView) findViewById(R.id.StatusView);
        accelXView = (TextView) findViewById(R.id.AccelXView);
        activityImage = (WebView) findViewById(R.id.ActivityImage);
        activityView =  (TextView) findViewById(R.id.ActivityView);
        accelYView = (TextView) findViewById(R.id.AccelYView);
        accelZView = (TextView) findViewById(R.id.AccelZView);
        statusView.setText("Service Not Bound");
        
        //Start Background Service if not already started
        if(!Context_Service.isRunning()) {
        	Intent cssBg = new Intent(activity,Context_Service.class);
    		startService(cssBg);
        }
        
        
        //Bind to the service if it is already running
        bindToServiceIfIsRunning();
        
        //Determine if the accelerometer is on
        accelStarted = false;
        if(Context_Service.isAccelerometerRunning())
        	accelStarted = true;
        
        //Set the buttons and the text accordingly
        accelButton = (ToggleButton) findViewById(R.id.StartButton);
        accelButton.setChecked(accelStarted);
        accelButton.setOnCheckedChangeListener(
        		new OnCheckedChangeListener() {
        		    public void onCheckedChanged(CompoundButton btn,boolean isChecked) {
        		    	accelStarted = Context_Service.isAccelerometerRunning();
        		    	if(!accelStarted)
        		    		startAccelerometer();
        		    	else
        		    		stopAccelerometer();
        		    }
        		}
        );
        
    	activityImage.loadUrl("file:///android_asset/sleeping.gif");
        
        
    }
    
    /**
     * Binds this activity to the service if the service is already running
     */
    private void bindToServiceIfIsRunning() {
        //If the service is running when the activity starts, we want to automatically bind to it.
        if (Context_Service.isRunning()) {
            doBindService();//
            statusView.setText("Request to bind service");
        }
    }
	
	
    /**
     * This method is required to send a request to the background service.
     * In current application, we are not sending any message yet.
     * @param message
     */
    private void sendMessageToService(int message) {
        if (mIsBound) {
            if (mService != null) {
            	try {
                    Message msg = Message.obtain(null, message);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }
    
    /**
     * Display accelerometer values in UI
     * @param accX
     * @param accY
     * @param accZ
     */
    public void setAccelValues(float accX, float accY, float accZ) {
    	String text = String.format("%2.2f", accX);
    	accelXView.setText(text);
    	text = String.format("%2.2f", accY);
    	accelYView.setText(text);
    	text = String.format("%2.2f", accZ);
    	accelZView.setText(text);
    	//setActivityBackgroundColor(accX, accY, accZ);
    }
    
    public void setActivityBackgroundColor(double x, double y, double z) {
        View view = this.getWindow().getDecorView();
        
        //
        // So, it would be possible to have the accelerometer data 
        // better map to colors by having a range, but I thought it would 
        // be easier to represent by assuming that the only force acting on
        // the phone would be gravity
        //
        // I also wanted the background to not get too light as to make
        // the labels unreadable, so I set the maximum lightness of the
        // background to RGB(150,150,150) (or 10 * 15) if the phone
        // accelerates beyond that point in any dimension, the background
        // won't update.
        //
        if(Math.abs(x) < 15 && Math.abs(y) < 15 && Math.abs(z) < 15){
        	view.setBackgroundColor(Color.rgb(10*Math.abs((int)x),10*Math.abs((int)y),10*Math.abs((int)z)));
        }
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	try {
            doUnbindService();
        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }
    
    /**
     * Binds the activity to the background service
     */
    void doBindService() {
        bindService(new Intent(this, Context_Service.class), mConnection, Context.BIND_AUTO_CREATE);
        statusView.setText("Binding to Service");
    }
    
    /**
     * Unbind this activity from the background service
     */
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, Context_Service.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            statusView.setText("Unbinding from Service");
        }
    }
    
    /**
     * Sends Accelerometer Start Request
     */
    private void startAccelerometer() {
    	if(!mIsBound) {
    		doBindService();
    		//In this case, start accelerometer won't work because service is not bound
    		accelButton.setChecked(false);
    	}
    	if(mIsBound) {
    		sendMessageToService(Context_Service.MSG_START_ACCELEROMETER);
    	}
    }
    
    /**
     * Sends Accelerometer Stop Request
     */
    private void stopAccelerometer() {
    	if(!mIsBound) {
    		doBindService();
    	}
    	if(mIsBound) {
    		sendMessageToService(Context_Service.MSG_STOP_ACCELEROMETER);
    		activityImage.loadUrl("file:///android_asset/sleeping.gif");
    		activityView.setText("");
    		
    	}
    }
}