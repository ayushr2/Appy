package com.bigred.appy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

/**
 * @author Ayush Ranjan
 * @since 16/09/17.
 */

public class ConsultantHomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private SharedPreferences settings;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultant_home_layout);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        setUI();
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
                if (mGoogleApiClient.isConnected()) {
                    signOut();
                    Intent intent = new Intent(getApplicationContext(), GoogleSignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

            }
        });
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


    @Override
    protected void onDestroy() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.AVAILABLE).child(settings.getString(Constants.CLEAN_EMAIL, ""));
        databaseReference.setValue(null);
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
