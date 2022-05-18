package com.example.frieght.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    public static DatabaseReference getCustomOrderReference() {
        return FirebaseDatabase.getInstance().getReference()
                .child("fravellers")
                .child(FirebaseAuth.getInstance().getUid());
    }
}
