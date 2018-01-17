package com.example.yvonnewu.frontend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import static com.example.yvonnewu.frontend.JSONTags.*;

/**
 * Created by Yvonne Wu on 11/26/2017.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter
{
    private Context mContext;
    private ArrayList<Offer> parentOfferList;
    private final String tag = "ELV";
    private final String volleyTag = "ExpandableListView";
    //private HashMap<String, ArrayList<Offer>> childOfferList;

    public ExpandableListViewAdapter(Context mContext, ArrayList<Offer> parentOfferList)
    {
        this.mContext = mContext;
        this.parentOfferList = parentOfferList;
    }

    @Override
    public int getGroupCount()
    {
        return parentOfferList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        ArrayList<Offer> childOffer = parentOfferList.get(groupPosition).getChildOffer();
        return childOffer.size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return parentOfferList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        ArrayList<Offer> childOffer = parentOfferList.get(groupPosition).getChildOffer();
        return childOffer.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        final Offer mPOffer = parentOfferList.get(groupPosition);
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_group, null);
        }
        TextView parentOfferName = (TextView) convertView.findViewById(R.id.offerParentTV);
        ImageButton infoBtn = (ImageButton) convertView.findViewById(R.id.offerParentInfoBtn);
        infoBtn.setFocusable(false);
        infoBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
              getOffer(mPOffer);
            }
        });
        parentOfferName.setText(mPOffer.getOfferName());
        return convertView;
    }

    public void getOffer(Offer mPOffer)
    {
        Log.d(tag, "Parent getting clicked is: " + mPOffer.getOfferName());
        if(mPOffer.getChildOffer().size() == 0)
        {
           User.getInstance().setOfferSelected(mPOffer);
           getOfferFromServer();
        }
        else
        {
            ArrayList<String> offerNames = new ArrayList<>();
            final ArrayList<Offer> offerList = new ArrayList<>();
            offerNames.add(mPOffer.getOfferName());
            offerList.add(mPOffer);
            for(Offer temp : mPOffer.getChildOffer())
            {
                offerList.add(temp);
                offerNames.add(temp.getOfferName());
            }

            CharSequence offers[] = offerNames.toArray(new CharSequence[offerNames.size()]);
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.offer_selection);
            builder.setItems(offers, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    User.getInstance().setOfferSelected(offerList.get(which));
                    getOfferFromServer();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    private void getOfferFromServer()
    {
        Offer offer = User.getInstance().getOfferSelected();
        String sendURL = URL + "/offer";
        JSONObject send = new JSONObject();
        try
        {
            send.put(USERID, User.getInstance().getID());
            send.put(OFFERID, offer.getOfferID());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        VolleySingleton.getInstance(mContext).PostToServer(sendURL, send, new OnJSONReceive()
        {
            @Override
            public void onReceive(JSONObject received)
            {
                try
                {
                    String result = received.getString(RESULT);
                    if(TRUE.equals(result))
                    {
                        User.getInstance().getOfferSelected().setOfferFromJSON(received);
                        Intent offerIntent = new Intent(mContext, OfferPageActivity.class);
                        offerIntent.putExtra(TAG,OFFERCENTER);
                        mContext.startActivity(offerIntent);
                    }
                    else
                        Toast.makeText(mContext, R.string.serverFail, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, volleyTag);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        Offer mCOffer = parentOfferList.get(groupPosition).getChildOffer().get(childPosition);
        //Log.d(tag, "child offer name is" + mCOffer.getOfferName());
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_item, null);
        }
        TextView childOfferName = (TextView) convertView.findViewById(R.id.offerChildTV);
        childOfferName.setText(mCOffer.getOfferName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}
