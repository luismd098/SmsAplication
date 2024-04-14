package com.example.smsaplication.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smsaplication.databinding.FragmentNotificationsBinding;
import com.example.smsaplication.utilities.Log;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private EditText tvTerminal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvTerminal = binding.tvTerminal;
        tvTerminal.setKeyListener(null);


        final Button btnUpdate = binding.btnUpdate;
        final Button btnDelete = binding.btnDelete;

        updateLogs();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLogs();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = Log.DeleteLogs(v.getContext());
                if(status)
                    Toast.makeText(v.getContext(), "Registros eliminados.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(v.getContext(), "No se encontraron registros.", Toast.LENGTH_SHORT).show();

                tvTerminal.setText("",null);
            }
        });

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void updateLogs(){
        List logs = Log.GetLogs(getContext());
        StringBuilder finalLogs = new StringBuilder();
        for (Object log:
                logs) {
            finalLogs.append(log.toString()).append('\n');
        }
        finalLogs.append("\n\n\n\n");
        tvTerminal.setText(finalLogs.toString());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}