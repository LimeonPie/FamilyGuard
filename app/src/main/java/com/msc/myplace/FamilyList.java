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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FamilyList extends AppCompatActivity {

    private ListView userListView;
    private UserListAdapter userListViewAdapter;

    private class UserListAdapter extends ArrayAdapter<Member> {

        private Context context;
        private ArrayList<Member> items;

        public UserListAdapter(Context context, ArrayList<Member> members) {
            super(context, android.R.layout.simple_list_item_2, members);
            this.context = context;
            this.items = members;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            if(convertView == null) {
                LayoutInflater inflater = ((Activity)this.context).getLayoutInflater();
                row = (View)inflater.inflate(android.R.layout.two_line_list_item, parent, false);
            } else {
                row = (View)convertView;
            }
            TextView v = (TextView) row.findViewById(android.R.id.text1);
            v.setText(this.items.get(position).name);
            v = (TextView) row.findViewById(android.R.id.text2);
            // TODO location of the member should be added here
            v.setText("Undefined location");
            return row;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_list);

        this.userListView = (ListView) findViewById(R.id.list_family);
        this.userListViewAdapter = new UserListAdapter(this, new ArrayList<Member>(0));
        this.userListView.setAdapter(this.userListViewAdapter);

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
    }

    private void updateListView(ArrayList<Member> members) {
        for (Member member : members) {
            userListViewAdapter.add(member);
        }
    }
}
