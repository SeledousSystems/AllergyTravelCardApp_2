package com.wright.paul.allergytravelcardapp.model;

import android.app.IntentService;
import android.content.Intent;

/**
 * Class that defines the intent for starting the Location Service
 */
public class LocationServiceIntent extends IntentService {

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public LocationServiceIntent() {
        super("LocationIntentService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
