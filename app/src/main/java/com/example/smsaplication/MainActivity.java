package com.example.smsaplication;

import static com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.InstallState.STATE_CANCELED;
import static com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.InstallState.STATE_COMPLETED;
import static com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.InstallState.STATE_FAILED;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.smsaplication.config.Settings;
import com.example.smsaplication.connection.Connection;
import com.example.smsaplication.connection.ServerCallback;
import com.example.smsaplication.services.SendSMSService;
import com.example.smsaplication.utilities.SmsActions;
import com.google.android.gms.common.api.OptionalModuleApi;
import com.google.android.gms.common.moduleinstall.InstallStatusListener;
import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smsaplication.databinding.ActivityMainBinding;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int SEND_SMS_REQUEST_CODE = 100;
    private Switch swEnvioMensajes;

    private ModuleInstallClient moduleInstallClient;

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
        // Revisar el status del servicio de envio de mensajes
        boolean statusSendSMS = isRunning(SendSMSService.class);
        moduleInstallClient = ModuleInstall.getClient(this);
//        f();
        swEnvioMensajes = findViewById(R.id.swEnvioMensajes);

        swEnvioMensajes.setChecked(statusSendSMS);

//        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.ThemeAlert));
//        builder.setMessage("HELLO!");
//        builder .setCancelable(true)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        //dialog.cancel();
//                    }
//                })
//                .setNeutralButton("Maybe", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//        AlertDialog alertdialog = builder.create();
//        alertdialog.show();

//        checkPermission(Manifest.permission.SEND_SMS,SEND_SMS_REQUEST_CODE);

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

    private void startSendSMSService(){
        boolean statusSendSMS = isRunning(SendSMSService.class);
        if(!statusSendSMS)
            startService(new Intent(MainActivity.this, SendSMSService.class));

        swEnvioMensajes.setChecked(true);
    }

    public void f(){

        ModuleInstallClient moduleInstallClient = ModuleInstall.getClient(getBaseContext());

        OptionalModuleApi optionalModuleApi = GmsBarcodeScanning.getClient(getBaseContext());
        moduleInstallClient
                .areModulesAvailable(optionalModuleApi)
                .addOnSuccessListener(
                        response -> {
                            if (response.areModulesAvailable()) {
                                // Modules are present on the device...
                                scann();
                            } else {
                                // Modules are not present on the device...
                                moduleInstall();
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Handle failure…
                        });


    }

    private void scann(){
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_ALL_FORMATS)
                .enableAutoZoom()
                .allowManualInput()
                .build();

        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(getBaseContext());
        scanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Task completed successfully
                            String rawValue = barcode.getRawValue();
                        })
                .addOnCanceledListener(
                        () -> {
                            // Task canceled
//                            promise.reject("Canceled");
                        })
                .addOnFailureListener(
                        e -> {
                            e.printStackTrace();
                            Log.d("Scan",e.getMessage());
                            // Task failed with an exception
//                            promise.reject(e.getMessage());
                        });
    }
    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        // Iniciar con el servicio de envio de mensajes
        else {
            startSendSMSService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == SEND_SMS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSendSMSService();
            }
            else {
                Toast.makeText(MainActivity.this, "Permiso para enviar mensajes denegado", Toast.LENGTH_SHORT) .show();
            }
        }
    }


    public void onScanButtonClicked() {

        GmsBarcodeScannerOptions.Builder optionsBuilder = new GmsBarcodeScannerOptions.Builder();



        OptionalModuleApi optionalModuleApi = GmsBarcodeScanning.getClient(getBaseContext());
        moduleInstallClient
                .areModulesAvailable(optionalModuleApi)
                .addOnSuccessListener(
                        response -> {
                            if (response.areModulesAvailable()) {
                                // Modules are present on the device...

                            } else {
                                // Modules are not present on the device...
                                moduleInstall();
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Handle failure…
                        });
    }

    final class ModuleInstallProgressListener implements InstallStatusListener {
        @Override
        public void onInstallStatusUpdated(ModuleInstallStatusUpdate update) {
            ModuleInstallStatusUpdate.ProgressInfo progressInfo = update.getProgressInfo();
            // Progress info is only set when modules are in the progress of downloading.
            if (progressInfo != null) {
                int progress =
                        (int) (progressInfo.getBytesDownloaded() * 100 / progressInfo.getTotalBytesToDownload());
                // Set the progress for the progress bar.
                System.out.println(progress);
            }
            // Handle failure status maybe…

            // Unregister listener when there are no more install status updates.
            if (isTerminateState(update.getInstallState())) {

                moduleInstallClient.unregisterListener(this);
            }
        }

        public boolean isTerminateState(@ModuleInstallStatusUpdate.InstallState int state) {
            return state == STATE_CANCELED || state == STATE_COMPLETED || state == STATE_FAILED;
        }
    }

    private void moduleInstall(){
        InstallStatusListener listener = new ModuleInstallProgressListener();

        OptionalModuleApi optionalModuleApi = GmsBarcodeScanning.getClient(getBaseContext());
        ModuleInstallRequest moduleInstallRequest =
                ModuleInstallRequest.newBuilder()
                        .addApi(optionalModuleApi)
                        // Add more API if you would like to request multiple optional modules
                        //.addApi(...)
                        // Set the listener if you need to monitor the download progress
                        .setListener(listener)
                        .build();

        moduleInstallClient.installModules(moduleInstallRequest)
                .addOnSuccessListener(
                        response -> {
                            if (response.areModulesAlreadyInstalled()) {
                                // Modules are already installed when the request is sent.
                                Toast.makeText(this, "areModulesAlreadyInstalled", Toast.LENGTH_SHORT).show();
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            // Handle failure...
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

    }

}