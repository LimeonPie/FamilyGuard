package com.msc.myplace;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CreateLocation extends AppCompatActivity {

    private EditText locNameEdit;
    private EditText locLatEdit;
    private EditText locLngEdit;
    private EditText locRadiusEdit;
    private ListView userListView;
    private UserListAdapter userListViewAdapter;

    // To store selected users
    private ArrayList<String> selectedIds;

    private class UserListAdapter extends ArrayAdapter<Member> {

        private Context context;
        private ArrayList<Member> items;

        public UserListAdapter(Context context, ArrayList<Member> members) {
            super(context, R.layout.user_row_with_checkbox, members);
            this.context = context;
            this.items = members;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.user_row_with_checkbox, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.userNameLabel);
            name.setText(items.get(position).name);
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);

        locNameEdit = (EditText) findViewById(R.id.txt_location_name);
        locLatEdit = (EditText) findViewById(R.id.txt_lat);
        locLngEdit = (EditText) findViewById(R.id.txt_lon);
        locRadiusEdit = (EditText) findViewById(R.id.txt_radius);
        selectedIds = new ArrayList<>(0);

        userListView = (ListView) findViewById(R.id.assignedListView);
        userListViewAdapter = new UserListAdapter(this, new ArrayList<Member>(0));
        userListView.setAdapter(userListViewAdapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.userCheckBox);
                cb.setChecked(!cb.isChecked());
                String clickedId = userListViewAdapter.getItem(position).id;
                if (cb.isChecked()) {
                    if (!selectedIds.contains(clickedId)) selectedIds.add(clickedId);
                }
                else {
                    if (selectedIds.contains(clickedId)) selectedIds.remove(clickedId);
                }
            }
        });

        // Fetching members
        BroadcastReceiver callback = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Group family = (Group) intent.getSerializableExtra(Client.EXTRA_FAMILY);
                if (family != null) {
                    updateListView(family.members);
                }
                unregisterReceiver(this);
            }
        };
        registerReceiver(callback, new IntentFilter(Client.ACTION_FAMILY_FETCHED));
        Client.fetchFamily(this);

        Button createButton = (Button) findViewById(R.id.btn_create_location);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationName = locNameEdit.getText().toString();
                double locationLat = Double.parseDouble(locLatEdit.getText().toString());
                double locationLng = Double.parseDouble(locLngEdit.getText().toString());
                double locationRadius = Double.parseDouble(locRadiusEdit.getText().toString());
                Client.createNewLocation(getApplicationContext(), locationName, locationLat, locationLng, locationRadius, selectedIds);
            }
        });
    }

    private void updateListView(ArrayList<Member> members) {
        for (Member member : members) {
            userListViewAdapter.add(member);
        }
    }
}
