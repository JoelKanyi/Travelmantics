package com.example.travelmantics;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {



    //all members of this class are static because we will be able to call them without instantiating them
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil mFirebaseUtil;
    private static FirebaseAuth mFirebaseAuth;
    private static Activity caller;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static ArrayList<TravelDeal> mDeals;


    //to prevent this class from being instantiated from outside of this class
    private FirebaseUtil(){}

    public static void openFirebaseReference(String ref, final Activity callerActivity){
        if (mFirebaseUtil==null){
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                            );
                    caller = callerActivity;

// Create and launch sign-in intent
                    caller.startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            };
        }
        mDeals = new ArrayList<TravelDeal>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static void attachListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
