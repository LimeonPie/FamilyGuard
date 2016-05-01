package com.msc.myplace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class LocationsList extends AppCompatActivity {
    private Button btn_add_location;
    private ListView list_view;
    private ArrayList<String> locations_list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);
        this.btn_add_location = (Button) findViewById(R.id.btn_add_location);

        this.btn_add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateLocation.class);
                startActivity(intent);
            }
        });

        this.list_view = (ListView) findViewById(R.id.list_locations);
        this.adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                this.locations_list
        );
        this.list_view.setAdapter(this.adapter);

        // Fetching locations
        BroadcastReceiver callback = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<Location> locations = (ArrayList<Location>) intent.getSerializableExtra(Client.EXTRA_LOCATIONS_ALL);
                updateListView(locations);
                unregisterReceiver(this);
            }
        };
        registerReceiver(callback, new IntentFilter(Client.ACTION_LOCATIONS_ALL_FETCHED));
        Client.fetchLocationsAll(this);
    }

    private void updateListView(final ArrayList<Location> locations) {
        if (locations != null && locations.size() > 0) {
            for (Location location:locations) {
                adapter.add(location.name);
            }
        }
    }
}
