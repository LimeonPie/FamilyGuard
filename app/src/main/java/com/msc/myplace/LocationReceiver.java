package com.msc.myplace;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.commonsware.cwac.locpoll.LocationPollerResult;

public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context_, Intent intent_) {
        Bundle bundle = intent_.getExtras();
        LocationPollerResult result = new LocationPollerResult(bundle);
        Location location = result.getLocation();
        // If new location is not available, try to get the last known location
        if (null == location) {
            location = result.getLastKnownLocation();
        }
        if (null != location) {
            Log.d("LOCATION", location.toString());
        }
    }
}
