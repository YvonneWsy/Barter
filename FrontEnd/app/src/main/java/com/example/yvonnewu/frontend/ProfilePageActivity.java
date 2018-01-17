package com.example.yvonnewu.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;
import static com.example.yvonnewu.frontend.JSONTags.*;

public class ProfilePageActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, OnJSONReceive, Check
{
    private EditText nameField, emailField, phoneField, locationField;
    private ImageView profilePicField;
    private GoogleApiClient mGoogleApiClient;
    private User ownerUser;
    private final String profileTAG = ProfilePageActivity.class.getSimpleName();
    private final String volleyTag = "Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        //instantiate all the GUI components
        Toolbar profileBar = (Toolbar) findViewById(R.id.toolBarProfile);
        setSupportActionBar(profileBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        profilePicField = (ImageView) findViewById(R.id.userPicProfile);
        nameField = (EditText) findViewById(R.id.nameETProfile);
        emailField = (EditText) findViewById(R.id.emailETProfile);
        phoneField = (EditText) findViewById(R.id.phoneETProfile);
        locationField = (EditText) findViewById(R.id.locationETProfile);
        Button saveBtn = (Button) findViewById(R.id.saveBtnProfile);
        saveBtn.setOnClickListener(this);

        //get the user
        ownerUser = User.getInstance();

        //create new instance of googleSigninResult & GoogleApiClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_profile_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_logOut:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess())
                    updateUI(true);
            }
        });
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        if(result.isSuccess()){
            //Update the GUI
            String imageURL = ownerUser.getUserPic();
            if(imageURL != null)
                Glide.with(this).load(imageURL)
                        .apply(RequestOptions.circleCropTransform()).into(profilePicField);
            nameField.setText(ownerUser.getName());
            emailField.setText(ownerUser.getEmail());
            phoneField.setText(ownerUser.getPhone());
            locationField.setText(ownerUser.getLocation());
        }
    }

    private void updateUI(boolean returnToLogin)
    {
        if(returnToLogin)
        {
            Intent loginIntent = new Intent(this, LogInPageActivity.class);
            startActivity(loginIntent);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.saveBtnProfile:
                saveButtonClick();
                break;

            default:
                return;
        }
    }

    private void saveButtonClick()
    {
        String sendURL = URL + "/profile";
        ownerUser.setName(nameField.getText().toString());
        ownerUser.setPhone(phoneField.getText().toString());
        ownerUser.setEmail(emailField.getText().toString());
        ownerUser.setLocation(locationField.getText().toString());
        boolean validFlag = checkFields();
        if(validFlag)
        {
            JSONObject send = ownerUser.encodeProfileToJSON();
            /*Log.d(profileTAG, "JSON.toString is"+send.toString());
            new JSONTask(this).execute(sendURL, send.toString());*/
            VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
        }
    }

    @Override
    public boolean checkFields()
    {
        boolean rtnFlag = true;
        if (ownerUser.getEmail().isEmpty())
        {
            emailField.setBackgroundResource(R.drawable.focus_border_style);
            rtnFlag = false;
        }

        if(ownerUser.getPhone().isEmpty())
        {
            phoneField.setBackgroundResource(R.drawable.focus_border_style);
            rtnFlag = false;
        }

        if(ownerUser.getLocation().isEmpty())
        {
            locationField.setBackgroundResource(R.drawable.focus_border_style);
            rtnFlag = false;
        }

        if(ownerUser.getName().isEmpty())
        {
            nameField.setBackgroundResource(R.drawable.focus_border_style);
            rtnFlag = false;
        }

        if(!rtnFlag)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.checkFieldsAlert)
                    .setPositiveButton(R.string.okBtnAlert, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
        return rtnFlag;
    }

    @Override
    public void onReceive(JSONObject received)
    {
        String JSONResult = null;
        try {
            JSONResult = received.getString(RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(profileTAG,"JSONREsult is"+JSONResult);

        //jump to inventory page if data is successfully sent
        if(TRUE.equals(JSONResult))
        {
            Intent userMainPage = new Intent(this, UserMainPageActivity.class);
            startActivity(userMainPage);
        }
        else
            Toast.makeText(this, R.string.serverFail,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
