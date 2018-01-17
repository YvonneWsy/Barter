package com.example.yvonnewu.frontend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.yvonnewu.frontend.JSONTags.*;

import java.util.ArrayList;

/**
 * Created by Yvonne Wu on 11/13/2017.
 */

public class SelectionAdapter extends BaseAdapter
{
    private Context mContext;
    private final ArrayList<SelectableItem> items;
    private View mView;
    private LayoutInflater mLayoutInflater;
    private final String volleyTag = "Selection";

    public SelectionAdapter(Context mContext, ArrayList<SelectableItem> items)
    {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return items.get(position).getmItem();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public boolean isChecked(int position)
    {
        return items.get(position).isChecked();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        mView = convertView;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SelectableItem.ViewHolder viewHolder = new SelectableItem.ViewHolder();
        if(convertView == null)
        {
            mView = mLayoutInflater.inflate(R.layout.inventory_selection_listview, null);

            viewHolder.checkBox = (CheckBox) mView.findViewById(R.id.selectionCB);
            viewHolder.pic = (ImageView) mView.findViewById(R.id.selectionIV);
            viewHolder.name = (TextView) mView.findViewById(R.id.selectionTV);

            mView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (SelectableItem.ViewHolder) mView.getTag();
        }

        final Item item= items.get(position).getmItem();

        viewHolder.pic.setImageBitmap(item.getPic());
        viewHolder.name.setText(item.getTitle());
        viewHolder.checkBox.setTag(position);

        viewHolder.pic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String sendURL = URL + "/item";
                JSONObject send = new JSONObject();
                try
                {
                    send.put(ITEMID, item.getID());
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
                                item.setTag("disable");
                                User.getInstance().setItemSelected(item, received);
                                Intent itemIntent = new Intent(mContext, ItemPageActivity.class);
                                itemIntent.putExtra(TAG, SELECTION);
                                mContext.startActivity(itemIntent);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, volleyTag);
            }
        });

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean newState = !items.get(position).isChecked();
                items.get(position).setChecked(newState);
            }
        });

        viewHolder.checkBox.setChecked(isChecked(position));
        return mView;
    }
}
