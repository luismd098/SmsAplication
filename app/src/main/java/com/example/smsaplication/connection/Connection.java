package com.example.smsaplication.connection;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smsaplication.config.Settings;

import org.json.JSONException;
import org.json.JSONObject;

public class Connection {
    private Context context;
    private static final String TAG =  "Connection";

    public Connection(Context context){
        this.context = context;
    }

    public void getSMS(final String URL, final ServerCallback callback ) {

        RequestQueue volleyQueue = Volley.newRequestQueue(this.context);
        JSONObject obj = new JSONObject();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL, obj, new Response.Listener<JSONObject>()
        {

            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response); // call call back function here

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onReject(error.getMessage());

            }
        }){
            @Override
            public String getBodyContentType()
            {
                return "application/json";
            }
        };


        int socketTimeout = 10000;//10 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);

        // Adding request to request queue
        volleyQueue.add(jsonObjReq);

    }

    public void post(final String URL, final ServerCallback callback, JSONObject params) throws Exception {

        RequestQueue volleyQueue = Volley.newRequestQueue(this.context);
//        JSONObject obj = new JSONObject();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL, params, new Response.Listener<JSONObject>()
        {

            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response); // call call back function here

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onReject(error.getMessage());

            }
        }){
            @Override
            public String getBodyContentType()
            {
                return "application/json";
            }
        };


        int socketTimeout = 10000;//10 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        // Adding request to request queue
        volleyQueue.add(jsonObjReq);

    }
}
