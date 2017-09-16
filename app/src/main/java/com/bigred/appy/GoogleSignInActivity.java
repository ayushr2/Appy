package com.bigred.appy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This activity is responsible for Google Sign In feature. If the user is already signed in OR if
 * they sign in, it displays their google profile picture (rounded), their name and sign out button.
 * Otherwise it displays the google sign in button and unknown user picture.
 * <p>
 * Libraries used:
 * 1. Google sign in sdk
 * 2. Picasso
 *
 * @see <a href="https://developers.google.com/identity/sign-in/android/sign-in">Google Sign In Docs</a>
 * @see <a href="http://square.github.io/picasso/">Picasso Docs</a>
 */
public class GoogleSignInActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    // Constants
    private static final int SIGN_IN_CODE = 2258;

    // Instance Variables
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private SignInButton signInButton;
    private Uri userProfilePictureUri;
    private String userName;
    private String userEmail;
    private ProgressDialog waitForFirebaseToUpload;
    private String userEmailClean;

    /**
     * Initialises all components and sets button listeners. Check if any users are already signed in.
     *
     * @param savedInstanceState bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_layout);
        initialiseComponents();
        initialiseButtonListeners();
        checkSilentSignIn();
    }

    /**
     * Sets button listeners for sign in and sign out buttons.
     */
    private void initialiseButtonListeners() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    /**
     * initialises all UI components and google sign in elements.
     */
    private void initialiseComponents() {
        /*
         * Configure sign-in to request the user's ID, email address, and basic profile. ID and
         * basic profile are included in DEFAULT_SIGN_IN.
         *
         * Note: If you want to request additional information from the user, specify them with
         * requestScopes. See the url below to more information.
         * https://developers.google.com/identity/sign-in/android/sign-in#configure_google_sign-in_and_the_googleapiclient_object
         */
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Private.CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by googleSignInOptions.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mAuth = FirebaseAuth.getInstance();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
    }

    /**
     * This method is called when google sign in returns back to the activity after their code
     * executes. Handles sign in result.
     *
     * @param requestCode used to differentiate which service is returning to the activity
     * @param resultCode  result code
     * @param data        the data of the result is held here
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_CODE) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    /**
     * Handles sign in result. If successful updates UI by getting username and profile picture.
     *
     * @param result Google Sign In result object which holds all data
     */
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            userProfilePictureUri = acct.getPhotoUrl();
            userName = acct.getDisplayName();
            userEmail = acct.getEmail();
            userEmailClean = userEmail.replace(Constants.DOT, Constants.UNDER_SCORE);
            waitForFirebaseToUpload = new ProgressDialog(this);
            waitForFirebaseToUpload.setTitle("Updating Database");
            waitForFirebaseToUpload.setMessage("Please wait...");
            waitForFirebaseToUpload.show();
            firebaseAuthWithGoogle(acct);
        } else {
            Toast.makeText(this, getString(R.string.google_sign_in_fail), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method authenticates Firebase client with the Google Signed In account. So If user signs
     * in with google successfully, they are automatically signed in with firebase which gives then
     * access to Firebase.
     *
     * @param acct GoogleSignInAccount which contains the signed in google user
     * @see <a href="https://firebase.google.com/docs/auth/android/google-signin">Firebase Sign In using Google Auth</a>
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                    .child(Constants.USERS_KEY).child(userEmailClean);
                            userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user;
                                    if (dataSnapshot.getValue() == null) {
                                        user = new User(userName, userProfilePictureUri.toString(), userEmail, userEmailClean);
                                        userDatabaseRef.setValue(user);
                                    } else {
                                        user = (User) dataSnapshot.getValue(User.class);
                                    }
                                    updatePreferences(user);
                                    waitForFirebaseToUpload.dismiss();
                                    selectIdentity();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.firebase_database_error),
                                            Toast.LENGTH_SHORT).show();
                                    waitForFirebaseToUpload.dismiss();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), getString(R.string.firebase_auth_fail),
                                    Toast.LENGTH_LONG).show();
                            waitForFirebaseToUpload.dismiss();
                        }
                    }
                });
    }

    private void selectIdentity() {
        
    }

    private void updatePreferences(User user) {
        SharedPreferences settings = getSharedPreferences(Constants.PREF_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.EMAIL, user.email);
        editor.putString(Constants.CLEAN_EMAIL, user.emailClean);
        editor.putString(Constants.NAME, user.name);
        editor.putString(Constants.PHOTO_URI_STRING, user.photoUriString);
        editor.commit();
    }

    /**
     * Google standard sign in code which starts the sign in process.
     */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_CODE);
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

    /**
     * Checks if any user was already signed in and if so updates UI accordingly. Pulls the user
     * information and starts handleResult method.
     */
    private void checkSilentSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            handleSignInResult(opr.get());
        }
    }

    /**
     * The only method of GoogleApiClient.OnConnectionFailedListener interface. If connection is
     * unsuccessful then shows the following method.
     *
     * @param connectionResult ConnectionResult object
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.connection_fail), Toast.LENGTH_SHORT).show();
    }
}