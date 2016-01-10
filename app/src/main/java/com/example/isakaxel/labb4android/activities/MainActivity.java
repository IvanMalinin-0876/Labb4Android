package com.example.isakaxel.labb4android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import com.example.isakaxel.labb4android.Model.Model;
import com.example.isakaxel.labb4android.R;
import com.example.isakaxel.labb4android.services.RegistrationIntentService;
import com.example.isakaxel.labb4android.services.SendGcmService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private Context context;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount userAccount;
    private BroadcastReceiver gcmBroadcastReceiver;
    private boolean appServerContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.googleButton).setOnClickListener(this);

        context = getApplicationContext();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("602319958990-p52mb9lu57pr5iklbt7h76uvdvoob6hd.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        gcmBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean("sentTokenToServer", false);
                if (sentToken) {
                    Log.i("sentToken", "True");
                } else {
                    Log.i("sentToken", "Error");
                }
                appServerContact = sharedPreferences.getBoolean("serverLoginSuccess", false);
                if (appServerContact) {
                    enableUI(appServerContact);
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Login result
        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.i("intentResult", "success");
                userAccount = result.getSignInAccount();
                if (hasPlayServices()) {
                    Intent regIntent = new Intent(this, RegistrationIntentService.class);
                    regIntent.putExtra("userEmail", userAccount.getEmail());
                    startService(regIntent);
                    Intent sendLoginGcm = new Intent(this, SendGcmService.class);
                    sendLoginGcm.putExtra("action", "login");
                    sendLoginGcm.putExtra("userEmail", userAccount.getEmail());
                    startService(sendLoginGcm);
                }
            } else {
                Log.i("intentResult", "" + result.getStatus().getStatusCode());
                signOut();
                revokeAccess();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(gcmBroadcastReceiver,
                new IntentFilter("registrationComplete"));


    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gcmBroadcastReceiver);
    }

    public void enableUI(boolean isSignedIn) {
        if (isSignedIn) {
            Intent inboxIntent = new Intent(this, InboxActivity.class);
            inboxIntent.putExtra("userEmail", userAccount.getEmail());
            startActivity(inboxIntent);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("onConnectionFailed", "" + connectionResult.getErrorCode());
    }

    private boolean hasPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(errorCode)) {
                apiAvailability.getErrorDialog(this, errorCode, 1);
            } else {
                Log.i("onResume", "This device is not supported");
                finish();
            }
            return false;
        }
        return true;
    }

    public void signIn() {
        // Login intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, 9001);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        enableUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        enableUI(false);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleButton:
                signIn();
                break;
        }
    }
}
