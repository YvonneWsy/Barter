package com.example.yvonnewu.frontend;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by Yvonne Wu on 11/24/2017.
 */

public class VolleySingleton
{
    private static final String tag = "Volley";
    private static VolleySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mContext;

    private VolleySingleton(Context context)
    {
        mContext = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context)
    {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        return requestQueue;
    }

    public<T> void addToRequestQueue(Request<T> request, String tag)
    {
        request.setTag(tag);
        requestQueue.add(request);
    }

    public  void cancelPendingRequests(Object tag)
    {
        if(requestQueue != null)
            requestQueue.cancelAll(tag);
    }

    public void PostToServer(final String sendURL, final JSONObject send, final OnJSONReceive callback, final String tag)
    {
        Log.d(tag, "Sending: "+send.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, sendURL, send,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(tag,"Receiving: " + response.toString());
                        callback.onReceive(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
                cancelPendingRequests(tag);
                PostToServer(sendURL, send,callback,tag);
            }
        });

        addToRequestQueue(jsonObjectRequest, tag);
    }
}
