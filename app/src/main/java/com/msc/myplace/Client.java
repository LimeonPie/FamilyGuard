package com.msc.myplace;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class Client extends IntentService {

    private Firebase db;

    public static final String ACTION_CREATE_FAMILY = "com.msc.myplace.action.CREATE_FAMILY";
    public static final String ACTION_CREATE_LOCATION = "com.msc.myplace.action.CREATE_LOCATION";
    public static final String ACTION_JOIN_FAMILY = "com.msc.myplace.action.JOIN_FAMILY";
    public static final String ACTION_UPDATE_SELF = "com.msc.myplace.action.UPDATE_SELF";

    public static final String ACTION_FETCH_FAMILY = "com.msc.myplace.action.FETCH_FAMILY";
    public static final String ACTION_FETCH_USER = "com.msc.myplace.action.FETCH_USER";
    public static final String ACTION_FETCH_LOCATIONS_ALL = "com.msc.myplace.action.FETCH_LOCATIONS_ALL";
    public static final String ACTION_CREATE_LOCATION_LISTENERS = "com.msc.myplace.action.CREATE_LOCATION_LISTENERS";

    public static final String ACTION_FAMILY_FETCHED = "com.msc.myplace.action.FAMILY_FETCHED";
    public static final String ACTION_USER_FETCHED = "com.msc.myplace.action.USER_FETCHED";
    public static final String ACTION_LOCATIONS_ALL_FETCHED = "com.msc.myplace.action.LOCATIONS_ALL_FETCHED";
    public static final String ACTION_LOCATION_CREATED = "com.msc.myplace.action.LOCATION_CREATED";

    public static final String EXTRA_LOCATIONS_ALL = "com.msc.myplace.extra.LOCATIONS_ALL";
    public static final String EXTRA_LOCATION = "com.msc.myplace.extra.LOCATION";
    public static final String EXTRA_LOCATION_NAME = "com.msc.myplace.extra.LOCATION_NAME";
    public static final String EXTRA_LOCATION_ID = "com.msc.myplace.extra.LOCATION_ID";
    public static final String EXTRA_LOCATION_ASSIGNED = "com.msc.myplace.extra.LOCATION_ASSIGNED";

    public static final String EXTRA_FAMILY = "com.msc.myplace.extra.FAMILY";
    public static final String EXTRA_FAMILY_NAME = "com.msc.myplace.extra.FAMILY_NAME";
    public static final String EXTRA_FAMILY_ID = "com.msc.myplace.extra.FAMILY_ID";

    public static final String EXTRA_USER = "com.msc.myplace.extra.USER";
    public static final String EXTRA_USER_NAME = "com.msc.myplace.extra.USER_NAME";
    public static final String EXTRA_USER_ID = "com.msc.myplace.extra.USER_ID";

    public static final String EXTRA_LAT = "com.msc.myplace.extra.LAT";
    public static final String EXTRA_LNG = "com.msc.myplace.extra.LNG";
    public static final String EXTRA_RADIUS = "com.msc.myplace.extra.RADIUS";


    public static final String FIREBASE = "https://myplacem.firebaseio.com/";

    public Client() {
        super("Client");
    }

    public static void createNewFamily(Context context, String familyName, String userName) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_CREATE_FAMILY);
        intent.putExtra(EXTRA_FAMILY_NAME, familyName);
        intent.putExtra(EXTRA_USER_NAME, userName);
        context.startService(intent);
    }

    public static void createNewLocation(Context context, String locationName, double lat, double lng, double radius, ArrayList<String> userIds) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_CREATE_LOCATION);
        intent.putExtra(EXTRA_LOCATION_NAME, locationName);
        intent.putExtra(EXTRA_LAT, lat);
        intent.putExtra(EXTRA_LNG, lng);
        intent.putExtra(EXTRA_RADIUS, radius);
        intent.putExtra(EXTRA_LOCATION_ASSIGNED, userIds);
        context.startService(intent);
    }

    public static void createLocationListeners(Context context) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_CREATE_LOCATION_LISTENERS);
        context.startService(intent);
    }

    public static void joinFamily(Context context, String familyId, String userName) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_JOIN_FAMILY);
        intent.putExtra(EXTRA_FAMILY_ID, familyId);
        intent.putExtra(EXTRA_USER_NAME, userName);
        context.startService(intent);
    }

    public static void updateSelf(Context context, double lat, double lng) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_UPDATE_SELF);
        intent.putExtra(EXTRA_LAT, lat);
        intent.putExtra(EXTRA_LNG, lng);
        context.startService(intent);
    }

    public static void fetchLocationsAll(Context context) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_FETCH_LOCATIONS_ALL);
        context.startService(intent);
    }

    public static void fetchFamily(Context context) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_FETCH_FAMILY);
        context.startService(intent);
    }

    public static void fetchUser(Context context, String userId) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_FETCH_USER);
        intent.putExtra(EXTRA_USER_ID, userId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_CREATE_FAMILY:
                    String familyName = intent.getStringExtra(EXTRA_FAMILY_NAME);
                    String creatorName = intent.getStringExtra(EXTRA_USER_NAME);
                    handleFamilyCreate(familyName, creatorName);
                    break;
                case ACTION_CREATE_LOCATION:
                    String locationName = intent.getStringExtra(EXTRA_LOCATION_NAME);
                    double locLat = intent.getDoubleExtra(EXTRA_LAT, 0);
                    double locLng = intent.getDoubleExtra(EXTRA_LNG, 0);
                    double radius = intent.getDoubleExtra(EXTRA_RADIUS, 0);
                    ArrayList<String> ids = intent.getStringArrayListExtra(EXTRA_LOCATION_ASSIGNED);
                    handleLocationCreate(locationName, locLat, locLng, radius, ids);
                    break;
                case ACTION_CREATE_LOCATION_LISTENERS:
                    handleLocationListenersCreate();
                    break;
                case ACTION_JOIN_FAMILY:
                    String familyId = intent.getStringExtra(EXTRA_FAMILY_ID);
                    String userName = intent.getStringExtra(EXTRA_USER_NAME);
                    handleFamilyJoin(familyId, userName);
                    break;
                case ACTION_UPDATE_SELF:
                    double latitude = intent.getDoubleExtra(EXTRA_LAT, 0);
                    double longitude = intent.getDoubleExtra(EXTRA_LNG, 0);
                    handleUpdateSelf(latitude, longitude);
                    break;
                case ACTION_FETCH_FAMILY:
                    handleFamilyFetch();
                    break;
                case ACTION_FETCH_USER:
                    String userId = intent.getStringExtra(EXTRA_USER_ID);
                    handleUserFetch(userId);
                    break;
                case ACTION_FETCH_LOCATIONS_ALL:
                    handleLocationsFetch();
                default:
                    break;
            }
        }
    }

    private void handleUpdateSelf(final double lat, final double lng) {
        db = new Firebase(FIREBASE);
        String familyId = readPrefs(Constants.GROUP_ID);
        final String userId = readPrefs(Constants.USER_ID);
        final long time = System.currentTimeMillis();
        if (familyId != null && userId != null) {
            final Firebase family = db.child("groups").child(familyId);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot members = dataSnapshot.child("members");
                    for (DataSnapshot memberData : members.getChildren()) {
                        Member member = memberData.getValue(Member.class);
                        if (member.id.equals(userId)) {
                            // This is us
                            // Update only user's locations fields
                            String key = memberData.getKey();
                            family.child("members").child(key).child("lat").setValue(lat);
                            family.child("members").child(key).child("lng").setValue(lng);
                            family.child("members").child(key).child("lastUpdate").setValue(time);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("Firebase", firebaseError.getMessage());
                }
            };
            family.addListenerForSingleValueEvent(listener);
        }
    }

    private void handleLocationsFetch() {
        db = new Firebase(FIREBASE);
        String familyId = readPrefs(Constants.GROUP_ID);
        final String userId = readPrefs(Constants.USER_ID);
        if (familyId != null && userId != null) {
            final Firebase family = db.child("groups").child(familyId);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot members = dataSnapshot.child("members");
                    for (DataSnapshot memberData : members.getChildren()) {
                        Member member = memberData.getValue(Member.class);
                        if (member.id.equals(userId)) {
                            // Here is out guy
                            if (member.locations != null && member.locations.size() > 0) {
                                Intent intent = new Intent();
                                intent.setAction(ACTION_LOCATIONS_ALL_FETCHED);
                                intent.putExtra(EXTRA_LOCATIONS_ALL, member.locations);
                                sendBroadcast(intent);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("Firebase", firebaseError.getMessage());
                }
            };
            family.addListenerForSingleValueEvent(listener);
        }
    }

    private void handleLocationListenersCreate() {
        db = new Firebase(FIREBASE);
        String familyId = readPrefs(Constants.GROUP_ID);
        final String userId = readPrefs(Constants.USER_ID);
        if (familyId != null && userId != null) {
            final Firebase family = db.child("groups").child(familyId);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot members = dataSnapshot.child("members");
                    for (DataSnapshot memberData : members.getChildren()) {
                        Member member = memberData.getValue(Member.class);
                        if (member.id.equals(userId)) {
                            // Here is our guy
                            if (member.locations != null && member.locations.size() > 0) {
                                // Make listeners to locations
                                for (Location location: member.locations) {
                                    createLocationListeners(location);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("Firebase", firebaseError.getMessage());
                }
            };
            family.addListenerForSingleValueEvent(listener);
        }
    }

    private void handleFamilyFetch() {
        db = new Firebase(FIREBASE);
        String familyId = readPrefs(Constants.GROUP_ID);
        if (familyId != null) {
            final Firebase family = db.child("groups").child(familyId);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Group group = dataSnapshot.getValue(Group.class);

                    Intent intent = new Intent();
                    intent.setAction(ACTION_FAMILY_FETCHED);
                    intent.putExtra(EXTRA_FAMILY, group);
                    sendBroadcast(intent);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("Firebase", firebaseError.getMessage());
                }
            };
            family.addListenerForSingleValueEvent(listener);
        }
    }

    private void handleUserFetch(final String userId) {
        db = new Firebase(FIREBASE);
        String familyId = readPrefs(Constants.GROUP_ID);
        if (familyId != null) {
            final Firebase family = db.child("groups").child(familyId);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot members = dataSnapshot.child("members");
                    for (DataSnapshot memberData : members.getChildren()) {
                        Member member = memberData.getValue(Member.class);
                        if (member.id.equals(userId)) {
                            // This is us
                            Intent intent = new Intent();
                            intent.setAction(ACTION_USER_FETCHED);
                            intent.putExtra(EXTRA_USER, member);
                            sendBroadcast(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("Firebase", firebaseError.getMessage());
                }
            };
            family.addListenerForSingleValueEvent(listener);
        }
    }

    private void handleFamilyJoin(final String familyId, final String userName) {
        db = new Firebase(FIREBASE);
        final Firebase groups = db.child("groups");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot groupSnapshot: dataSnapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group.id.equals(familyId)) {
                        // Adding new user
                        Member user = new Member(userName);
                        group.addMember(user);
                        groups.child(familyId).child("members").setValue(group.members);

                        Toast.makeText(getApplicationContext(), "Joined a Family", Toast.LENGTH_SHORT).show();

                        writePrefs(Constants.GROUP_ID, group.id);
                        writePrefs(Constants.USER_ID, user.id);
                        openMainActivity();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Firebase", firebaseError.getMessage());
            }
        };

        groups.addListenerForSingleValueEvent(listener);
    }

    private void handleLocationCreate(final String locationName, final double lat, final double lng, final double radius, final ArrayList<String> userIds) {
        db = new Firebase(FIREBASE);
        String familyId = readPrefs(Constants.GROUP_ID);
        final String userId = readPrefs(Constants.USER_ID);
        if (familyId != null && userId != null) {
            final Firebase family = db.child("groups").child(familyId);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot members = dataSnapshot.child("members");
                    for (DataSnapshot memberData : members.getChildren()) {
                        Member member = memberData.getValue(Member.class);
                        if (member.id.equals(userId)) {
                            // This is us
                            // Firstly, create new Location
                            Location created = new Location(locationName, lat, lng, radius);
                            created.assigned = userIds;
                            // Add to temporal copy a new location
                            member.addLocation(created);
                            // Update only locations
                            String key = memberData.getKey();
                            family.child("members").child(key).child("locations").setValue(member.locations);
                            // And create listeners to assigned users locations
                            createLocationListeners(created);
                            // Send a signal that new location created and added
                            Intent intent = new Intent();
                            intent.setAction(ACTION_LOCATION_CREATED);
                            sendBroadcast(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("Firebase", firebaseError.getMessage());
                }
            };
            family.addListenerForSingleValueEvent(listener);
        }
    }

    private void createLocationListeners(final Location location) {
        db = new Firebase(FIREBASE);
        String familyId = readPrefs(Constants.GROUP_ID);
        for (final String targetId : location.assigned) {
            final Firebase family = db.child("groups").child(familyId);
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot members = dataSnapshot.child("members");
                    for (DataSnapshot memberData : members.getChildren()) {
                        Member member = memberData.getValue(Member.class);
                        if (member.id.equals(targetId)) {
                            // This is the target
                            String index = memberData.getKey();
                            final Firebase target = family.child("members").child(index);
                            // Add listeners to data change
                            target.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    double targetLat = dataSnapshot.child("lat").getValue(double.class);
                                    double targetLng = dataSnapshot.child("lng").getValue(double.class);
                                    float[] distance = new float[1];
                                    android.location.Location.distanceBetween(location.lat, location.lng, targetLat, targetLng, distance);
                                    if (distance[0] <= location.radius) {
                                        // Push notifications
                                        final String name = dataSnapshot.child("name").getValue(String.class);
                                        sendNotification("MyPlace", name + " enters in a " + location.name);
                                        target.removeEventListener(this);
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.e("Firebase", firebaseError.getMessage());
                }
            };
            family.addListenerForSingleValueEvent(listener);
        }
    }

    private void handleFamilyCreate(String familyName, String userName) {
        db = new Firebase(FIREBASE);

        // Creating new family with creator as first member
        final Group newFamily = new Group(familyName);
        final Member creator = new Member(userName);
        newFamily.addMember(creator);

        // Pushing to server
        Firebase familyRef = db.child("groups").child(newFamily.id);
        familyRef.setValue(newFamily, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.e("Firebase", firebaseError.getMessage());
                } else {
                    Log.i("Firebase", "Successfully created new group");
                    Toast.makeText(getApplicationContext(), "Created a new Family", Toast.LENGTH_SHORT).show();
                    writePrefs(Constants.GROUP_ID, newFamily.id);
                    writePrefs(Constants.USER_ID, creator.id);
                    openMainActivity();
                }
            }
        });
    }

    private void writePrefs(String key, String value) {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String readPrefs(String key) {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        return settings.getString(key, null);
    }

    private void sendNotification(String title, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());
    }

    private void openMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
