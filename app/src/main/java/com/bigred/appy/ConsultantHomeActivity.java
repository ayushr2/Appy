package com.bigred.appy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.sample.video.BaseActivity;
import com.sinch.android.rtc.sample.video.SinchService;
import com.squareup.picasso.Picasso;

/**
 * @author Ayush Ranjan
 * @since 16/09/17.
 */

public class ConsultantHomeActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, SinchService.StartFailedListener {
    private SharedPreferences settings;
    GoogleApiClient mGoogleApiClient;
    private ProgressDialog mSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultant_home_layout);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        setUI();
        requestPermissions();
    }

    private void requestPermissions() {
        int writeExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED || recordAudioPermission != PackageManager.PERMISSION_GRANTED
                || cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 1);
        }
    }

    private void setUI() {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView nameView = (TextView) findViewById(R.id.consultant_name_view);
        final TextView scoreView = (TextView) findViewById(R.id.consultant_score_view);
        final TextView helpedView = (TextView) findViewById(R.id.consultant_helped_view);
        settings = getSharedPreferences(Constants.PREF_NAME, 0);
        Picasso.with(getApplicationContext()).load(Uri.parse(settings.getString(Constants.PHOTO_URI_STRING, "")))
                .transform(new CircleTransformation()).into(imageView);
        nameView.setText(settings.getString(Constants.NAME, ""));
        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_KEY).child(settings.getString(Constants.CLEAN_EMAIL, ""));
        userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                scoreView.setText("Score: " + String.valueOf(user.score));
                helpedView.setText("People Helped: " + String.valueOf(user.numHelped));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button signOutButton = (Button) findViewById(R.id.log_out_btn);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutAndFinish();

            }
        });
    }

    private void signOutAndFinish() {
        if (mGoogleApiClient.isConnected()) {
            signOut();
        }
        Intent intent = new Intent(getApplicationContext(), GoogleSignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Signs out the current user and updates the user upon logout.
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in to video call service");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        addToAvailable();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        addToAvailable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addToAvailable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        removeFromAvailable();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        removeFromAvailable();
    }

    @Override
    public void onStarted() {
        mSpinner.dismiss();
        addToAvailable();
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        logInToVideoCallService();
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        removeFromAvailable();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        removeFromAvailable();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        signOutAndFinish();
        removeFromAvailable();
    }

    private void addToAvailable() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.AVAILABLE).child(settings.getString(Constants.CLEAN_EMAIL, ""));
        databaseReference.setValue(true);
    }

    private void removeFromAvailable() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.AVAILABLE).child(settings.getString(Constants.CLEAN_EMAIL, ""));
        databaseReference.setValue(null);
    }

    private void logInToVideoCallService() {
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(settings.getString(Constants.CLEAN_EMAIL, ""));
            showSpinner();
        } else {
            if (mSpinner != null) {
                mSpinner.dismiss();
            }
        }
    }
}
