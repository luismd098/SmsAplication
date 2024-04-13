package com.example.smsaplication.connection;

import org.json.JSONObject;

public interface ServerCallback {

    void onSuccess(JSONObject result);
    void onReject(String error);
}
