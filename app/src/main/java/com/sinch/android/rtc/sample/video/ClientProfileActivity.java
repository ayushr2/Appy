package main.java.com.sinch.android.rtc.sample.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bigred.appy.R;

public class ClientProfileActivity extends BaseActivity {

    private Button mEditButton;
    //private EditText mCallName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientprofile);

        mEditButton = (Button) findViewById(R.id.editButton);
        mEditButton.setEnabled(true);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
    }

    private void loginClicked() {
        openPlaceCallActivity();
    }

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, ClientProfileEditActivity.class);
        startActivity(mainActivity);
    }

}
