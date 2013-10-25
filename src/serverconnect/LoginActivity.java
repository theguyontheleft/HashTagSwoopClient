package serverconnect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.hashtagswoop.MainActivity;
import com.example.hashtagswoop.R;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 * 
 * @author Jimmy Dagres
 * 
 * @version Oct 20, 2013
 * 
 */
public class LoginActivity extends Activity
{
    // Display that you're in the login page
    TextView loginStatus_;

    // The remember login info toggle button
    ToggleButton rememberLoginToggle_;
    static boolean rememberLoginIsSet_ = false;

    // GPS location values
    protected double longitude_;
    protected double latitude_;
    private GPSLocator gps_ = null;

    // Properties file the login information is save in
    Properties properties_;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    private String mIP_;
    private String mPort_;

    // The server to pull from, received from the login activity
    static protected String serverToGet_;

    // UI references.
    private EditText mIPView_;
    private EditText mPortView_;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_login );
        loginStatus_ = (TextView) findViewById( R.id.loginStatus );

        // Initialize the properties information
        properties_ = new Properties();
        FileInputStream in;
        try
        {
            String filepath =
                    this.getFilesDir().getPath().toString()
                            + "/properties.txt";
            in = new FileInputStream( filepath );
            properties_.load( in );
            in.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        // Initialize toggle button
        setUpToggleButton();

        // Initialize login information
        setLoginInformation();

        findViewById( R.id.sign_in_button ).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick( View view )
                    {
                        attemptLogin();

                        finish();
                    }
                } );

        // Get Initial GPS location
        getGPSLocation();
    }

    /**
     * Sets up the toggle button
     */
    private void setUpToggleButton()
    {
        rememberLoginToggle_ =
                (ToggleButton) findViewById( R.id.rememberLoginToggleButton );
        try
        {
            if ( properties_.containsKey( getString( R.string.saveInfo ) ) )
            {
                rememberLoginIsSet_ =
                        Boolean.parseBoolean( (properties_
                                .get( getString( R.string.saveInfo ) ))
                                .toString() );
            }
            else
            {
                rememberLoginIsSet_ = true;
            }
        }
        catch ( Exception ex )
        {
            System.err.print( "Error converting to boolean: " + ex );
        }

        rememberLoginToggle_.setChecked( rememberLoginIsSet_ );

        rememberLoginToggle_.setOnClickListener( new View.OnClickListener()
        {
            // Toggle the button on click
            @Override
            public void onClick( View v )
            {
                rememberLoginIsSet_ = !rememberLoginIsSet_;
            }
        } );
    }

    /**
     * Called from on create to initialize the login information
     */
    private void setLoginInformation()
    {
        // Set up the login form.
        mIPView_ = (EditText) findViewById( R.id.IP );
        mPortView_ = (EditText) findViewById( R.id.password );

        // Set the intial display
        if ( rememberLoginIsSet_ )
        {
            if ( properties_.containsKey( getString( R.string.saved_IP ) ) )
            {
                mIP_ =
                        properties_
                                .getProperty( getString( R.string.saved_IP ) );
            }

            if ( properties_.containsKey( getString( R.string.saved_Port ) ) )
            {
                mPort_ =
                        properties_
                                .getProperty( getString( R.string.saved_Port ) );
            }
        }

        // Set the values to whatever the load settings or default are
        mIPView_.setText( mIP_ );
        mPortView_.setText( mPort_ );

        mPortView_
                .setOnEditorActionListener( new TextView.OnEditorActionListener()
                {
                    @Override
                    public boolean onEditorAction( TextView textView, int id,
                            KeyEvent keyEvent )
                    {
                        if ( id == R.id.login || id == EditorInfo.IME_NULL )
                        {
                            attemptLogin();
                            return true;
                        }
                        return false;
                    }
                } );

        mLoginFormView = findViewById( R.id.login_form );
        mLoginStatusView = findViewById( R.id.login_status );
        mLoginStatusMessageView =
                (TextView) findViewById( R.id.login_status_message );
    }

    /**
     * Try's to get the GPS location and if successful gets the closest WOIED
     * for that location
     */
    private void getGPSLocation()
    {
        gps_ = new GPSLocator( LoginActivity.this );

        // check if GPS enabled
        if ( gps_.canGetLocation() )
        {
            latitude_ = gps_.getLatitude();
            longitude_ = gps_.getLongitude();

            // Raises a toast to show the location
            Toast.makeText(
                    getApplicationContext(),
                    "Your Location is - \nLat: " + latitude_ + "\nLong: "
                            + longitude_, Toast.LENGTH_SHORT ).show();
        }
        else
        {
            // There was an error getting the gps information
            gps_.showSettingsAlert();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        super.onCreateOptionsMenu( menu );
        getMenuInflater().inflate( R.menu.login, menu );
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause()
    {
        super.onPause();

        // Turn off the GPS
        gps_.stopUsingGPS();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        // Turn the GPS back on
        gps_ = new GPSLocator( LoginActivity.this );
        super.onResume();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin()
    {
        if ( mAuthTask != null )
        {
            return;
        }

        // Reset errors.
        mIPView_.setError( null );
        mPortView_.setError( null );

        // Store values at the time of the login attempt.
        mIP_ = mIPView_.getText().toString();
        mPort_ = mPortView_.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if ( TextUtils.isEmpty( mPort_ ) )
        {
            mPortView_
                    .setError( getString( R.string.error_field_required ) );
            focusView = mPortView_;
            cancel = true;
        }
        // The port should be four digits long
        else if ( mPort_.length() != 4 )
        {
            mPortView_
                    .setError( getString( R.string.error_invalid_server ) );
            focusView = mPortView_;
            cancel = true;
        }

        // Check for a valid email address.
        if ( TextUtils.isEmpty( mIP_ ) )
        {
            mIPView_.setError( getString( R.string.error_field_required ) );
            focusView = mIPView_;
            cancel = true;
        }

        if ( cancel )
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView
                    .setText( R.string.login_progress_signing_in );
            showProgress( true );

            // Start the asynchronous thread to connect and post to the server
            mAuthTask = new UserLoginTask();
            mAuthTask.execute( (Void) null );
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi( Build.VERSION_CODES.HONEYCOMB_MR2 )
    private void showProgress( final boolean show )
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 )
        {
            int shortAnimTime =
                    getResources().getInteger(
                            android.R.integer.config_shortAnimTime );

            mLoginStatusView.setVisibility( View.VISIBLE );
            mLoginStatusView.animate()
                    .setDuration( shortAnimTime )
                    .alpha( show ? 1 : 0 )
                    .setListener( new AnimatorListenerAdapter()
                    {
                        @Override
                        public void onAnimationEnd( Animator animation )
                        {
                            mLoginStatusView.setVisibility( show
                                    ? View.VISIBLE : View.GONE );
                        }
                    } );

            mLoginFormView.setVisibility( View.VISIBLE );
            mLoginFormView.animate()
                    .setDuration( shortAnimTime )
                    .alpha( show ? 0 : 1 )
                    .setListener( new AnimatorListenerAdapter()
                    {
                        @Override
                        public void onAnimationEnd( Animator animation )
                        {
                            mLoginFormView.setVisibility( show ? View.GONE
                                    : View.VISIBLE );
                        }
                    } );
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility( show ? View.VISIBLE : View.GONE );
            mLoginFormView.setVisibility( show ? View.GONE : View.VISIBLE );
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground( Void... params )
        {
            try
            {
                serverToGet_ = "http://" + mIP_ + ":" + mPort_;
                BufferedReader in = null;

                // setup http client
                HttpClient client = new DefaultHttpClient();

                // Attempt authentication of the running sever
                HttpGet request = new HttpGet( serverToGet_ );

                HttpResponse response = client.execute( request );

                // string using buffered reader
                in =
                        new BufferedReader( new InputStreamReader( ((
                                HttpResponse) response)
                                        .getEntity().getContent() ) );
                StringBuffer sb = new StringBuffer( "" );

                String l = "";
                String newline = System.getProperty( "line.separator" );
                while ( (l = in.readLine()) != null )
                {
                    sb.append( l + newline );
                }
                in.close();

                if ( null != sb.toString() )
                {
                    if ( null != gps_ )
                    {
                        if ( getString( R.string.error_posting_to_server ) != postToServer() )
                        {
                            return true;
                        }
                    }
                }
                else
                {
                    // GPS wasn't enabled, or the GPS failed to get location
                    return true;
                }
            }
            catch ( Exception ex )
            {
                System.err.print( "Error Connecting to Server: " + ex );
            }

            return false;
        }

        /**
         * This function posts the location to the server
         * 
         * @param nameAndOrderFromPrev
         * @return the data from the post
         */
        public String postToServer()
        {
            // posting to server
            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost( serverToGet_ );
            String data = "";

            try
            {
                // three parameters are passed
                List<NameValuePair> nameValuePairs =
                        new ArrayList<NameValuePair>( 1 );
                nameValuePairs
                        .add( new BasicNameValuePair( "longitude", Double
                                .toString( longitude_ ) ) );
                nameValuePairs.add( new BasicNameValuePair( "latitude", Double
                        .toString( latitude_ ) ) );
                // nameValuePairs
                // .add( new BasicNameValuePair( "location", "none" ) );
                post.setEntity( new UrlEncodedFormEntity( nameValuePairs ) );

                HttpResponse response = client.execute( post );
                BufferedReader rd =
                        new BufferedReader( new InputStreamReader( response
                                .getEntity().getContent() ) );
                // read from server
                String line = "";
                StringBuffer sb = new StringBuffer( "" );
                String newline = System.getProperty( "line.separator" );
                while ( (line = rd.readLine()) != null )
                {
                    sb.append( line + newline );

                }
                rd.close();
                data = sb.toString();
                // get acknowledgment from server
                return data;
            }
            catch ( IOException e )
            {
                data = getString( R.string.error_posting_to_server );
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute( final Boolean success )
        {
            mAuthTask = null;
            showProgress( false );

            if ( success )
            {
                // Start the main activity on login
                Intent myIntent =
                        new Intent( LoginActivity.this, MainActivity.class );
                myIntent.putExtra( "serverToGet", serverToGet_ );
                LoginActivity.this.startActivity( myIntent );

                finish();
            }
            else
            {
                // Raises a toast to show connection to the server failed
                displayErrorToast();

                mPortView_
                        .setError( getString( R.string.error_incorrect_password ) );
                mPortView_.requestFocus();
            }
        }

        @Override
        protected void onCancelled()
        {
            mAuthTask = null;
            showProgress( false );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onDestroy()
    {
        saveSettings();
        super.onDestroy();
    }

    /**
     * Stores the data settings, writing the appropriate settings to the
     * properties file
     */
    private void saveSettings()
    {
        try
        {
            // If remember information is true then save the ip and port
            // properties to the projects config.properties file
            if ( rememberLoginIsSet_ )
            {
                properties_.setProperty( getString( R.string.saved_IP ), mIP_ );
                properties_.setProperty( getString( R.string.saved_Port ),
                        mPort_ );
            }

            // Always save the remember login boolean
            properties_.setProperty( getString( R.string.saveInfo ),
                    String.valueOf( rememberLoginIsSet_ ) );

            File propertiesFile =
                    new File( this.getFilesDir().getPath().toString()
                            + "/properties.txt" );
            FileOutputStream out =
                    new FileOutputStream( propertiesFile );
            properties_.store( out, "Swoop" );
            out.close();
        }
        catch ( Exception ex )
        {
            System.err.print( ex );
        }
    }

    /**
     * This function raises a toast prompting the user when connection to the
     * server failed
     */
    private void displayErrorToast()
    {
        Toast.makeText(
                getApplicationContext(),
                "Connection to the server failed, please try again!",
                Toast.LENGTH_LONG ).show();
    }
}