package com.example.yvonnewu.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import static com.example.yvonnewu.frontend.JSONTags.*;

import java.util.ArrayList;

public class SearchResultPageActivity extends AppCompatActivity implements OnJSONReceive, ListView.OnItemClickListener {
    private User ownerUser;
    private ListView searchResultView;
    private ImageView notFoundPic, notFoundTxt;
    private Item itemClicked;
    private final String searchResultTAG = SearchResultPageActivity.class.getSimpleName();
    private final String sendURL = URL + "/item";
    private final String volleyTag = "SearchResult";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_page);

        ownerUser = User.getInstance();

        //instantiate all the GUI components
        Toolbar mBar = (Toolbar) findViewById(R.id.searchResultTool);
        setSupportActionBar(mBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notFoundPic = (ImageView) findViewById(R.id.notFoundIVSR);
        notFoundTxt = (ImageView) findViewById(R.id.notFoundTxt);
        searchResultView = (ListView) findViewById(R.id.searchListView);
        setSearchResult();
    }

    private void setSearchResult()
    {
        ArrayList<Item> itemList = ownerUser.getItemList();
        if(itemList.isEmpty())
        {
            searchResultView.setVisibility(View.GONE);
        }
        else
        {
            notFoundTxt.setVisibility(View.GONE);
            notFoundPic.setVisibility(View.GONE);
            ViewAdapter listApt = new ViewAdapter(this, itemList, "List");
            searchResultView.setAdapter(listApt);
            searchResultView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        itemClicked = (Item) searchResultView.getItemAtPosition(position);
        JSONObject send = itemClicked.encodeItemToJSON_Item();
        /*new JSONTask(this).execute(sendURL, send.toString());*/
        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_search_result_page, menu);
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

            case R.id.action_search:
                search();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void search()
    {
        Intent searchIntent = new Intent(this, SearchPageActivity.class);
        startActivity(searchIntent);
    }

    private void goHome()
    {
        Intent homeIntent = new Intent(this, UserMainPageActivity.class);
        startActivity(homeIntent);
    }

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
            ownerUser.setItemSelected(itemClicked, received);
            Intent itemIntent = new Intent(this, ItemPageActivity.class);
            itemIntent.putExtra(FROM, SEARCH);
            startActivity(itemIntent);
        }
        else
            Toast.makeText(this, R.string.serverFail, Toast.LENGTH_SHORT).show();
    }
}
