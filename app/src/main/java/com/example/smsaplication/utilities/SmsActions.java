package com.example.smsaplication.utilities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.smsaplication.modules.MyReceiver;

import java.util.Date;

public class SmsActions {


    private Context context;

    public SmsActions(Context context){
        this.context = context;
    }

    /**
     * Funcion para realizar le envio de SMS
     *
     *  El estatus del mensaje se envia a travez del broadcast, el cual se controla en la clase MyReceiver.java
     * */
    public void sendSMS(final String message,final String phoneNumber)
    {
        SmsManager sms = SmsManager.getDefault();

        Intent sentIntent = new Intent(context,MyReceiver.class);
        sentIntent.putExtra(Params.SMS_ACTION_TYPE,Params.SMS_ACTION_SENT);

        Intent deliveredIntent = new Intent(context,MyReceiver.class);
        deliveredIntent.putExtra(Params.SMS_ACTION_TYPE,Params.SMS_ACTION_DELIVERED);

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, deliveredIntent, PendingIntent.FLAG_IMMUTABLE);

        try{
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
