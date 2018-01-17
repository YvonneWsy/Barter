package com.example.yvonnewu.frontend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;


import static com.example.yvonnewu.frontend.JSONTags.*;

public class LogInPageActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, OnJSONReceive {
    private GoogleApiClient googleApiClient;
    private final int Sign_In_Req = 9001;
    private final String loginTAG = LogInPageActivity.class.getSimpleName();
    private final String volleyTag = "Login";
    private String sendURL;
    private User ownerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);
        sendURL = URL;

        //Initiate GUI Component
        SignInButton signInBtn = (SignInButton) findViewById(R.id.loginBtn);
        signInBtn.setOnClickListener(this);

        //get user
        ownerUser = new User();

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
    }

    @Override
    protected void onStart()
    {
        googleApiClient.connect();
        super.onStart();
    }

    //login_Btn click method
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.loginBtn:
                signIn();
                break;

            default:
                return;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
    }

    private void signIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, Sign_In_Req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Sign_In_Req) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }


    private void handleResult(GoogleSignInResult result)
    {
        Log.d(loginTAG, "LoginResult is " + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount mAccount = result.getSignInAccount();
            ownerUser = User.getInstance();
            ownerUser.setID(mAccount.getId());
            ownerUser.setUserPic(mAccount.getPhotoUrl().toString());
            ownerUser.setName(mAccount.getDisplayName());
            ownerUser.setEmail(mAccount.getEmail());
            ownerUser.setPhone("");
            ownerUser.setLocation("");
            updateUI(true);
        } else {
            Toast.makeText(this, R.string.not_login, Toast.LENGTH_SHORT).show();
            updateUI(false);
        }
    }

    private void updateUI(boolean isLogin)
    {
        if (isLogin) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(USERID, ownerUser.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //new JSONTask(this).execute(sendURL, jsonObject.toString());
            VolleySingleton.getInstance(this).PostToServer(sendURL, jsonObject, this, volleyTag );
        }
    }

    @Override
    public void onReceive(JSONObject received)
    {
        String result = "";
        try {
            result = received.getString(RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //if sending is success
        if (TRUE.equals(result)) {
            String uExist = "";
            try {
                uExist = received.getString(EXIST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //if user is in the DB, grab name from DB
            if (TRUE.equals(uExist)) {
                ownerUser.setProfileFromJSON(received);
                Intent userMainIntent = new Intent(this, UserMainPageActivity.class);
                startActivity(userMainIntent);
            }
            //if user is not in the DB, go to profile Page
            else if (FALSE.equals(uExist)) {
                Intent profileIntent = new Intent(this, ProfilePageActivity.class);
                startActivity(profileIntent);
            }
        }
        //toast for unsuccessful sending
        else
            Toast.makeText(this, R.string.serverFail, Toast.LENGTH_SHORT).show();
    }
}