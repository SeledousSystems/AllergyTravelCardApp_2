package com.wright.paul.allergytravelcardapp.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wright.paul.allergytravelcardapp.R;
import com.wright.paul.allergytravelcardapp.userInterface.CardActivity;
import com.wright.paul.allergytravelcardapp.userInterface.MainActivity;

import java.util.List;
import java.util.Locale;

/**
 * Class that defines the Location Service. Service that runs as a start service and listens to location changes every hour and 1km.
 * If the country changes to one that is supported by the app a notification is displayed advising the user that
 * they can make cards for their current country in the relevant language.
 */
public class LocationService extends Service {

    /**
     * Class Attributes
     */
    private static final String COUNTRY_FILE = "MyCountryFile";
    private static final String TAG = "LOCATIONSERVICE";
    //set the location update intervals to 60secs
    private static final int LOCATION_INTERVAL = 60000;
    //set location update distance deltas to 1000 meters
    private static final float LOCATION_DISTANCE = 1000f;
    protected Location mLastLocation;
    protected String oldCountry, newCountry = null;
    protected Geocoder geocoder;
    LocationListener mLocationListener = new LocationListener(LocationManager.PASSIVE_PROVIDER);
    private Context context;
    private LocationManager mLocationManager = null;
    private NotificationManager mNotificationManager = null;
    private Criteria criteria = null;

    /**
     * onBind method called when method is bound to
     *
     * @param arg0
     * @return
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * onStart method that is called when the method for the service is started.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        context = this;
        //assign geocoder to get address from location
        geocoder = new Geocoder(this, Locale.getDefault());
        super.onStartCommand(intent, flags, startId);
        //return Start Sticky so service is not stopped on return from onStartCommand method
        return START_STICKY;
    }

    /**
     * onCreate method called when the service is created. Initialises the location manager.
     */
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListener);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    /**
     * method called when the service is destroyed. Clears the location manager.
     */
    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {

            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);

            }
        }
    }

    /**
     * Method for initialising the location manager.
     */
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /**
     * Defines the Location Listener class that listens for changes in the location.
     */
    private class LocationListener implements android.location.LocationListener {

        /**
         * Location Listener constructor
         *
         * @param provider
         */
        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        /**
         * method called on location change. The method gets the country of the new location, compares it
         * to the persisted existing location and if different and the location's language is supported by the
         * app then a notification is posted advising the user that they can create a card in the Country's language.
         *
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            if (geocoder == null) Log.e(TAG, "geocoder is null");
            try {
                Log.e(TAG, "onLocationChanged: " + location);
                mLastLocation.set(location);
            } catch (Exception e) {
                Log.d(TAG, "Onlocation changed exception. mLastLocation or location parameter");
            }
            //get the address of the new location
            List<Address> addresses = null;
            try {
                if (geocoder != null) {
                    addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        //get the country from the address
                        newCountry = addresses.get(0).getCountryName();
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception");
                e.printStackTrace();
            }
            //get the previous location country
            SharedPreferences countryLoc = getSharedPreferences(COUNTRY_FILE, 0);
            oldCountry = countryLoc.getString("oldCountry", "");
            Log.d(TAG, oldCountry + " " + newCountry);
            //if the new country is different to the old country, post a notification to the user.
            if (!oldCountry.equals(newCountry)) {
                String language = CardManager.getLanguage(newCountry, context);
                //check country/language is supported by the app
                if (language != null) {
                    //build a notification to advise user of h
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.appl_icon)
                            .setContentTitle("ATC: Welcome to " + newCountry)
                            .setContentText("Tap to create allergy cards in " + language);
                    mNotificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent notificationIntent = new Intent(context, MainActivity.class);
                    // The stack builder object will contain an artificial back stack for the started Activity.
                    // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    // Add the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(CardActivity.class);
                    // Add the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(notificationIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    mBuilder.setAutoCancel(true);
                    mNotificationManager.notify(1, mBuilder.build());

                } else {
                    //if the country changes but to a country not supported by the app, cancel the notification
                    mNotificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(1);
                }

                //set the location to compare on the next location change.
                oldCountry = newCountry;
                //persist the country data for next location
                countryLoc = getSharedPreferences(COUNTRY_FILE, 0);
                SharedPreferences.Editor editor = countryLoc.edit();
                editor.putString("oldCountry", oldCountry);
                editor.apply();
            }
        }

        /**
         * onProviderDisabled method called when the location provider is disabled
         *
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        /**
         * onProviderEnabled method called when the location provider is enabled
         *
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        /**
         * onStatus changed method called when status of the location provider is changed
         *
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
}
