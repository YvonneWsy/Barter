package com.example.yvonnewu.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.yvonnewu.frontend.JSONTags.*;


public class InventoryPageActivity extends AppCompatActivity
        implements OnJSONReceive, GridView.OnItemClickListener{
    private User ownerUser;
    private TextView itemCountTxt;
    private GridView invGrid;
    private final String inventoryTAG = InventoryPageActivity.class.getSimpleName();
    private final String sendURL = URL + "/item";
    private final String volleyTag = "Inventory";
    private Item itemClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_page);

        //get the user instance
        ownerUser = User.getInstance();

        //instantiate all the GUI components
        Toolbar inventoryBar = (Toolbar) findViewById(R.id.inventoryToolbar);
        setSupportActionBar(inventoryBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        invalidateOptionsMenu();
        ImageView profilePic = (ImageView) findViewById(R.id.profilePicInv);
        TextView toolBarTitle = (TextView) findViewById(R.id.toolBarTitleInv);
        String imageURL = ownerUser.getItemListOwnerPic();
        Log.d(inventoryTAG, "the image URL is " + imageURL);
        if(imageURL != null)
            Glide.with(this).load(imageURL)
                    .apply(RequestOptions.circleCropTransform()).into(profilePic);
        toolBarTitle.setText("  " + ownerUser.getItemListOwnerName() + " INVENTORY");
        itemCountTxt = (TextView) findViewById(R.id.itemCountTxt);
        invGrid = (GridView) findViewById(R.id.inventoryGrid);
        invGrid.setOnItemClickListener(this);
        setUserInventory();
    }

    private void setUserInventory()
    {
        ArrayList<Item> itemList = ownerUser.getItemList();
        //user has no item in inventory
        if(itemList.isEmpty())
            itemCountTxt.setText("Number of Item in Inventory:" + " 0");
        //user has items in inventory
        else
        {
            ViewAdapter gridApt = new ViewAdapter(this, itemList, "Grid");
            invGrid.setAdapter(gridApt);
            itemCountTxt.setText("Number of Item in Inventory: " + Integer.toString(itemList.size()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_inventory_page, menu);
        MenuItem addItemMenu = menu.findItem(R.id.action_addItem);
        if(!ownerUser.getID().equals(ownerUser.getItemListOwnerID()))
            addItemMenu.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_goHome:
                goHome();
                return true;

            case R.id.action_addItem:
                addItem();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addItem()
    {
        Intent addItemIntent = new Intent(this, AddItemPageActivity.class);
        startActivity(addItemIntent);
    }

    private void goHome()
    {
        Intent userMainIntent = new Intent(this, UserMainPageActivity.class);
        startActivity(userMainIntent);
    }

    @Override
    public void onReceive(JSONObject received)
    {
        Log.d(inventoryTAG, "receive from item " + received.toString());
        String result = null;
        try {
            result = received.getString(RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(TRUE.equals(result))
        {
            ownerUser.setItemSelected(itemClicked, received);
            Intent itemIntent = new Intent(this, ItemPageActivity.class);
            startActivity(itemIntent);
        }
        else
            Toast.makeText(this, R.string.serverFail, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        itemClicked = (Item) invGrid.getItemAtPosition(position);
        Log.d(inventoryTAG, "item title is" + itemClicked.getTitle());
        JSONObject send = itemClicked.encodeItemToJSON_Item();
        /*Log.d(inventoryTAG, "sending " + send.toString());
        new JSONTask(this).execute(sendURL, send.toString());*/
        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
    }
}
