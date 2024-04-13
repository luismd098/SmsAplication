package com.example.smsaplication.modules;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.example.smsaplication.utilities.Params;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("ResultCode: " + getResultCode());
        Bundle bundle = intent.getExtras();
        if (bundle == null)  return;

        String actionType = (bundle.get(Params.SMS_ACTION_TYPE) != null ? String.valueOf(bundle.get(Params.SMS_ACTION_TYPE)) : "NULL");

        if(actionType == Params.SMS_ACTION_SENT)
            processSentAction(getResultCode());
        else if(actionType == Params.SMS_ACTION_DELIVERED)
            processDeliverAction(getResultCode());


    }

    private void processSentAction(int status){
        switch (status)
        {
            case Activity.RESULT_OK:
//                Toast.makeText(getActivity().getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                Toast.makeText(getActivity().getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
            case SmsManager.RESULT_ERROR_NO_SERVICE:
//                Toast.makeText(getActivity().getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
            case SmsManager.RESULT_ERROR_NULL_PDU:
//                Toast.makeText(getActivity().getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
            case SmsManager.RESULT_ERROR_RADIO_OFF:
//                Toast.makeText(getActivity().getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void processDeliverAction(int status){
        switch (status)
        {
            case Activity.RESULT_OK:
//                Toast.makeText(getActivity().getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                break;
            case Activity.RESULT_CANCELED:
//                Toast.makeText(getActivity().getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


}
