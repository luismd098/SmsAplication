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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smsaplication.connection.Connection;
import com.example.smsaplication.databinding.FragmentHomeBinding;
import com.example.smsaplication.services.SendSMSService;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private Connection connection;
    private Button btnStatus;
    private Context context;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ////
        context = getContext();

        btnStatus = binding.btnStatus;
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = isRunning(SendSMSService.class);
//                Toast.makeText(getContext(), aux, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context,MyReceiver.class);
//                Intent intent2 = new Intent(context,MyReceiver.class);
//                intent2.putExtra("DELIVERED",1);
//                PendingIntent sentIntent = PendingIntent.getBroadcast(context,0, intent,PendingIntent.FLAG_MUTABLE);
//                PendingIntent deliveryIntent = PendingIntent.getBroadcast(context,0, intent2,PendingIntent.FLAG_MUTABLE);
//
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage("4521902181",null,"Prueba",sentIntent,deliveryIntent);
            }
        });


        return root;
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