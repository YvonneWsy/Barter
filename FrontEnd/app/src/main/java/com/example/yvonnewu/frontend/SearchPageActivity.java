package com.example.yvonnewu.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import static com.example.yvonnewu.frontend.JSONTags.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchPageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, OnJSONReceive
{
    private Spinner catSpinner;
    private EditText locationField;
    private User ownerUser;
    private String sendURL;
    private final String searchTAG = SearchPageActivity.class.getSimpleName();
    private final String volleyTag = "Search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        ownerUser = User.getInstance();
        sendURL = URL + "/browsing";

        //instantiate GUI
        catSpinner = (Spinner) findViewById(R.id.catFilterSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(adapter);
        locationField = (EditText) findViewById(R.id.locationFilterEditTxt);
        Button searchBtn = (Button) findViewById(R.id.searchConfirmBtn);
        searchBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.searchConfirmBtn:
                search();
                break;

            default:
                return;
        }
    }

    private void search()
    {
        String location = locationField.getText().toString();
        String category = catSpinner.getSelectedItem().toString();

        JSONObject send = new JSONObject();
        try
        {
            if(location.isEmpty()) {
                Log.d(searchTAG, "location is empty");
                send.put(LOCATION, NULL);
            } else
                send.put(LOCATION, location);
            if("All".equals(category))
                send.put(ITEMCATEGORY, NULL);
            else
                send.put(ITEMCATEGORY, category);
            send.put(USERID, ownerUser.getID());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        /*Log.d(searchTAG, "sending " + send.toString());
        new JSONTask(this).execute(sendURL, send.toString());*/
        VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        Toast.makeText(this,
                R.string.itemCatSelecWarning,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(JSONObject received)
    {
        Log.d(searchTAG, "Received: " + received.toString());
        String result = "";
        try {
            result = received.getString(RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(TRUE.equals(result))
        {
            ownerUser.setItemList(received);
            Intent searchResultIntent = new Intent(this, SearchResultPageActivity.class);
            startActivity(searchResultIntent);
        }
        else
            Toast.makeText(this, R.string.serverFail, Toast.LENGTH_SHORT).show();
    }
}
