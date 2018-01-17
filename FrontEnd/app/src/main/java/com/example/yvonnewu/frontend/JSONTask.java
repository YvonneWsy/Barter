package com.example.yvonnewu.frontend;

import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by Yvonne Wu on 10/16/2017.
 */

public class JSONTask extends AsyncTask<String, JSONObject, String>
{
    private static final String TAG = JSONTask.class.getSimpleName();
    private OnJSONReceive mJSONReceive = null;

    public JSONTask(OnJSONReceive listener){
        mJSONReceive = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection mConnection = null;
        try {
            URL profileURL = new URL(params[0]);
            JSONObject jsonObj = new JSONObject(params[1]);

            mConnection = (HttpURLConnection) profileURL.openConnection();
            mConnection.setReadTimeout(15000);
            mConnection.setConnectTimeout(15000);
            mConnection.setDoOutput(true);
            mConnection.setDoInput(true);
            mConnection.setRequestMethod("POST");
            mConnection.setRequestProperty("Content-Type", "application/json");
            mConnection.connect();

            OutputStream outStream = mConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(outStream, "UTF-8")
            );
            writer.write(jsonObj.toString());
            writer.flush();
            writer.close();
            outStream.close();

            int contentLength = mConnection.getContentLength();
            Log.d(TAG, "content length is: " + Integer.toString(contentLength));
            int responseCode = mConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(mConnection.getInputStream())
                );
                StringBuffer buffer = new StringBuffer("");
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    //break;
                }
                reader.close();
                return buffer.toString();
            } else{
                JSONObject rtn = new JSONObject();
                rtn.put("result", "timeout");
                return rtn.toString();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(mConnection != null)
                mConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "Recv: "+s);
        JSONObject rtnJSON = null;
        try {
            rtnJSON = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"Going to return: "+rtnJSON.toString());
        if(mJSONReceive != null)
        {
            Log.d(TAG,"calling onReceived");
            mJSONReceive.onReceive(rtnJSON);
        }
    }
}

