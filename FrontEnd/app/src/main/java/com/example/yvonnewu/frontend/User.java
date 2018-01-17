package com.example.yvonnewu.frontend;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.yvonnewu.frontend.JSONTags.*;

import java.util.ArrayList;

/**
 * Created by Yvonne Wu on 10/24/2017.
 */

public class User
{
    private static User mUser = null;
    private String userID;
    private String userName;
    private String userPic;
    private String email;
    private String phone;
    private String location;
    private Item itemSelected;
    private Offer offerSelected;
    private ArrayList<Item> itemList;
    private ArrayList<Offer> offerList;
    private String itemListOwnerID;
    private String itemListOwnerName;
    private String itemListOwnerPic;
    private final String UserTAG = "in User Class";

    protected User()
    {
        this.userID = null;
        this.userName = null;
        this.email = null;
        this.phone = null;
        this.location = null;
        this.itemList = null;
        this.itemSelected = null;
        this.itemListOwnerName = null;
        this.itemListOwnerPic = null;
        this.itemListOwnerID = null;
        this.offerList = null;
        this.offerSelected = null;
    }

    protected static User getInstance()
    {
        if(mUser == null)
            mUser = new User();
        return mUser;
    }

    public Offer getOfferSelected()
    {
        return offerSelected;
    }

    public void setOfferSelected(Offer offerSelected)
    {
        this.offerSelected = offerSelected;
    }

    public ArrayList<Offer> getOfferList()
    {
        return offerList;
    }

    public void setOfferList(JSONObject received)
    {
        this.offerList = new ArrayList<>();
        try
        {
            JSONArray offerArray = received.getJSONArray(OFFERLIST);
            if(offerArray.length() == 0)
                return;
            for(int index = 0; index < offerArray.length(); index++)
            {
                JSONObject temp = offerArray.getJSONObject(index);
                Offer newOffer = new Offer();
                newOffer.setOfferFromJSON(temp);
                this.offerList.add(newOffer);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    String getItemListOwnerID() {
        return itemListOwnerID;
    }

    void setItemListOwnerID(String itemListOwnerID) {
        this.itemListOwnerID = itemListOwnerID;
    }

    String getItemListOwnerName()
    {
        return itemListOwnerName;
    }

    String getItemListOwnerPic() {
        return itemListOwnerPic;
    }

    void setID(String id)
    {
        this.userID = id;
    }

    protected String getID()
    {
        return this.userID;
    }

    protected void setName(String name)
    {
        this.userName = name;
    }

    protected String getName()
    {
        return this.userName;
    }

    void setEmail(String email)
    {
        this.email = email;
    }

    String getEmail()
    {
        return this.email;
    }

    void setPhone(String phone)
    {
        this.phone = phone;
    }

    String getPhone()
    {
        return this.phone;
    }

    void setLocation(String location)
    {
        this.location = location;
    }

    String getLocation()
    {
        return this.location;
    }

    void setUserPic(String imageURL){this.userPic = imageURL;}

    String getUserPic(){return this.userPic;}

    Item getItemSelected() {return this.itemSelected;}

    void setItemSelected(Item item, JSONObject obj)
    {
        item.setItemFromJSON(obj);
        this.itemSelected = item;
    }

    void setItemList(JSONObject received)
    {
        this.itemList = new ArrayList<>();
        try {
            if(received.has(USERNAME))
            {
                this.itemListOwnerName = received.getString(USERNAME);
                Log.d(UserTAG, "received username "+ this.itemListOwnerName);
            }
            if(received.has(IMAGEURL))
                this.itemListOwnerPic = received.getString(IMAGEURL);
            JSONArray itemListJson = received.getJSONArray(ITEMLIST);
            Log.d(UserTAG, "the inventory array is " + itemListJson.toString());
            if(itemListJson.length() == 0)
                return;
            for(int index = 0; index < itemListJson.length(); index++)
            {
                JSONObject jsonObject = itemListJson.getJSONObject(index);
                Item tempItem = new Item();
                tempItem.setItemFromJSON(jsonObject);
                this.itemList.add(tempItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    ArrayList<Item> getItemList(){return this.itemList;}

    void setProfileFromJSON(JSONObject profile)
    {
        try {
            this.userName = profile.getString(USERNAME);
            this.email = profile.getString(EMAIL);
            this.phone = profile.getString(PHONE);
            this.location = profile.getString(LOCATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    JSONObject encodeProfileToJSON()
    {
        JSONObject rtnObj = new JSONObject();
        try {
            rtnObj.put(USERID,this.userID);
            rtnObj.put(TAG,CREATE);
            rtnObj.put(USERNAME,this.userName);
            rtnObj.put(EMAIL, this.email);
            rtnObj.put(PHONE, this.phone);
            rtnObj.put(LOCATION, this.location);
            if(this.userPic == null)
                rtnObj.put(IMAGEURL, NULL);
            else
                rtnObj.put(IMAGEURL, this.userPic);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rtnObj;
    }
}
