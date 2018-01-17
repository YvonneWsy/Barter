package com.example.yvonnewu.frontend;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Yvonne Wu on 11/13/2017.
 */

public class SelectableItem
{
    private boolean checked;
    private Item mItem;

    public SelectableItem(boolean checked, Item mItem) {
        this.checked = checked;
        this.mItem = mItem;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Item getmItem() {
        return this.mItem;
    }

    static class ViewHolder
    {
        CheckBox checkBox;
        ImageView pic;
        TextView name;
    }
}
