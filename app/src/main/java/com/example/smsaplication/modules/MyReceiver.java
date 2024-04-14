package com.example.smsaplication.modules;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.ArrayMap;
import android.widget.Toast;

import com.example.smsaplication.config.Settings;
import com.example.smsaplication.connection.Connection;
import com.example.smsaplication.connection.ServerCallback;
import com.example.smsaplication.utilities.Log;
import com.example.smsaplication.utilities.Params;
import com.example.smsaplication.utilities.SmsActions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle == null)  return;
        try{
            String actionType = (bundle.get(Params.SMS_ACTION_TYPE) != null ? String.valueOf(bundle.get(Params.SMS_ACTION_TYPE)) : "NULL");
            int smsId = (bundle.get("SMS_ID") != null ? (int) bundle.get("SMS_ID") : 0);

            if(actionType.equals(Params.SMS_ACTION_SENT))
                processSentAction(context,getResultCode(),smsId);
            else if(actionType.equals(Params.SMS_ACTION_DELIVERED))
                processDeliverAction(context,getResultCode(),smsId);

        } catch (Exception e){
            Log.LocalLog(context,e.hashCode(),e.getMessage());
        }

    }

    private void processSentAction(final Context context,int status, int smsId){
        switch (status)
        {
            case Activity.RESULT_OK:
                Log.LocalLog(context,status,"Mensaje con ID " + smsId + " enviado");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Log.SendLog(context,status," Error al enviar el mensaje con ID: " + smsId + ".(Generic failure) (RESULT_ERROR_GENERIC_FAILURE)");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Log.SendLog(context,status," Error al enviar el mensaje con ID: " + smsId + ".(No service) (RESULT_ERROR_NO_SERVICE)");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Log.SendLog(context,status," Error al enviar el mensaje con ID: " + smsId + ".(Null PDU) (RESULT_ERROR_NULL_PDU)");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Log.SendLog(context,status," Error al enviar el mensaje con ID: " + smsId + ".(Radio off) (RESULT_ERROR_RADIO_OFF)");
                break;
            default:
                Log.SendLog(context,status," Error al enviar el mensaje con ID: " + smsId + ".(Unknown) (ERROR DESCONOCIDO)");
                break;
        }
    }

    private void processDeliverAction(final Context context,int status, int smsId){
        switch (status)
        {
            case Activity.RESULT_OK:
                   // Confirmar el envio del mensaje
                try{
                    new Connection(context)
                            .post(Settings.POST_CONFIRMAR_SMS_URL, new ServerCallback() {
                                    @Override
                                        public void onSuccess(JSONObject result) {
                                            try {
                                                int status = result.getInt("status");
                                                String msg = result.getString("msg");

                                                if(status != 0 ){
                                                    throw new Exception(msg);
                                                }

                                                Log.LocalLog(context,1,"Mensaje con ID " + smsId + " entregado correctamente.");

                                            } catch (JSONException e){
                                                Log.LocalLog(context,e.hashCode(),e.getMessage());
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        @Override
                                        public void onReject(String error) {
                                            Log.LocalLog(context,0,error);
                                        }
                                    },
                                    new JSONObject()
                                            .put("Id",smsId)
                            );
                } catch (Exception ex){
                    Log.LocalLog(context,ex.hashCode(),ex.getMessage());
                }

                break;
            case Activity.RESULT_CANCELED:
                Log.SendLog(context,status," Error al enviar el mensaje con ID: " + smsId + ".(Canceled) (RESULT_CANCELED)");
                break;
            default:
                Log.SendLog(context,status," Error al enviar el mensaje con ID: " + smsId + ".(Unknown) (ERROR DESCONOCIDO)");
                break;
        }
    }


}
