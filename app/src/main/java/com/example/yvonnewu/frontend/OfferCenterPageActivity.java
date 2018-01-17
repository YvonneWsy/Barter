package com.example.yvonnewu.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

public class OfferCenterPageActivity extends AppCompatActivity
{
    private final String offerCenterTag = OfferCenterPageActivity.class.getSimpleName();
    private User owner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_center_page);

        owner = User.getInstance();

        //instantiate all the GUI components
        Toolbar mBar = (Toolbar) findViewById(R.id.toolBarOC);
        setSupportActionBar(mBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ExpandableListView offerELV = (ExpandableListView) findViewById(R.id.offerListELVOC);
        ExpandableListViewAdapter adp = new ExpandableListViewAdapter(this, owner.getOfferList());
        offerELV.setAdapter(adp);
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
}
