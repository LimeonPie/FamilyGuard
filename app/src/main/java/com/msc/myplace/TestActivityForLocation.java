package com.msc.myplace;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;
import com.msc.myplace.LocationReceiver;

public class TestActivityForLocation extends AppCompatActivity {
    private PendingIntent pending_intent;
    private AlarmManager alarm_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_activity_for_location);

        this.alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, LocationPoller.class);

        Bundle bundle = new Bundle();
        LocationPollerParameter parameter = new LocationPollerParameter(bundle);
        parameter.setIntentToBroadcastOnCompletion( new Intent(this, LocationReceiver.class) );
        parameter.setProviders(
                new String[]{
                    LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER
        });
        parameter.setTimeout(60000);
        intent.putExtras(bundle);

        this.pending_intent = PendingIntent.getBroadcast(this, 0, intent, 0);
        this.alarm_manager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                // 1 minute
                60000,
                this.pending_intent);
    }
}
