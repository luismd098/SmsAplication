package com.example.smsaplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.smsaplication.config.Settings;
import com.example.smsaplication.connection.Connection;
import com.example.smsaplication.connection.ServerCallback;
import com.example.smsaplication.services.SendSMSService;
import com.example.smsaplication.utilities.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smsaplication.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        // Iniciar servicio SendSMS
//        startService(new Intent(this, SendSMSService.class));

        boolean statusSendSMS = isRunning(SendSMSService.class);
        Switch swEnvioMensajes = findViewById(R.id.swEnvioMensajes);
        swEnvioMensajes.setChecked(statusSendSMS);

        final Context context = this;

        final Button btn = findViewById(R.id.btnStatus);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new Connection(view.getContext()).getSMS(Settings.GET_SMS_URL, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            System.out.println(result);
    //                        result.keys().forEachRemaining(key -> {
    //                            try {
    //                                Object value = result.get(key);
    //                            } catch (JSONException e) {
    //                                com.example.smsaplication.utilities.Log.LocalLog(e.hashCode(),e.getMessage());
    //                            }
    //                        });
    //                        smsActions.sendSMS("PRUEBA SERVICE","4521902181");
                        }

                        @Override
                        public void onReject(String error) {
                            System.out.println(error);
                            // TODO
                            System.out.println(Log.LocalLog(context,-1,error));
    //                        com.example.smsaplication.utilities.Log.SendLog(context,-1,error);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private boolean isRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}