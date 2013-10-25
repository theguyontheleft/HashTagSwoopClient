package com.example.hashtagswoop;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * @author Jimmy Dagres
 * 
 * @version Oct 18, 2013
 * 
 *          "Shake shake, shake shake, a-shake it" - Metro Station
 * 
 *          This class handles the shake activity using the accelerometer to
 *          read for shakes, it performs the math calculations and creates a
 *          thread when the desired shake occurs
 */
public class ShakeMeter implements SensorEventListener
{
    // These values can be adjusted to change the intensity of the desired shake
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOPE_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;

    /**
     * @param onShakeListener
     */
    public void setOnShakeListener(
            OnShakeListener onShakeListener )
    {
        this.mListener = onShakeListener;
    }

    /**
     * @author Jimmy Dagres
     * 
     * @version Oct 18, 2013
     * 
     */
    public interface OnShakeListener
    {
        /**
         * @param count
         */
        public void onShake( int count );
    }

    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy )
    {
        // ignore
    }

    @Override
    public void onSensorChanged( SensorEvent event )
    {

        if ( mListener != null )
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce =
                    (float) java.lang.Math.sqrt( gX * gX + gY * gY + gZ * gZ );

            if ( gForce > SHAKE_THRESHOLD_GRAVITY )
            {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if ( mShakeTimestamp + SHAKE_SLOPE_TIME_MS > now )
                {
                    return;
                }

                // reset the shake count after 3 seconds of no shakes
                if ( mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now )
                {
                    mShakeCount = 0;
                }

                mShakeTimestamp = now;
                mShakeCount++;

                mListener.onShake( mShakeCount );
            }
        }
    }
}
