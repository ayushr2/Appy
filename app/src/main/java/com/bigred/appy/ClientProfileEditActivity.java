package com.sinch.android.rtc.sample.video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

public class ClientProfileEditActivity extends BaseActivity{

   // privateArrayList<string> schedule;
    //privateArrayAdapter<string> adapter;
    private Button mLoginButton;
    private EditText mLoginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientedit);

        //ListView listView = (ListView)findViewById(R.id.)

        mLoginName = (EditText) findViewById(R.id.aboutyou);

        mLoginButton = (Button) findViewById(R.id.endEditButton);
        mLoginButton.setEnabled(true);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
    }

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, ClientProfileActivity.class);
        startActivity(mainActivity);
    }

    private void loginClicked() {
        String aboutme = mLoginName.getText().toString();

        System.out.println("About me: " + aboutme);
        openPlaceCallActivity();
    }
}
