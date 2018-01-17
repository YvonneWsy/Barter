package com.example.yvonnewu.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.yvonnewu.frontend.JSONTags.*;

public class AddItemPageActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener, OnJSONReceive, Check
{
    private ImageView itemPic;
    private EditText titleField, descriptionField;
    private final int REQUEST_CAMERA = 1, REQUEST_GALLERY = 0;
    private Bitmap itemBitmap;
    private Item newItem;
    private final String addItemTAG = AddItemPageActivity.class.getSimpleName();
    private final String volleyTag = "AddItem";
    private String sendURL;
    private Spinner itemSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_page);

        //instantiate GUI
        itemSpinner = (Spinner) findViewById(R.id.itemCatSpinnerAI);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);
        //Buttons
        Button addBtn = (Button) findViewById(R.id.addItemConfirmBtnAI);
        Button cancelBtn = (Button) findViewById(R.id.cancelAddItemBtnAI);
        FloatingActionButton uploadPicBtn = (FloatingActionButton) findViewById(R.id.uploadItemPicFloarBtnAI);
        uploadPicBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        //ImageView
        itemPic = (ImageView) findViewById(R.id.itemIVAI);
        //EditText
        titleField = (EditText) findViewById(R.id.itemTitleETAI);
        descriptionField = (EditText) findViewById(R.id.itemDesETAI);

        //enable URL
        sendURL = URL + "/addItem";

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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.addItemConfirmBtnAI:
                addItem();
                break;

            case R.id.cancelAddItemBtnAI:
                cancelAddItem();
                break;

            case R.id.uploadItemPicFloarBtnAI:
                selectImage();
                break;

            default:
                return;
        }

    }

    private void selectImage()
    {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Item Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equals("Camera"))
                    takePic();
                else if(items[which].equals("Gallery"))
                    getPic();
                else if(items[which].equals("Cancel"))
                    dialog.dismiss();
            }
        });
        builder.show();
    }

    private void cancelAddItem()
    {
        /*Intent userMainIntent = new Intent(this, UserMainPageActivity.class);
        startActivity(userMainIntent);*/
        this.finish();
    }

    private void getPic()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType(getString(R.string.galleryType));
        startActivityForResult(galleryIntent.createChooser(galleryIntent, "Select Images"), REQUEST_GALLERY);
    }

    private void takePic()
    {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicIntent, REQUEST_CAMERA);
    }

    private void addItem()
    {
        //create new item
        newItem = new Item();
        newItem.setTitle(titleField.getText().toString());
        newItem.setDescription(descriptionField.getText().toString());
        newItem.setCategory(itemSpinner.getSelectedItem().toString());
        newItem.setPic(itemBitmap);
        boolean validFlag = checkFields();

        if(validFlag)
        {
            JSONObject send = newItem.encodeItemToJSON_AddItem();
            User ownerUser = User.getInstance();
            try
            {
                send.put(USERID, ownerUser.getID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*Log.d(addItemTAG, "the string send to add item is " + send.toString());
            new JSONTask(this).execute(sendURL, send.toString());*/
            VolleySingleton.getInstance(this).PostToServer(sendURL, send, this, volleyTag);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == REQUEST_CAMERA)
            {
                itemBitmap = (Bitmap) data.getExtras().get("data");
                itemPic.setImageBitmap(itemBitmap);
            }
            else if(requestCode == REQUEST_GALLERY && data != null)
            {
                try {
                    itemBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),
                            data.getData());
                    itemPic.setImageBitmap(itemBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onReceive(JSONObject received)
    {
        Log.d(addItemTAG, "receive from server "+received.toString());
        String rtnString = null;
        try
        {
            rtnString = received.getString(RESULT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(TRUE.equals(rtnString))
        {
            Intent userMainIntent = new Intent(this, UserMainPageActivity.class);
            startActivity(userMainIntent);
        }
        else
            Toast.makeText(this, "Oops", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean checkFields()
    {
        boolean rtnFlag = true;
        if(newItem.getTitle().isEmpty())
        {
            titleField.setBackgroundResource(R.drawable.focus_border_style);
            rtnFlag = false;
        }
        if(newItem.getDescription().isEmpty())
        {
            descriptionField.setBackgroundResource(R.drawable.focus_border_style);
            rtnFlag = false;
        }
        if(newItem.getPic() == null)
        {
            itemPic.setBackgroundResource(R.drawable.focus_border_style);
            rtnFlag = false;
        }
        if(!rtnFlag)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.checkFieldsAlert)
                    .setPositiveButton(R.string.okBtnAlert, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
        return rtnFlag;
    }
}
