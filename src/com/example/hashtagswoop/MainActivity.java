package com.example.hashtagswoop;

import org.json.JSONArray;
import org.json.JSONObject;

import serverconnect.DataPullThread;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.hashtagswoop.ShakeMeter.OnShakeListener;

/**
 * @author Jimmy Dagres
 * 
 * @version Oct 17, 2013
 * 
 */
public class MainActivity extends Activity implements SensorEventListener
{
    // The following are used for the shake detection
    private SensorManager sensorManager_;
    private Sensor accelerometer_;
    private ShakeMeter shakeMeter_;

    // The server to pull from, received from the login activity
    private String serverToGet_;

    // Instance the asynchronous thread that gets and parses the data
    private DataPullThread asyncThread_ = null;

    // Textview to display on the main thread
    private TextView tv1;

    /**
     * @return the serverToGet_
     */
    public String getServerToGet()
    {
        return serverToGet_;
    }

    /**
     * Ensure a one shake doesn't run the shake sequence more than once
     */
    static private boolean isShaked_ = false;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // Receives the server location that was logged in
        Intent nbundle = getIntent();
        serverToGet_ = nbundle.getStringExtra( "serverToGet" );

        // Let's pull the data from the daily data servlet
        serverToGet_ = serverToGet_ + "/data";

        // ShakeDetector initialization
        sensorManager_ =
                (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        accelerometer_ = sensorManager_
                .getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        shakeMeter_ = new ShakeMeter();

        tv1 = (TextView) findViewById( R.id.textView1 );

        asyncThread_ = new DataPullThread( this );

        shakeMeter_.setOnShakeListener( new OnShakeListener()
        {
            @Override
            public void onShake( int count )
            {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                handleShakeEvent( count );
            }

            /**
             * This function runs when the shake event is detected, it starts
             * the shake activity
             * 
             * @param count
             */
            private void handleShakeEvent( int count )
            {
                // The first time a shake is registered
                if ( !isShaked_ )
                {
                    isShaked_ = true;

                    // Vibrate the device on a successful shake
                    Vibrator v =
                            (Vibrator) getSystemService( Context.VIBRATOR_SERVICE );
                    v.vibrate( 200 );

                    // Start the shake activity
                    Intent myIntentionsArePure =
                            new Intent( MainActivity.this, ShakeActivity.class );
                    myIntentionsArePure.putExtra( "key", isShaked_ ); // Optional

                    MainActivity.this.startActivity( myIntentionsArePure );

                    // See if there's website data
                    fetchWebsiteData();
                }
            }
        } );

        // Make sure the keyboard doesn't try and show it's ugly head
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
    }

    /**
     * Starts a thread to get the data from the logged in server and sets the
     * text to display this information
     */
    protected void fetchWebsiteData()
    {
        if ( null != serverToGet_ )
        {
            // Display server information
            if ( "" != serverToGet_.trim() )
            {
                // Initialize the asynchronous threads
                asyncThread_.execute( (Void) null );
                asyncThread_ =
                        new DataPullThread( this );
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public void onAccuracyChanged( Sensor arg0, int arg1 )
    {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public void onSensorChanged( SensorEvent arg0 )
    {
        // Don't need to do anything when the sensor value is changed
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // Registers the Session Manager Listener onResume
        sensorManager_.registerListener( shakeMeter_, accelerometer_,
                SensorManager.SENSOR_DELAY_UI );
    }

    @Override
    public void onPause()
    {
        // Unregister the Sensor Manager onPause
        sensorManager_.unregisterListener( shakeMeter_ );
        super.onPause();
    }

    /**
     * This function is called when the shaken activity was closed
     */
    public void ShakenClosed()
    {
        isShaked_ = false;
        shakeMeter_ = new ShakeMeter();

        // Make sure the keyboard doesn't try and show it's ugly head
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
    }

    /**
     * This function downloads and prints the server information
     * 
     * @param dataToPrint
     */
    public void printServerInformation( String dataToPrint )
    {
        try
        {
            // Parse and display the JSON data
            JSONObject jObject = new JSONObject( dataToPrint );

            JSONArray array = jObject.getJSONArray( "location" );
            String stringToDisplay =
                    "The closest cities to you that Twitter has trending topics for are:";
            for ( int j = 0; j < array.length(); j++ )
            {
                JSONObject coordinateObject = array.getJSONObject( j );

                stringToDisplay +=
                        "\n" + coordinateObject.getString( "City" )
                                + ", "
                                + coordinateObject.getString( "Country" );
            }
            tv1.setText( stringToDisplay );
        }
        catch ( Exception ex )
        {

        }
    }
}
