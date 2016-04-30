package com.msc.myplace;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class Client extends IntentService {

    private Firebase db;

    public static final String ACTION_CREATE_FAMILY = "com.msc.myplace.action.CREATE_FAMILY";
    public static final String ACTION_JOIN_FAMILY = "com.msc.myplace.action.JOIN_FAMILY";

    public static final String EXTRA_FAMILY_NAME = "com.msc.myplace.extra.FAMILY_NAME";
    public static final String EXTRA_USER_NAME = "com.msc.myplace.extra.USER_NAME";
    public static final String EXTRA_FAMILY_ID = "com.msc.myplace.extra.FAMILY_ID";

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
                default:
                    break;
            }
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

                        Toast.makeText(getApplicationContext(), "Joined", Toast.LENGTH_SHORT).show();
                        // We found existing family
                        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Constants.GROUP_ID, group.id);
                        editor.commit();

                        // Redirect to MainActivity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
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
        Member creator = new Member(userName);
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
                    Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_SHORT).show();
                    // Writing newly created family
                    SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.GROUP_ID, newFamily.id);
                    editor.commit();

                    // Redirect to MainActivity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}
