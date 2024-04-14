package com.example.smsaplication.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.smsaplication.config.Settings;
import com.example.smsaplication.connection.Connection;
import com.example.smsaplication.connection.ServerCallback;
import com.example.smsaplication.utilities.sqlite.LogTableContract;
import com.example.smsaplication.utilities.sqlite.SQLiteHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Log {



    public static void SendLog(final Context context,int status, String message) {
        try{
            LocalLog(context,status,message);
            new Connection(context)
                    .post(Settings.POST_LOGS_URL, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            // Error enviado correctamente
                            LocalLog(context,1,"Envio de Log exitoso");
                        }

                        @Override
                        public void onReject(String error) {
                            LocalLog(context,0,error);
                        }
                    },
                            new JSONObject()
                                    .put("Status",status)
                                    .put("Message",message)
                    );
        } catch (Exception ex){
            LocalLog(context,ex.hashCode(),ex.getMessage());
        }
    }

    public static long LocalLog(final Context context,int status, String message) {
        SQLiteHelper sqLiteHelper = new SQLiteHelper(context);
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LogTableContract.LogEntry.COLUMN_NAME_STATUS,status);
        values.put(LogTableContract.LogEntry.COLUMN_NAME_MESSAGE,message);
        values.put(LogTableContract.LogEntry.COLUMN_NAME_DATE,Date.getDate());

        long newRowId = db.insert(LogTableContract.LogEntry.TABLE_NAME,null,values);
        return newRowId;
    }

    public static boolean DeleteLogs(final Context context){
        try{
            SQLiteHelper sqLiteHelper = new SQLiteHelper(context);
            SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
            int result = db.delete(LogTableContract.LogEntry.TABLE_NAME,null,null);
            return result > 0;
        } catch ( Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;

    }

    public static List GetLogs(final Context context){
        SQLiteHelper sqLiteHelper = new SQLiteHelper(context);
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        String[] projection = {
                "_ID",
                LogTableContract.LogEntry.COLUMN_NAME_STATUS,
                LogTableContract.LogEntry.COLUMN_NAME_MESSAGE,
                LogTableContract.LogEntry.COLUMN_NAME_DATE
        };

        String query = "SELECT * FROM " + LogTableContract.LogEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        List result = new ArrayList();

        try{
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String raw = "";

                    String date = cursor.getString(cursor.getColumnIndexOrThrow(LogTableContract.LogEntry.COLUMN_NAME_DATE));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow(LogTableContract.LogEntry.COLUMN_NAME_MESSAGE));
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(LogTableContract.LogEntry.COLUMN_NAME_STATUS));
                    raw += "| " + date + " | STATUS: " + status + " | " + message ;
                    result.add(raw);

                    cursor.moveToNext();
                }
            }
        } catch (Exception ex){
            Toast.makeText(context,ex.getMessage(),Toast.LENGTH_LONG).show();
        } finally {
            cursor.close();
        }

        return result;


    }


}
