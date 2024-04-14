package com.example.smsaplication.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.smsaplication.config.Settings;
import com.example.smsaplication.connection.Connection;
import com.example.smsaplication.connection.ServerCallback;
import com.example.smsaplication.utilities.Log;
import com.example.smsaplication.utilities.SmsActions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SendSMSService extends Service {

    private Runnable runnable;
    private Connection connection;
    private SmsActions smsActions;
    private boolean stopService = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // SmsActions
        smsActions = new SmsActions(this);
        // Connection
        connection = new Connection(this);
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        Handler handler = new Handler();
        final Context context = this;
        runnable = () -> {
            try {
                connection.getSMS(Settings.GET_SMS_URL, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            int status = result.getInt("status");
                            String msg = result.getString("msg");

                            if(status != 0 ){
                                throw new Exception(msg);
                            }

                            JSONArray data = result.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                int id = object.getInt("Id");
                                String numeroDestino = object.getString("NumeroDestino");
                                String mensaje = object.getString("Mensaje");
                                new SmsActions(context)
                                        .sendSMS(id,mensaje,numeroDestino);
                            }

                        } catch (JSONException e){
                            com.example.smsaplication.utilities.Log.LocalLog(context,e.hashCode(),e.getMessage());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }


                    @Override
                    public void onReject(String error) {
                        // TODO
                        Log.LocalLog(context,-1,error);
                    }
                });
            } catch (Exception e) {
                Log.LocalLog(context,e.hashCode(),e.getMessage());
            }
            if(stopService){
                return;
            }
            handler.postDelayed(runnable,10000);
        };

        handler.postDelayed(runnable,10000);

        // Get the HandlerThread's Looper and use it for our Handler
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService = true;
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }


}
