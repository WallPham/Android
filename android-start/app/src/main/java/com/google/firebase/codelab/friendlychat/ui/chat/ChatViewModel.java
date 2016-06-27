package com.google.firebase.codelab.friendlychat.ui.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.codelab.friendlychat.model.CodelabPreferences;
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage;
import com.google.firebase.codelab.friendlychat.ui.base.BaseViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tuongpv on 6/19/2016.
 */
public class ChatViewModel extends BaseViewModel{

    private static final String TAG = ChatViewModel.class.toString();
    public static final String MESSAGES_CHILD = "messages";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";

    private static final String MESSAGE_SENT_EVENT = "message_sent";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private SharedPreferences mSharedPreferences;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private DatabaseReference mFirebaseDatabaseReference;

    private String mUsername =ANONYMOUS;
    private String mPhotoUrl;

    private ChatViewModelListener mListener;

    public ChatViewModel(Context context, ChatViewModelListener listener) {

        mListener = listener;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mUsername = getUserName();
        mPhotoUrl = getPhotoUrl();
        // Fetch remote config.
        fetchConfig();
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {

        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that
        // each fetch goes to the server. This should not be used in release
        // builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings()
                .isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via
                        // FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config: " +
                                e.getMessage());
                        applyRetrievedLengthLimit();
                    }
                });
    }

    public void sendMessage(String message) {

        FriendlyMessage friendlyMessage = new
                FriendlyMessage(message,
                mUsername,
                mPhotoUrl);
        getDatabaseReference()
                .push().setValue(friendlyMessage);
    }

    public String getUserName() {

        if (mFirebaseUser != null) {
            return mFirebaseUser.getDisplayName();
        }
        return ANONYMOUS;

    }

    public String getPhotoUrl() {

        Uri uri =mFirebaseUser.getPhotoUrl();
        if(uri != null)
        {
            return uri.toString();
        }
        return null;
    }

    private void applyRetrievedLengthLimit() {

        if (mListener == null){
            return;
        }
        Long friendly_msg_length =
                mFirebaseRemoteConfig.getLong("friendly_msg_length");
        mListener.onApplyRetrievedLengthLimit(friendly_msg_length.intValue());

        Log.d(TAG, "FML is: " + friendly_msg_length);
    }

    public int getDefaultMsgLengthLimit() {

        return mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT);
    }

    public void signOut() {

        mFirebaseAuth.signOut();
        mUsername = ANONYMOUS;
    }

    public DatabaseReference getDatabaseReference() {

        if (mFirebaseDatabaseReference == null) {
            return  null;
        }
        return mFirebaseDatabaseReference.child(MESSAGES_CHILD);
    }

    interface ChatViewModelListener {

        void onApplyRetrievedLengthLimit( int friendly_msg_length);
    }
}
