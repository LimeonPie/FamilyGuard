package com.msc.myplace;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Client extends IntentService {

    private Firebase db;

    public static final String ACTION_CREATE_FAMILY = "com.msc.myplace.action.CREATE_FAMILY";
    public static final String ACTION_JOIN_FAMILY = "com.msc.myplace.action.JOIN_FAMILY";
    public static final String ACTION_UPDATE_SELF = "com.msc.myplace.action.UPDATE_SELF";
    public static final String ACTION_FETCH_FAMILY = "com.msc.myplace.action.FETCH_FAMILY";
    public static final String ACTION_FAMILY_FETCHED = "com.msc.myplace.action.FAMILY_FETCHED";

    public static final String EXTRA_FAMILY_NAME = "com.msc.myplace.extra.FAMILY_NAME";
    public static final String EXTRA_GROUP = "com.msc.myplace.extra.GROUP";
    public static final String EXTRA_USER_NAME = "com.msc.myplace.extra.USER_NAME";
    public static final String EXTRA_FAMILY_ID = "com.msc.myplace.extra.FAMILY_ID";
    public static final String EXTRA_USER_ID = "com.msc.myplace.extra.USER_ID";
    public static final String EXTRA_LAT = "com.msc.myplace.extra.LAT";
    public static final String EXTRA_LNG = "com.msc.myplace.extra.LNG";


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

    public static void joinFamily(Context context, String familyId, String userName) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_JOIN_FAMILY);
        intent.putExtra(EXTRA_FAMILY_ID, familyId);
        intent.putExtra(EXTRA_USER_NAME, userName);
        context.startService(intent);
    }

    public static void fetchFamily(Context context) {
        Intent intent = new Intent(context, Client.class);
        intent.setAction(ACTION_FETCH_FAMILY);
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
                case ACTION_JOIN_FAMILY:
                    String familyId = intent.getStringExtra(EXTRA_FAMILY_ID);
                    String userName = intent.getStringExtra(EXTRA_USER_NAME);
                    handleFamilyJoin(familyId, userName);
                    break;
                case ACTION_UPDATE_SELF:
                    handleUpdateSelf();
                    break;
                case ACTION_FETCH_FAMILY:
                    handleFamilyFetch();
                    break;
                default:
                    break;
            }
        }
    }

    private void handleUpdateSelf() {

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
                    Log.d("Firebase", "Received data");
                    Intent intent = new Intent();
                    intent.setAction(ACTION_FAMILY_FETCHED);
                    intent.putExtra(EXTRA_GROUP, group);
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
                        groups.child(group.id).setValue(group);

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

    private void handleFamilyCreate(String familyName, String userName) {
        db = new Firebase(FIREBASE);

        // Creating new family with creator as first member
        final Group newFamily = new Group(familyName);
        final Member creator = new Member(userName);
        newFamily.addMember(creator);

        // Pushing to server
        Firebase familyRef = db.child("groups").child(newFamily.id);
        familyRef.setValue(newFamily);
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

    private void openMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
