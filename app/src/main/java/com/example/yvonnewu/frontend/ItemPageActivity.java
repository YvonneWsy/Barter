package com.example.yvonnewu.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.yvonnewu.frontend.JSONTags.*;

public class ItemPageActivity extends AppCompatActivity implements View.OnClickListener, OnJSONReceive
{
    private TextView itemTitle, itemCat, itemDes, ownerName;
    private Button viewOwnerBtn, makeOfferBtn, deleteItemBtn;
    private final String itemTAG = ItemPageActivity.class.getSimpleName();
    private final String volleyTag = "Item";
    private User ownerUser;
    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_page);

        //get owner instance
        ownerUser = User.getInstance();
        mItem = ownerUser.getItemSelected();

        //instantiate GUI component
        //Buttons
        viewOwnerBtn = (Button) findViewById(R.id.viewOwnerInventoryBtn);
        makeOfferBtn = (Button) findViewById(R.id.makeOfferBtn);
        deleteItemBtn = (Button) findViewById(R.id.deleteItemBtn);
        validateBtn();

        //TextViews
        itemTitle = (TextView) findViewById(R.id.itemTitleDynTxtI);
        itemCat = (TextView) findViewById(R.id.itemCatDynTxtI);
        itemDes = (TextView) findViewById(R.id.itemDesDynTxtI);
        ownerName = (TextView) findViewById(R.id.itemOwnerName);
        setText();

        //ImageViews
        ImageView itemPic = (ImageView) findViewById(R.id.itemImageI);
        itemPic.setImageBitmap(mItem.getPic());
        ImageView profilePic = (ImageView)findViewById(R.id.itemOwnerPicI);
        String imageURL = mItem.getOwnerPic();
        if(imageURL != null)
            Glide.with(this).load(imageURL)
                    .apply(RequestOptions.circleCropTransform()).into(profilePic);
    }

    @Override
    public void onBackPressed()
    {
        String tag = getIntent().getStringExtra(TAG);
        if(SELECTION.equals(tag))
            this.finish();
        super.onBackPressed();
    }

    private void validateBtn()
    {
        //hide the make offer or delete item button accordingly
        if("disable".equals(mItem.getTag()))
        {
            makeOfferBtn.setVisibility(View.GONE);
            viewOwnerBtn.setVisibility(View.GONE);
            deleteItemBtn.setVisibility(View.GONE);
        }
        else if(mItem.getOwnerID().equals(ownerUser.getID()))
        {
            deleteItemBtn.setOnClickListener(this);
            makeOfferBtn.setVisibility(View.GONE);
            viewOwnerBtn.setVisibility(View.GONE);
        }
        else
        {
            makeOfferBtn.setOnClickListener(this);
            viewOwnerBtn.setOnClickListener(this);
            deleteItemBtn.setVisibility(View.GONE);
        }
    }

    private void setText()
    {
        itemTitle.setText(mItem.getTitle());
        itemCat.setText(mItem.getCategory());
        itemDes.setText(mItem.getDescription());
        ownerName.setText(mItem.getOwnerName());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.viewOwnerInventoryBtn:
                displayOwnerInventory();
                break;

            case R.id.makeOfferBtn:
                makeOffer();
                break;

            case R.id.deleteItemBtn:
                deleteItem();
                break;

            default:
                return;
        }
    }

    private void deleteItem()
    {
        String sendURL = URL + "/deleteItem";
        JSONObject send = new JSONObject();
        try
        {
            send.put(ITEMID, mItem.getID());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        /*new JSONTask(this).execute(sendURL, send.toString());*/
        //TODO: if need to change to Http Delete convention
        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
    }

    private void makeOffer()
    {
        String sendURL = URL + "/inventory";
        JSONObject send = new JSONObject();
        try
        {
            send.put(USERID, ownerUser.getID());
            send.put(TAG, SELECTION);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        /*Log.d(itemTAG, "sending" + send.toString());
        new JSONTask(this).execute(sendURL, send.toString());*/
        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
    }

    private void displayOwnerInventory()
    {
        String sendURL = URL + "/inventory";
        JSONObject send = new JSONObject();
        try
        {
            send.put(USERID, mItem.getOwnerID());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        /*new JSONTask(this).execute(sendURL, send.toString());*/
        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
    }

    @Override
    public void onReceive(JSONObject received)
    {
        Log.d(itemTAG, "now in item onReceive " + received.toString());
        String result = null;
        try {
            result = received.getString(RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(itemTAG, "result is " + result);
        if(TRUE.equals(result))
        {
            Log.d(itemTAG, "result is true");
            try {
                result = received.getString(FROM);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(DELETEITEM.equals(result))
            {
                ownerUser.setItemListOwnerID(ownerUser.getID());
                ownerUser.setItemList(received);
                Intent inventoryIntent = new Intent(this, InventoryPageActivity.class);
                inventoryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inventoryIntent);
            }
            else if(INVENTORY.equals(result))
            {
                ownerUser.setItemListOwnerID(mItem.getOwnerID());
                ownerUser.setItemList(received);
                Intent inventoryIntent = new Intent(this, InventoryPageActivity.class);
                startActivity(inventoryIntent);
            }
            else if(SELECTION.equals(result))
            {
                ownerUser.setItemListOwnerID(ownerUser.getID());
                ownerUser.setItemList(received);
                Intent inventorySelectionIntent = new Intent(this, InventorySelectionPageActivity.class);
                inventorySelectionIntent.putExtra(TAG, BRANDNEW);
                inventorySelectionIntent.putExtra(FROM, getIntent().getStringExtra(FROM));
                startActivity(inventorySelectionIntent);
                this.finish();
            }
        }
    }
}
