package com.bigred.appy;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by ayushranjan on 16/09/17.
 */

public class ConsultantInfoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultant_info_layout);
        setUI();
    }

    private void setUI() {
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        final TextView nameView = (TextView) findViewById(R.id.consultant_name_view);
        final TextView scoreView = (TextView) findViewById(R.id.consultant_score_view);
        final TextView helpedView = (TextView) findViewById(R.id.consultant_helped_view);

        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_KEY).child(getIntent().getExtras().getString(Constants.CONSULTANT_CLEAN_ID));
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

        Button callButton = (Button) findViewById(R.id.call_btn);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call handle
            }
        });
    }
}
