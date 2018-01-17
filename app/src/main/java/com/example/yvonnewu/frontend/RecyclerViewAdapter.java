package com.example.yvonnewu.frontend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import static com.example.yvonnewu.frontend.JSONTags.*;

import java.util.ArrayList;

/**
 * Created by Yvonne Wu on 11/26/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewViewHolder>
{
    private ArrayList<Item> items;
    private final String tag = "RecyclerView";
    private final String volleyTag = "RecyclerItem";
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<Item> items, Context context)
    {
        this.items = items;
        this.mContext = context;
    }

    @Override
    public RecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_recylerview, parent, false);
        return new RecyclerViewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewViewHolder holder, int position)
    {
        final Item mItem = items.get(position);
        holder.itemPic.setImageBitmap(mItem.getPic());
        holder.itemBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(tag, "Item getting clicked: " + mItem.getTitle());
                String sendURL = URL + "/item";
                JSONObject send = mItem.encodeItemToJSON_Item();
                VolleySingleton.getInstance(mContext).PostToServer(sendURL, send, new OnJSONReceive()
                {
                    @Override
                    public void onReceive(JSONObject received)
                    {
                        String result = null;
                        try {
                            result = received.getString(RESULT);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(TRUE.equals(result))
                        {
                            User ownerUser = User.getInstance();
                            mItem.setTag("disable");
                            ownerUser.setItemSelected(mItem, received);
                            Intent itemIntent = new Intent(mContext, ItemPageActivity.class);
                            mContext.startActivity(itemIntent);
                        }
                    }
                }, volleyTag);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    class RecyclerViewViewHolder extends RecyclerView.ViewHolder
    {
        ImageView itemPic;
        Button itemBtn;
        public RecyclerViewViewHolder(View itemView)
        {
            super(itemView);
            this.itemPic = (ImageView) itemView.findViewById(R.id.offerItemIV);
            this.itemBtn = (Button) itemView.findViewById(R.id.offerItemBtn);
        }
    }
}

