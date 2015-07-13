package com.pawnua.weathermap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick on 18.11.2014.
 */

public abstract class GooglePlusActivity extends AppCompatActivity implements GooglePlayServicesClient.ConnectionCallbacks, PlusClient.OnAccessRevokedListener,
        GooglePlayServicesClient.OnConnectionFailedListener, View.OnClickListener {

    final String TAG = "NickPeshkovLogs";

    //SignIn Button Id from Activity
    int signInButtonId;

    boolean isSignIn = false;

    //Google Plus API Classes Used
    private ProgressDialog mConnectionProgressDialog;
    public PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    //Request Codes for Intents
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    //Managing Periodic Connection Status and User Info
    String previousEmail = "";

    Drawable mDrawable;
    /**
     * Methods for Activity
     * onCreate, onActivityResult, onStart, onPause, onResume
     * More can be implemented from inheritee
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mPlusClient = new PlusClient.Builder(this, this, this)
                .setScopes(Scopes.PROFILE, Scopes.PLUS_LOGIN) // Space separated list of scopes
                .build();
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");
        onCreateExtended(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_CODE_RESOLVE_ERR:
                if (resultCode == RESULT_OK) {
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
        }
        onActivityResultExtended(requestCode, resultCode, data);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }
    /**
     * Methods required by GooglePlusAPI
     */
//Google+ Connection successful
    public void onConnected(Bundle connectionHint) {
        mConnectionProgressDialog.dismiss();
        getUserInformation();
        if (!previousEmail.equals(mPlusClient.getAccountName())) {
            Log.d(TAG, "GooglePlusActivity: " + mPlusClient.getAccountName() + ", you connected!");
            previousEmail = mPlusClient.getAccountName();

            if (mPlusClient.getCurrentPerson() != null && mPlusClient.getCurrentPerson().hasImage()) {
                ConnectImage mConnectImage = new ConnectImage();
                mConnectImage.execute(mPlusClient.getCurrentPerson().getImage().getUrl());
                try {
                    mDrawable = mConnectImage.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
/*            if (mPlusClient.getCurrentPerson() == null) {
                Toast.makeText(this, mPlusClient.getAccountName() + ", you connected!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, mPlusClient.getCurrentPerson().getName().getGivenName() + ", you connected!", Toast.LENGTH_LONG).show();

            }
*/
        }
        isSignIn = true;
        onConnectionStatusChanged();
    }
    //Google+ Connection Disconnected
    public void onDisconnected() {
        isSignIn = false;
        onConnectionStatusChanged();
    }
    //Google+ Connection Failed
    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (mConnectionProgressDialog.isShowing()) {
// The user clicked the sign-in button already. Start to resolve
// connection errors. Wait until onConnected() to dismiss the
// connection dialog.
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    mPlusClient.connect();
                }
            }
            mConnectionProgressDialog.dismiss();
        }
// Save the intent so that we can start an activity when the user clicks
// the sign-in button.
        mConnectionResult = result;
    }
    //On GooglePlus AccessRevoked
    public void onAccessRevoked(ConnectionResult status) {
// mPlusClient is now disconnected and access has been revoked.
// Trigger app logic to comply with the developer policies
    }
    /**
     * Signing in and out
     */
//Signing In to Google+
    public void signIn() {
        mPlusClient.disconnect();
        if (!mPlusClient.isConnected()) { //Create a new Story
            if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
                mPlusClient.connect();
            } else {
                try {
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
// Try connecting again.
                    mConnectionResult = null;
//signIn();
                    mPlusClient.connect();
                }
            }
        }
    }
    //Signing Out of Google+
    public void signOut() {
        Log.i("isConnected", Boolean.toString(mPlusClient.isConnected()));
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.revokeAccessAndDisconnect(new PlusClient.OnAccessRevokedListener() {
                @Override
                public void onAccessRevoked(ConnectionResult connectionResult) {
// mPlusClient is now disconnected and access has been revoked.
// Trigger app logic to comply with the developer policies
                }
            });
            mPlusClient.disconnect();
//mPlusClient.connect();
            Toast.makeText(this, "Successfully Signed Out", Toast.LENGTH_LONG).show();
            isSignIn = false;
        }
        onConnectionStatusChanged();
    }
    /**
     * Required by View.onClickListener
     */
    @Override
    public void onClick(View view) {

        if (view.getId() == signInButtonId) {
            signIn();
        }
    }

    private class ConnectImage extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... params) {
            // TODO Auto-generated method stub
            String url = params[0];
            url = url.replace("sz=50", "sz=200");
            try {
                InputStream is = (InputStream)new URL(url).getContent();
                Drawable d = Drawable.createFromStream(is, "src name");
                return d;
            } catch (Exception e) {
                Log.e("MainActivity",e.toString());
                return null;
            }
        }

    }
    /**
     * Methods to be implemented by inheritee
     */
    public abstract void onConnectionStatusChanged();
    public abstract void onActivityResultExtended(int requestCode, int resultCode, Intent data);
    public abstract void onCreateExtended(Bundle savedInstanceState);
    public abstract void getUserInformation();
}