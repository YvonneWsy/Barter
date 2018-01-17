package com.example.yvonnewu.frontend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

public class UserMainPageActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, OnJSONReceive {
    //TODO: need to implement
    private GoogleApiClient mGoogleApiClient;
    private ImageView profilePicField;
    private TextView nameField;
    private User ownerUser;
    private final String userMainTAG = UserMainPageActivity.class.getSimpleName();
    private final String volleyTag = "UserMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);

        //setting up GUI component
        profilePicField = (ImageView) findViewById(R.id.profilePicUserMain);
        nameField = (TextView) findViewById(R.id.nameTVUserMain);
        ImageButton editProfileBtn = (ImageButton) findViewById(R.id.editProfileBtnUserMain);
        ImageButton logOutBtn = (ImageButton) findViewById(R.id.logoutBtnUserMain);
        ImageButton viewInvBtn = (ImageButton) findViewById(R.id.inventoryBtnUserMain);
        ImageButton viewOfferBtn = (ImageButton) findViewById(R.id.offerBtnUserMain);
        ImageButton searchBtn = (ImageButton) findViewById(R.id.searchBtnUserMain);
        ImageButton addItemBtn = (ImageButton) findViewById(R.id.addItemBtnUserMain);
        addItemBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        viewOfferBtn.setOnClickListener(this);
        viewInvBtn.setOnClickListener(this);
        logOutBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);

        //get user
        ownerUser = User.getInstance();

        //create new instance of googleSigninResult & GoogleApiClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart() {
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

    private void handleSignInResult(GoogleSignInResult result)
    {
        if(result.isSuccess()){
            //Update the GUI
            String imageURL = ownerUser.getUserPic();
            if(imageURL != null)
                Glide.with(this).load(imageURL)
                        .apply(RequestOptions.circleCropTransform()).into(profilePicField);
            nameField.setText(ownerUser.getName());
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.editProfileBtnUserMain:
                editProfile();
                break;

            case R.id.logoutBtnUserMain:
                signOut();
                break;

            case R.id.addItemBtnUserMain:
                addItem();
                break;

            case R.id.inventoryBtnUserMain:
                viewInventory();
                break;

            case R.id.searchBtnUserMain:
                search();
                break;

            case R.id.offerBtnUserMain:
                offerCenter();
                break;

            default:
                return;
        }
    }

    private void offerCenter()
    {
        String sendURL = URL + "/offerCenter";
        JSONObject send = new JSONObject();
        try
        {
            send.put(USERID, ownerUser.getID());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
    }

    private void search()
    {
        Intent searchIntent = new Intent(this, SearchPageActivity.class);
        startActivity(searchIntent);
    }

    private void viewInventory()
    {
        String sendURL = URL + "/inventory";
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(USERID, ownerUser.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //new JSONTask(this).execute(sendURL, jsonObj.toString());
        VolleySingleton.getInstance(this).PostToServer(sendURL, jsonObj, this, volleyTag);
    }

    private void editProfile()
    {
        String sendURL = URL + "/profile";
        JSONObject jsonObj = new JSONObject();
        try
        {
            jsonObj.put(USERID, ownerUser.getID());
            jsonObj.put(TAG,INQUIRY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*new JSONTask(this).execute(sendURL,jsonObj.toString());*/
        VolleySingleton.getInstance(this).PostToServer(sendURL, jsonObj, this, volleyTag);
    }

    private void addItem()
    {
        Intent addItemIntent = new Intent(this, AddItemPageActivity.class);
        startActivity(addItemIntent);
    }

    private void signOut()
    {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess())
                    updateUI(false);
            }
        });
    }

    private void updateUI(boolean returnToPrevious)
    {
        if(!returnToPrevious)
        {
            Intent loginIntent = new Intent(this, LogInPageActivity.class);
            startActivity(loginIntent);
        }
    }

    @Override
    public void onReceive(JSONObject received)
    {
        Log.d(userMainTAG,"in userMain onReceive");
        String result = "";
        try {
            result = received.getString(RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Evaluating where is the response from
        if(TRUE.equals(result)){
            try {
                result = received.getString(FROM);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Receiving income msg for profilePage
            if(PROFILE.equals(result))
            {
                Intent profileIntent = new Intent(this, ProfilePageActivity.class);
                ownerUser.setProfileFromJSON(received);
                startActivity(profileIntent);
            }
            //Receiving income msg for inventoryPage
            else if(INVENTORY.equals(result))
            {
                ownerUser.setItemListOwnerID(ownerUser.getID());
                ownerUser.setItemList(received);
                Intent inventoryIntent = new Intent(this, InventoryPageActivity.class);
                startActivity(inventoryIntent);
            }
            //Receiving income msg for offerCenter
            else if(OFFERCENTER.equals(result))
            {
                ownerUser.setOfferList(received);
                Intent offerCenterIntent = new Intent(this, OfferCenterPageActivity.class);
                startActivity(offerCenterIntent);
            }
        }
        else
            Toast.makeText(this, R.string.serverFail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

}
