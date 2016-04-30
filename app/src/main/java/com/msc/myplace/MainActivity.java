package com.msc.myplace;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.msc.myplace.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MapFragment mapFragment;
    private MapHandler mapHandler;
    private Group family;
    private FloatingActionsMenu fabMenu;
    private List<BroadcastReceiver> fabReceivers;


    // Wrapper for Google Maps
    private class MapHandler extends FragmentActivity
            implements OnMapReadyCallback {

        private GoogleMap googleMap;

        @Override
        public void onMapReady(GoogleMap map) {
            googleMap = map;
            /*CameraUpdate zoomTo = CameraUpdateFactory.zoomTo(14f);
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(0, 0))
                    .title("Marker"));
            googleMap.animateCamera(zoomTo);*/
        }

        // Move camera to certain location
        public void moveTo(double lat, double lng) {
            LatLng destination = new LatLng(lat, lng);
            CameraUpdate moveVector = CameraUpdateFactory.newLatLngZoom(destination, 14f);
            googleMap.addMarker(new MarkerOptions().position(destination).title("I am here!"));
            googleMap.animateCamera(moveVector);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab);
        fabReceivers = new ArrayList<>(0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapHandler = new MapHandler();
        mapFragment.getMapAsync(mapHandler);

        // Start the daemon to get the location of this device
        startService(new Intent(this, LocationHandler.class));

        fetchFamily();
        moveToMe();
    }

    private void fetchFamily() {
        BroadcastReceiver callback = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                family = (Group) intent.getSerializableExtra(Client.EXTRA_GROUP);
                buildFloatingButtons();
            }
        };
        registerReceiver(callback, new IntentFilter(Client.ACTION_FAMILY_FETCHED));
        Client.fetchFamily(this);
    }

    private void buildFloatingButtons() {
        removeFabButtons();
        for (final Member member: family.members) {
            FloatingActionButton fab = new FloatingActionButton(this);
            final String name = member.name;
            fab.setTitle(name);
            fab.setSize(FloatingActionButton.SIZE_MINI);
            fab.setIcon(R.mipmap.ic_map_marker_white);
            fab.setColorNormalResId(R.color.colorAccent);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Tap on " + name, Toast.LENGTH_SHORT).show();
                    BroadcastReceiver callback = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Member user = (Member) intent.getSerializableExtra(Client.EXTRA_USER);
                            mapHandler.moveTo(user.lat, user.lng);
                        }
                    };
                    registerReceiver(callback, new IntentFilter(Client.ACTION_USER_FETCHED));
                    Client.fetchUser(getApplicationContext(), member.id);
                    fabReceivers.add(callback);
                }
            });
            fabMenu.addButton(fab);
        }
    }

    private void moveToMe() {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        String userId = settings.getString(Constants.USER_ID, null);
        BroadcastReceiver callback = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Member user = (Member) intent.getSerializableExtra(Client.EXTRA_USER);
                mapHandler.moveTo(user.lat, user.lng);
                unregisterReceiver(this);
            }
        };
        registerReceiver(callback, new IntentFilter(Client.ACTION_USER_FETCHED));
        Client.fetchUser(getApplicationContext(), userId);
    }

    private void removeFabButtons() {
        // There is no way to just delete all menu items
        // Try that, but it seems dangerous
        unregisterFabReceivers();
        for (int i = 0; i < fabMenu.getChildCount(); i++) {
            FloatingActionButton fab = (FloatingActionButton) fabMenu.getChildAt(i);
            fabMenu.removeButton(fab);
        }
    }

    private void unregisterFabReceivers() {
        for (BroadcastReceiver receiver: fabReceivers) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        unregisterFabReceivers();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.familyMenuItem:
                break;
            case R.id.locationsMenuItem:
                Intent intent = new Intent(this, LocationsList.class);
                startActivity(intent);
                break;
            case R.id.settingsMenuItem:
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
