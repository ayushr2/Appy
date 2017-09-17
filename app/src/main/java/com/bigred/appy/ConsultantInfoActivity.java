package main.java.com.bigred.appy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigred.appy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;

import main.java.com.sinch.android.rtc.sample.video.BaseActivity;
import main.java.com.sinch.android.rtc.sample.video.CallScreenActivity;
import main.java.com.sinch.android.rtc.sample.video.SinchService;

/**
 * @author ayushranjan
 * @since 16/09/17.
 */

public class ConsultantInfoActivity extends BaseActivity implements SinchService.StartFailedListener {
    private ProgressDialog mSpinner;
    private Button callButton;
    private String consultantCleanID;
    private SharedPreferences settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultant_info_layout);
        consultantCleanID = getIntent().getExtras().getString(Constants.CONSULTANT_CLEAN_ID);
        settings = getSharedPreferences(Constants.PREF_NAME, 0);
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
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        final TextView nameView = (TextView) findViewById(R.id.consultant_name_view);
        final TextView scoreView = (TextView) findViewById(R.id.consultant_score_view);
        final TextView helpedView = (TextView) findViewById(R.id.consultant_helped_view);

        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_KEY).child(consultantCleanID);
        userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameView.setText(user.name);
                scoreView.setText("Score: " + String.valueOf(user.score));
                helpedView.setText("People Helped: " + String.valueOf(user.numHelped));
                Picasso.with(getApplicationContext()).load(Uri.parse(user.photoUriString))
                        .transform(new CircleTransformation()).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        callButton = (Button) findViewById(R.id.call_btn);
        callButton.setEnabled(false);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call call = getSinchServiceInterface().callUserVideo(consultantCleanID);
                String callId = call.getCallId();

                Intent callScreen = new Intent(getApplicationContext(), CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
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
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        mSpinner.dismiss();
        callButton.setEnabled(true);
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        logInToVideoCallService();
    }

    private void logInToVideoCallService() {
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(settings.getString(Constants.CLEAN_EMAIL, ""));
            showSpinner();
        } else {
            if (mSpinner != null) {
                mSpinner.dismiss();
            }
            callButton.setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
