package com.example.smsaplication.ui.home;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smsaplication.MainActivity;
import com.example.smsaplication.R;
import com.example.smsaplication.connection.Connection;
import com.example.smsaplication.databinding.FragmentHomeBinding;
import com.example.smsaplication.services.SendSMSService;
import com.example.smsaplication.utilities.Log;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private Connection connection;
    private Switch swEnvioMensajes;
    private Context context;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ////
        context = getContext();

        swEnvioMensajes = binding.swEnvioMensajes;
        swEnvioMensajes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked)
                    stopSendSMSService();
                else
                    startSendSMSService();
            }
        });

        return root;
    }

    private void startSendSMSService(){
        boolean statusSendSMS = isRunning(SendSMSService.class);
        if(!statusSendSMS)
            context.startService(new Intent(context, SendSMSService.class));

        swEnvioMensajes.setChecked(true);
    }

    private void stopSendSMSService(){
        context.stopService(new Intent(context, SendSMSService.class));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressWarnings("deprecation")
    private boolean isRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}