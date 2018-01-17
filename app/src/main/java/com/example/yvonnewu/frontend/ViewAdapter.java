package com.example.yvonnewu.frontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yvonne Wu on 10/25/2017.
 */

public class ViewAdapter extends BaseAdapter
{
    private Context mContext;
    private final ArrayList<Item> items;
    private View mView;
    private LayoutInflater mLayoutInflater;
    private String selection;
    private ImageView itemPic;
    private TextView itemTxt;

    public ViewAdapter(Context context, ArrayList<Item> items, String selection)
    {
        this.mContext = context;
        this.items = items;
        this.selection = selection;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    //return the actual object at the specified position
    public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override
    //return the row id of the item
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
        {
            mView = new View(mContext);

            if("Grid".equals(selection))
            {
                mView = mLayoutInflater.inflate(R.layout.inventory_gridview, null);
                itemPic = (ImageView) mView.findViewById(R.id.gridIV);
                itemTxt = (TextView) mView.findViewById(R.id.gridTV);
            }
            else if("List".equals(selection))
            {
                mView = mLayoutInflater.inflate(R.layout.search_result_listview, null);
                itemPic = (ImageView) mView.findViewById(R.id.listIV);
                itemTxt = (TextView) mView.findViewById(R.id.listTV);
            }
            itemPic.setImageBitmap(items.get(position).getPic());
            itemTxt.setText(items.get(position).getTitle());
        }
        return mView;
    }
}
