package serverconnect;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.hashtagswoop.MainActivity;

import android.os.AsyncTask;


/**
 * @author Jimmy Dagres
 * 
 * @version Sep 27, 2013
 * 
 */
public class DataPullThread extends AsyncTask<Void, Void, String>
{
    // Reference the main activity object
    protected MainActivity activity_;

    /**
     * @param activityToCallBack
     */
    public DataPullThread( MainActivity activityToCallBack )
    {
        activity_ = activityToCallBack;
    }

    /**
     * @return the string received from the page
     * @throws Exception
     */
    public String connectToPage() throws Exception
    {
        BufferedReader in = null;
        String data = ""; // null;
        try
        {
            // setup http client
            HttpClient client = new DefaultHttpClient();

            // Get data from website
            // Let's pull the data from the daily data servlet
            URI website =
                    new URI( activity_.getServerToGet());

            // request using get method
            HttpGet request = new HttpGet( website );

            HttpResponse response = client.execute( request );

            // string using buffered reader
            in =
                    new BufferedReader( new InputStreamReader( response
                            .getEntity().getContent() ) );
            StringBuffer sb = new StringBuffer( "" );

            String l = "";
            String newline = System.getProperty( "line.separator" );
            while ( (l = in.readLine()) != null )
            {
                sb.append( l + newline );
            }
            in.close();
            data = sb.toString();

            return data;
        }
        finally
        {
            if ( in != null )
            {
                try
                {
                    in.close();
                    return data;
                }
                catch ( Exception e )
                {
                    System.out.println( "Error e:" + e.toString() );

                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected String doInBackground( Void... params )
    {
        try
        {
            return connectToPage();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return "Error, no Data returned!!!";
        }
    }

    @Override
    protected void onPostExecute( String result )
    {
        // Call a function from activity to print the resulting location
        activity_.printServerInformation( result );
    }
}