package com.example.yvonnewu.frontend;

import android.content.ClipData;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.yvonnewu.frontend.JSONTags.*;

/**
 * Created by Yvonne Wu on 11/22/2017.
 */

public class Offer
{
    private String offerID;
    private String userToPick;
    private Item itemToBarter;
    private String previous;
    private String offerName;
    private String fromUser;
    private String toUser;
    private ArrayList<Item> itemList;
    private ArrayList<Offer> childOffer;
    private String status;
    private String phone;
    private String email;
    private final String tag = "Offer";

    public Offer()
    {
        this.userToPick = null;
        this.itemToBarter = null;
        this.previous = null;
        this.offerName = null;
        this.fromUser = null;
        this.toUser = null;
        this.itemList = null;
        this.status = null;
        this.offerID = null;
        this.childOffer = null;
        this.phone = null;
        this.email = null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Offer> getChildOffer()
    {
        return childOffer;
    }

    public void setChildOffer(JSONArray childOfferJson)
    {
        this.childOffer = new ArrayList<>();
        if(childOfferJson.length() == 0)
            return;
        try
        {
            for(int index = 0; index < childOfferJson.length(); index++)
            {
                JSONObject temp = childOfferJson.getJSONObject(index);
                Log.d(tag, "the child offer is " + temp.toString());
                Offer newOffer = new Offer();
                newOffer.setOfferFromJSON(temp);
                this.childOffer.add(newOffer);
            }
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String getOfferID()
    {
        return offerID;
    }

    public void setOfferID(String offerID)
    {
        this.offerID = offerID;
    }

    public String getStatus()
    {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Item> getItemList() {
        return this.itemList;
    }

    public void setItemList(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    public String getUserToPick() {
        return this.userToPick;
    }

    public void setUserToPick(String userToPick) {
        this.userToPick = userToPick;
    }

    public ArrayList<Item> getItemToBarter()
    {
        ArrayList<Item> items = new ArrayList<>();
        items.add(this.itemToBarter);
        return items;
    }

    public void setItemToBarter(Item itemToBarter) {
        this.itemToBarter = itemToBarter;
    }

    public String getPrevious() {
        return this.previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getOfferName() {
        return this.offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getFromUser() {
        return this.fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return this.toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public JSONObject offerToJSON()
    {
        JSONObject rtnJSON = new JSONObject();
        try
        {
            rtnJSON.put(FROMUSER, this.fromUser);
            rtnJSON.put(TOUSER, this.toUser);
            rtnJSON.put(USERTOPICK, this.userToPick);
            rtnJSON.put(ITEMTOBARTER, this.itemToBarter.getID());
            rtnJSON.put(PREVIOUS, this.previous);
            rtnJSON.put(OFFERNAME, this.offerName);
            JSONArray itemListJSONArray = new JSONArray();
            for(Item item: itemList)
            {
                JSONObject temp = new JSONObject();
                temp.put(ITEMID, item.getID());
                itemListJSONArray.put(temp);
            }
            rtnJSON.put(ITEMLIST, itemListJSONArray);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return rtnJSON;
    }

    public void setOfferFromJSON(JSONObject json)
    {
        try
        {
            Log.d(tag, "setOfferFromJson: " + json.toString());
            if(json.has(OFFERID))
                this.setOfferID(json.getString(OFFERID));

            if(json.has(OFFERNAME))
                this.setOfferName(json.getString(OFFERNAME));

            if(json.has(OFFERSTATUS))
                this.setStatus(json.getString(OFFERSTATUS));

            if(json.has(PREVIOUS))
                this.setPrevious(json.getString(previous));

            if(json.has(USERTOPICK))
                this.setUserToPick(json.getString(USERTOPICK));

            if(json.has(FROMUSER))
                this.setFromUser(json.getString(FROMUSER));

            if(json.has(TOUSER))
                this.setToUser(json.getString(TOUSER));

            if(json.has(ITEMTOBARTER))
            {
                Item newItem = new Item();
                JSONObject itemJson = json.getJSONObject(ITEMTOBARTER);
                Log.d(tag, "itemTB in json is " + itemJson.toString());
                newItem.setItemFromJSON(json.getJSONObject(ITEMTOBARTER));
                this.setItemToBarter(newItem);
            }

            if(json.has(CHILDOFFER))
            {
                JSONArray childOfferArray = json.getJSONArray(CHILDOFFER);
                this.setChildOffer(childOfferArray);
            }

            if(json.has(ITEMLIST))
            {
                JSONArray itemArray = json.getJSONArray(ITEMLIST);
                this.itemList = new ArrayList<>();
                for(int index = 0; index < itemArray.length(); index++)
                {
                    Item newItem = new Item();
                    newItem.setItemFromJSON(itemArray.getJSONObject(index));
                    this.itemList.add(newItem);
                }
            }

            if(json.has(CONTACTINFO))
            {
                Log.d(tag, "Offer is accepted");
                JSONObject contactInfo = json.getJSONObject(CONTACTINFO);
                this.setEmail(contactInfo.getString(EMAIL));
                this.setPhone(contactInfo.getString(PHONE));
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
