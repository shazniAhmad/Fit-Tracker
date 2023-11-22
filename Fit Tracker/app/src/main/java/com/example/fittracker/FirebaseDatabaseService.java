package com.example.fittracker;

import android.content.Context;
import android.provider.Settings;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseService {
    FirebaseDatabase database;
    String android_id;

    public FirebaseDatabaseService(Context context) {
        database = FirebaseDatabase.getInstance("https://fit-tracker-48458-default-rtdb.firebaseio.com/");
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public boolean write(String path, String value) {
        DatabaseReference myRef = database.getReference(android_id + '/' + path);
        myRef.setValue(value);
        return true;
    }

    public DatabaseReference getDatabaseReference(String path) {

        final DataSnapshot[] snapshot = new DataSnapshot[1];
        DatabaseReference myRef = database.getReference(android_id + '/' + path);
        final String[] result = {""};
        return myRef;
    }

}
