HashTagSwoop
===============

James Dagres

The is the client side. 
The server side is located here: https://github.com/theguyontheleft/HashTagSwoopServer

DESCRIPTION:
=============
This application was originally meant to display the top hashtags from Twitter, Facebook and Instagram. However,
only data available from twitter was available, so as the 
project evolved it turned into an application that will
have a server that will process any longitude and latitude
and return a JSONArray of nearby cities with trending hashtags from twitter. Using this app, hipsters and twitter congregations are easily findable. Followers for days!

SERVER:
=============
The server side is located here: https://github.com/theguyontheleft/HashTagSwoopServer

Uses three servlets
* The main servlet: (/*) Displays the locations of the other two servlets, receives data with keys containing longitude and 
latitude coordinates and displays that data if successful.
* The API servlet: (/api) Describes the process of post and pulling from the server. 
* Data (/data) Checks to see if a location coordinate was
posted to the server, if so it takes that location and pulls an array from twitter containing close by cities which
have a tending hashtag. This servlet then converts the 
pulled array to a JSONArray which it displays. The client
then checks this servlet to pull the JSON data which is
easily parsed and displayed in the client app. 


FEATURES
=======================================
* Will use a server (located here: https://github.com/theguyontheleft/HashTagSwoopServer) to receive and transmit data. More specifically it receives data from Android clients (the current clients location), stores that data in JSON format, and allows clients to download that data using the JSON API
* Uses three activities. The login to server activity, the main activity, and the shake activity.
* Currently uses three android sensors; the accelerometer, the vibration sensor and the GPS to get the location. 
* Accelerometer is used to pick up shake events
* GPS is used to get the location and post it to the main server
* Uses more than five user interfaces and GUI elements. A linear layout for the login activity, a relative layout for the main and shake activities, multiple TextViews, a toggle button to save the server login information, numerous images and a toast displaying the GPS location.
* The toggle button will save the server login settings
in a property file.
* Low battery usage

Future Implementations (in the works/private repo):
=======================================
* Pulling hashtags data from twitter, this is the main goal of this app and is currently in production!
* Facebook hashtag data
* Instagram hashtag data

Additional Resources:
=======================================
* For information on the twitter API and trend pulling see the following site: //twitter4j.org/oldjavadocs/3.0.3/index.html

ADDITIONAL PROGRAMS TO RUN THE PROJECT:
=======================================
Eclipse
* The program can be made and executed from the terminal as well 
as from Eclipse

INSTRUCTIONS:
=======================================
Building the project
* You will need to connect it to an android device and run the source code through eclipse or use one of eclipses virtual devices. The APK is also available. 

NOTES:
=======================================
* the Nexus 7 and similar tablets do not have haptic feedback, IE it does not have vibration, so vibration features from this project will not work.