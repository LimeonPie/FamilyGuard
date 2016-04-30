package com.msc.myplace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LocationsList extends AppCompatActivity {
    private ListView list_view;
    private ArrayList<String> locations_list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_list);
        this.list_view = (ListView) findViewById(R.id.list_locations);
        this.adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                this.locations_list
        );
        this.list_view.setAdapter(this.adapter);

        // Add the items here
        this.adapter.add("lorem ipsum");
    }
}
