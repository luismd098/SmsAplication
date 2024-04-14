package com.example.smsaplication.utilities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.smsaplication.config.Settings;
import com.example.smsaplication.connection.Connection;
import com.example.smsaplication.connection.ServerCallback;
import com.example.smsaplication.modules.MyReceiver;

import org.json.JSONException;
import org.json.JSONObject;

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
    public void sendSMS(final int id,final String message,final String phoneNumber)
    {
        SmsManager sms = SmsManager.getDefault();

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(Params.SMS_ACTION_SENT), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(Params.SMS_ACTION_DELIVERED), PendingIntent.FLAG_IMMUTABLE);

        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                processSentAction(getResultCode(),id);
                context.unregisterReceiver(this);
            }
        }, new IntentFilter(Params.SMS_ACTION_SENT));

        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1)
            {
                processDeliverAction(getResultCode(),id);
                context.unregisterReceiver(this);
            }
        }, new IntentFilter(Params.SMS_ACTION_DELIVERED));

        try{
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void processSentAction(int status, int smsId){
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

    private void processDeliverAction(int status, int smsId){
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

                                            } catch (Exception e) {
                                                Log.LocalLog(context,e.hashCode(),e.getMessage());
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
