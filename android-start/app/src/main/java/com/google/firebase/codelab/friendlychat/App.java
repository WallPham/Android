package com.google.firebase.codelab.friendlychat;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by tuongpv on 6/19/2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
