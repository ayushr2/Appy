package com.bigred.appy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ayushranjan on 16/09/17.
 */

public class ClientHomeActivity extends Activity {

    ArrayList<User> users = new ArrayList<>();
    OnlineListAdapter onlineListAdapter;
    ImageView profilePictureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_home_layout);

        setProfilePicture();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        onlineListAdapter = new OnlineListAdapter(users);
        mRecyclerView.setAdapter(onlineListAdapter);

        updateRecycleView();
    }

    private void setProfilePicture() {
        profilePictureView = (ImageView) findViewById(R.id.profile_pic_view);
        SharedPreferences settings = getSharedPreferences(Constants.PREF_NAME, 0);
        Picasso.with(getApplicationContext()).load(Uri.parse(settings.getString(Constants.PHOTO_URI_STRING, "")))
                .transform(new CircleTransformation()).into(profilePictureView);
    }

    private void updateRecycleView() {
        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.AVAILABLE);
        userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> onlineConsultants = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    onlineConsultants.add(child.getKey());
                }
                if (onlineConsultants.size() == 0)
                    Toast.makeText(getApplicationContext(), "There are no available consultants. Please try again later!", Toast.LENGTH_SHORT).show();
                else
                    recurseOnlineConsultants(onlineConsultants, 0, onlineConsultants.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), getString(R.string.firebase_database_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recurseOnlineConsultants(final ArrayList<String> consultants, final int i, final int size) {
        if (i >= size) {
            onlineListAdapter.notifyDataSetChanged();
            return;
        }

        DatabaseReference consultantRef = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS_KEY).child(consultants.get(i));
        consultantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                users.add(user);
                recurseOnlineConsultants(consultants, i + 1, size);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}