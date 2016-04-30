package com.msc.myplace;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationHandler extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient google_api_client;
    private LocationRequest location_request;

    public LocationHandler() {
        super("LocationHandler");
    }

    @Override
    public void onDestroy() {
        if(this.google_api_client.isConnected()) {
            this.google_api_client.disconnect();
        }
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LOCATION", "starting...?");
        this.google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.google_api_client.connect();

        this.location_request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        //return super.onStartCommand(intent, flags, startId);
        // TODO
        // not sure about this...
        return START_NOT_STICKY;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = null;
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(this.google_api_client);
        } catch (java.lang.SecurityException ex) {
            // no permissions...
        }
        if(null != location) {
            this.handleNewLocation(location);
        } else {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        this.google_api_client,
                        this.location_request,
                        this);
            } catch(java.lang.SecurityException ex) {
                // no permissions...
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Handle the newly received location
    private void handleNewLocation(Location location_)
    {
        Log.d("LOCATION", location_.toString());
    }
}
