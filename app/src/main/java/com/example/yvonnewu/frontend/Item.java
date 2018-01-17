package com.example.yvonnewu.frontend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import static com.example.yvonnewu.frontend.JSONTags.*;

/**
 * Created by Yvonne Wu on 10/24/2017.
 */

public class Item
{
    private final String itemTAG = "ITEMTAG";
    private String itemID;
    private String itemTitle;
    private String itemDescription;
    private String itemCategory;
    private String itemPic;
    private String ownerName;
    private String ownerPic;
    private String ownerID;
    private String tag;

    protected Item()
    {
        this.itemTitle = null;
        this.itemDescription = null;
        this.itemID = null;
        this.itemPic = null;
        this.itemCategory = null;
        this.ownerName = null;
        this.ownerPic = null;
        this.ownerID = null;
        this.tag = null;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    protected void setTitle(String title)
    {
        this.itemTitle = title;
    }

    protected String getTitle(){return this.itemTitle;}

    void setDescription(String description)
    {
        this.itemDescription = description;
    }

    String getDescription(){return this.itemDescription;}

    private void setID(String id)
    {
        this.itemID = id;
    }

    String getID(){return this.itemID;}

    void setCategory(String category)
    {
        this.itemCategory = category;
    }

    String getCategory(){return this.itemCategory;}

    private void setPic(String pic){this.itemPic = pic;}

    private void setOwnerName(String name){this.ownerName = name;}

    String getOwnerName(){return this.ownerName;}

    private void setOwnerPic(String pic){this.ownerPic = pic;}

    String getOwnerPic(){return this.ownerPic;}

    private void setOwnerID(String id){this.ownerID = id;}

    String getOwnerID(){return this.ownerID;}

    void setPic(Bitmap picInBm)
    {
        if(picInBm == null)
            this.itemPic = null;
        else
        {
            final int COMPRESS_QUALITY = 50;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            picInBm.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, byteArrayOutputStream);
            byte[] b = byteArrayOutputStream.toByteArray();
            this.itemPic = Base64.encodeToString(b, Base64.DEFAULT);
        }
    }

    Bitmap getPic()
    {
        if(this.itemPic == null)
            return null;
        else
        {
            byte[] decodeString = Base64.decode(this.itemPic, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        }
    }

    void setItemFromJSON(JSONObject json)
    {
        try
        {
            Log.d(itemTAG, "json String is" + json.toString());
            if(json.has(ITEMID))
                this.setID(json.getString(ITEMID));

            if(json.has(ITEMTITLE))
                this.setTitle(json.getString(ITEMTITLE));

            if(json.has(ITEMPIC))
                this.setPic(json.getString(ITEMPIC));

            if(json.has(ITEMDESCRIPTION))
                this.setDescription(json.getString(ITEMDESCRIPTION));

            if(json.has(ITEMCATEGORY))
                this.setCategory(json.getString(ITEMCATEGORY));

            if(json.has(USERNAME))
                this.setOwnerName(json.getString(USERNAME));

            if(json.has(IMAGEURL))
                this.setOwnerPic(json.getString(IMAGEURL));

            if(json.has(USERID))
                this.setOwnerID(json.getString(USERID));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    JSONObject encodeItemToJSON_AddItem()
    {
        JSONObject rtnObj = new JSONObject();
        try {
            rtnObj.put(ITEMTITLE, this.itemTitle);
            rtnObj.put(ITEMCATEGORY, this.itemCategory);
            rtnObj.put(ITEMDESCRIPTION, this.itemDescription);
            rtnObj.put(ITEMPIC, this.itemPic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rtnObj;
    }

    JSONObject encodeItemToJSON_Item()
    {
        JSONObject rtnObj = new JSONObject();
        try {
            rtnObj.put(ITEMID, this.itemID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rtnObj;
    }
}
