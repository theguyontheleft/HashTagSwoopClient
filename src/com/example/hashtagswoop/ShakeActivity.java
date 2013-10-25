package com.example.hashtagswoop;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.hashtagswoop.ShakeMeter.OnShakeListener;

/**
 * @author Jimmy Dagres
 * 
 * @version Oct 18, 2013
 * 
 *          This is the shake activity class
 * 
 */
public class ShakeActivity extends MainActivity
{
    // The following are used for the shake detection
    private SensorManager sensorManager_;
    private Sensor accelerometer_;
    private ShakeMeter shakeMeter_;

    // TextView title
    private TextView shakeTitle_;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_shake );

        // Make sure the keyboard doesn't try and show it's ugly head
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );

        shakeTitle_ = (TextView) findViewById( R.id.shakeTitle );

        // ShakeDetector initialization
        sensorManager_ =
                (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        accelerometer_ = sensorManager_
                .getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        shakeMeter_ = new ShakeMeter();

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
                // Vibrate the device on a successful shake
                Vibrator v =
                        (Vibrator) getSystemService( Context.VIBRATOR_SERVICE );
                v.vibrate( 200 );

                shakeTitle_.setText( "Shake activity ending" );
                finish();
            }
        } );
    }

    public void onAccuracyChanged( Sensor arg0, int arg1 )
    {
        // Do something here if sensor accuracy changes.
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        super.ShakenClosed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
     * android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu( ContextMenu menu, View v,
            ContextMenuInfo menuInfo )
    {
        super.onCreateContextMenu( menu, v, menuInfo );
    }

}
