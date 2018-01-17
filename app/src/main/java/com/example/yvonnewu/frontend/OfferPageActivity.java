package com.example.yvonnewu.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.yvonnewu.frontend.JSONTags.*;

public class OfferPageActivity extends AppCompatActivity
        implements View.OnClickListener, OnJSONReceive
{
    private Offer mOffer;
    private User owner;
    private Button acceptBtn, declineBtn, negBtn;
    private TextView offerNameTV, statusTV, emailTV, phoneTV, emailField, phoneField, contactInfoTitle, contactInfoInstr;
    private RecyclerView itemToBarter, itemList;
    private String tag;
    private final String offerTag = OfferPageActivity.class.getSimpleName();
    private final String volleyTag = "Offer";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_page);

        owner = User.getInstance();
        mOffer = owner.getOfferSelected();
        tag = getIntent().getStringExtra(FROM);

        //GUI
        offerNameTV = (TextView) findViewById(R.id.offerNameTVEditO);
        offerNameTV.setText(mOffer.getOfferName());
        statusTV = (TextView) findViewById(R.id.offerStatusTVEditO);
        Log.d(offerTag, "Status is " + mOffer.getStatus());
        acceptBtn = (Button) findViewById(R.id.acceptBtnO);
        declineBtn = (Button) findViewById(R.id.declineBtnO);
        negBtn = (Button) findViewById(R.id.NegBtnO);

        emailTV = (TextView)findViewById(R.id.emailTVO);
        phoneTV = (TextView) findViewById(R.id.phoneTVO);
        emailField = (TextView) findViewById(R.id.emailEditTVO);
        phoneField = (TextView)findViewById(R.id.phoneEditTVO);
        contactInfoInstr = (TextView) findViewById(R.id.contactInfoInstrTVO);
        contactInfoTitle = (TextView) findViewById(R.id.contactInfoTVO);

        //Recycler View
        itemList = (RecyclerView) findViewById(R.id.NegItemRVO);
        itemToBarter = (RecyclerView) findViewById(R.id.itemToBarterRVO);
        itemToBarter.setItemAnimator(new DefaultItemAnimator());
        itemList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager itemListLM = new LinearLayoutManager(this);
        itemList.setLayoutManager(itemListLM);
        RecyclerView.LayoutManager itemToBarterLM = new LinearLayoutManager(this);
        itemToBarter.setLayoutManager(itemToBarterLM);
        RecyclerView.Adapter itemListAdapter = new RecyclerViewAdapter(mOffer.getItemList(), this);
        itemList.setAdapter(itemListAdapter);
        RecyclerView.Adapter itemToBarterAdapter = new RecyclerViewAdapter(mOffer.getItemToBarter(), this);
        itemToBarter.setAdapter(itemToBarterAdapter);

        checkStatus();
    }

    private void checkStatus()
    {
        if(ACCEPT.equals(mOffer.getStatus()))
        {
            acceptBtn.setVisibility(View.GONE);
            declineBtn.setVisibility(View.GONE);
            negBtn.setVisibility(View.GONE);
            emailField.setText(mOffer.getEmail());
            phoneField.setText(mOffer.getPhone());
            statusTV.setText(mOffer.getStatus());
        }
        else if(PENDING.equals(mOffer.getStatus()) ||
                DECLINE.equals(mOffer.getStatus()))
        {
            contactInfoTitle.setVisibility(View.GONE);
            contactInfoInstr.setVisibility(View.GONE);
            emailField.setVisibility(View.GONE);
            emailTV.setVisibility(View.GONE);
            phoneField.setVisibility(View.GONE);
            phoneTV.setVisibility(View.GONE);
            acceptBtn.setVisibility(View.GONE);
            declineBtn.setVisibility(View.GONE);
            negBtn.setVisibility(View.GONE);
            statusTV.setText(mOffer.getStatus());
            statusTV.append("\n\n");
        }
        else
        {
            contactInfoTitle.setVisibility(View.GONE);
            contactInfoInstr.setVisibility(View.GONE);
            emailField.setVisibility(View.GONE);
            emailTV.setVisibility(View.GONE);
            phoneField.setVisibility(View.GONE);
            phoneTV.setVisibility(View.GONE);
            acceptBtn.setOnClickListener(this);
            declineBtn.setOnClickListener(this);
            negBtn.setOnClickListener(this);
            statusTV.setText("In Progress");
        }
    }

    @Override
    public void onBackPressed()
    {
        if(CHILDOFFER.equals(tag))
        {
            String sendURL = URL + "/offerCenter";
            JSONObject send = new JSONObject();
            try
            {
                send.put(USERID, owner.getID());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.acceptBtnO:
                acceptOffer();
                break;

            case R.id.declineBtnO:
                declineOffer();
                break;

            case R.id.NegBtnO:
                negotiate();
                break;

            default:
                return;
        }
    }

    private void negotiate()
    {
        String sendURL = URL + "/inventory";
        JSONObject send = new JSONObject();
        try
        {
            send.put(TAG, SELECTION);
            send.put(USERID, mOffer.getUserToPick());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
    }

    private void declineOffer()
    {
        String sendURL = URL + "/declineOffer";
        JSONObject send = new JSONObject();
        try
        {
            send.put(OFFERID, mOffer.getOfferID());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
    }

    private void acceptOffer()
    {
        String sendURL = URL + "/acceptOffer";
        JSONObject send = new JSONObject();
        try
        {
            send.put(OFFERID, mOffer.getOfferID());
            send.put(USERID, owner.getID());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
    }

    @Override
    public void onReceive(JSONObject received)
    {
        try
        {
            String result = received.getString(RESULT);
            if(TRUE.equals(result))
            {
                result = received.getString(FROM);
                if(ACCPETOFFER.equals(result))
                {
                    JSONObject contactInfo = received.getJSONObject(CONTACTINFO);
                    mOffer.setStatus(ACCEPT);
                    mOffer.setPhone(contactInfo.getString(PHONE));
                    mOffer.setEmail(contactInfo.getString(EMAIL));
                    this.finish();
                    getIntent().putExtra(FROM,CHILDOFFER);
                    this.startActivity(getIntent());
                }

                else if(DECLINEOFFER.equals(result))
                {
                    mOffer.setStatus(DECLINE);
                    this.finish();
                    this.startActivity(getIntent());
                }
                else if(SELECTION.equals(result))
                {
                    User ownerUser = User.getInstance();
                    ownerUser.setItemListOwnerID(ownerUser.getID());
                    ownerUser.setItemList(received);
                    Intent inventorySelectionIntent = new Intent(this, InventorySelectionPageActivity.class);
                    inventorySelectionIntent.putExtra(TAG, CHILDOFFER);
                    inventorySelectionIntent.putExtra(FROM, CHILDOFFER);
                    startActivity(inventorySelectionIntent);
                    this.finish();
                }
                else if(OFFERCENTER.equals(result))
                {
                    owner.setOfferList(received);
                    Intent offerCenterIntent = new Intent(this, OfferCenterPageActivity.class);
                    startActivity(offerCenterIntent);
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
