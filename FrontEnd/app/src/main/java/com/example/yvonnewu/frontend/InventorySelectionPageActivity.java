package com.example.yvonnewu.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import static com.example.yvonnewu.frontend.JSONTags.*;

public class InventorySelectionPageActivity extends AppCompatActivity
        implements OnJSONReceive, ListView.OnItemClickListener, View.OnClickListener, Check
{
    private User ownerUser;
    private ListView selectionLV;
    private ArrayList<SelectableItem> selectableItems;
    private ImageView notFoundPic1, notFoundPic2;
    private Button confirmBtn, cancelBtn;
    private EditText offerNameField;
    private String tag;
    private Offer mOffer, oldOffer;
    private final String inventorySelectTAG = InventoryPageActivity.class.getSimpleName();
    private final String volleyTag = "InventorySelection";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_selection_page);

        ownerUser = User.getInstance();
        oldOffer = ownerUser.getOfferSelected();
        tag = getIntent().getStringExtra(TAG);
        Log.d(inventorySelectTAG, "Tag from previous activity is" + tag);

        confirmBtn = (Button) findViewById(R.id.confirmBtnIS);
        cancelBtn = (Button) findViewById(R.id.cancelBtnIS);
        notFoundPic1 = (ImageView)findViewById(R.id.notFoundIVSR);
        notFoundPic2 = (ImageView) findViewById(R.id.unabletobarterIVSR);
        selectionLV = (ListView) findViewById(R.id.inventorySelectionLV);
        offerNameField = (EditText) findViewById(R.id.offerNameETIS);
        if(CHILDOFFER.equals(tag))
            offerNameField.setHint(oldOffer.getOfferName());
        setSelectionList();
    }

    private void setSelectionList()
    {
        ArrayList<Item> itemList = ownerUser.getItemList();
        if(itemList.isEmpty())
        {
            selectionLV.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            offerNameField.setVisibility(View.GONE);
        }
        else
        {
            notFoundPic1.setVisibility(View.GONE);
            notFoundPic2.setVisibility(View.GONE);
            initSelectableItem(itemList);
            SelectionAdapter listApt = new SelectionAdapter(this, selectableItems);
            selectionLV.setAdapter(listApt);
            selectionLV.setOnItemClickListener(this);
            confirmBtn.setOnClickListener(this);
            cancelBtn.setOnClickListener(this);
        }
    }

    private void initSelectableItem(ArrayList<Item> items)
    {
        selectableItems = new ArrayList<>();

        for(Item iterator : items)
        {
            SelectableItem temp = new SelectableItem(false, iterator);
            selectableItems.add(temp);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.confirmBtnIS:
                confirmMakeOffer();
                break;

            case R.id.cancelBtnIS:
                cancelMakeOffer();
                break;

            default:
                return;
        }

    }

    private void cancelMakeOffer()
    {
        this.finish();
    }

    private void confirmMakeOffer()
    {
        /*ArrayList<String> itemInvolved = new ArrayList<>();*/
       /* Log.d(inventorySelectTAG, "the selected Item is " + ownerUser.getItemSelected()
                .getTitle());*/
        boolean validFlag = checkFields();
        if(validFlag)
        {
            ArrayList<Item> itemInvolved = new ArrayList<>();
            for(SelectableItem iterator : selectableItems)
            {
                if(iterator.isChecked())
                {
                    Item temp = iterator.getmItem();
                    itemInvolved.add(temp);
                }
            }
            String sendURL = URL + "/makeOffer";
            mOffer = new Offer();
            mOffer.setFromUser(ownerUser.getID());
            mOffer.setOfferName(offerNameField.getText().toString());
            mOffer.setItemList(itemInvolved);
            mOffer.setStatus(PENDING);
            if(BRANDNEW.equals(tag))
            {
                mOffer.setToUser(ownerUser.getItemSelected().getOwnerID());
                mOffer.setItemToBarter(ownerUser.getItemSelected());
                mOffer.setPrevious(NULL);
                mOffer.setUserToPick(ownerUser.getID());
            }
            else if(CHILDOFFER.equals(tag))
            {
                mOffer.setItemToBarter(oldOffer.getItemToBarter().get(0));
                mOffer.setUserToPick(oldOffer.getUserToPick());
                mOffer.setPrevious(oldOffer.getOfferID());
                mOffer.setToUser(oldOffer.getFromUser());
            }

            JSONObject send = mOffer.offerToJSON();
            VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
        }
    }

    @Override
    public void onReceive(JSONObject received)
    {
        Log.d(inventorySelectTAG, received.toString());
        String result = "";
        try
        {
            result = received.getString(RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(TRUE.equals(result))
        {
            mOffer.setStatus("Pending");
            ownerUser.setOfferSelected(mOffer);
            Intent offerIntent = new Intent(this,OfferPageActivity.class);
            offerIntent.putExtra(TAG,SELECTION);
            offerIntent.putExtra(FROM, getIntent().getStringExtra(FROM));
            startActivity(offerIntent);
            this.finish();
        }
        else
            Toast.makeText(this, R.string.serverFail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean checkFields()
    {
        boolean rtnFlag = true;
        if (offerNameField.getText().toString().isEmpty())
        {
            Log.d(TAG, "offerName is empty");
            offerNameField.setBackgroundResource(R.drawable.focus_border_style);
            rtnFlag = false;
        }

        boolean checkStatus = false;
        for(SelectableItem iterator: selectableItems)
        {
            if(iterator.isChecked())
            {
                checkStatus = true;
                break;
            }
        }
        if(!checkStatus)
        {
            Log.d(TAG, "nothing is checked");
            rtnFlag = false;
            selectionLV.setBackgroundResource(R.drawable.focus_border_style);
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
}
