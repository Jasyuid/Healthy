package com.example.healthy2;

import android.Manifest;

import a5.com.a5bluetoothlibrary.A5BluetoothCallback;
import a5.com.a5bluetoothlibrary.A5Device;
import a5.com.a5bluetoothlibrary.A5DeviceManager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.Button;

public class ConnectActivity extends AppCompatActivity implements A5BluetoothCallback {

    private CountDownTimer timer = null;

    private TextView status_text = null;
    private TextView connected_text = null;

    private Button scan_button = null;
    private Button active_button = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        requestPermission();

        status_text = findViewById(R.id.status);
        connected_text = findViewById(R.id.connected_text);
        status_text.setText("Status: Idle");
        connected_text.setText("Device: None");
        connected_text.setTextColor(0xFFAA0000);

        scan_button = findViewById(R.id.scan_button);
        active_button = findViewById(R.id.active_button);
        active_button.setEnabled(false);

        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_text.setText("Status: Searching for device");
                scan_button.setEnabled(false);
                scan_button.setText("Scan for Device");
                if(Healthy.connected) {
                    Healthy.device = null;
                    Healthy.connected = false;
                }
                startBluetooth();
            }
        });

        active_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivActivity.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            Log.d("tag", "hi");
            Healthy.device.disconnect();
            Healthy.connected = false;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void startBluetooth() {
        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
        BluetoothManager bluetoothManager = (BluetoothManager) Healthy.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 999);
        } else {
            A5DeviceManager.INSTANCE.setCallback(this);
            A5DeviceManager.INSTANCE.scanForDevices();
        }
    }

    private void requestPermission() {
        Log.d("tag", "1");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            )
                    != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                ) {
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                            998
                    );
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
                    != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                ) {
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            998
                    );
                }
            }
        }
    }

    @Override
    public void bluetoothIsSwitchedOff() {

    }

    @Override
    public void deviceConnected(A5Device a5Device) {

    }

    @Override
    public void deviceDisconnected(A5Device a5Device) {

    }

    @Override
    public void deviceFound(A5Device a5Device) {
        Healthy.device = a5Device;
        Healthy.connected = true;
        A5DeviceManager.INSTANCE.connect(Healthy.getInstance(), Healthy.device);
        connected_text.setText("Device: " + Healthy.device.getDevice().getName());
        connected_text.setTextColor(0xFF00AA00);
        active_button.setEnabled(true);
        scan_button.setText("Find New Device");

    }

    @Override
    public void didReceiveIsometric(A5Device a5Device, int i) {
        Healthy.data.add(i);
    }

    @Override
    public void on133Error() {

    }

    @Override
    public void onWriteCompleted(A5Device a5Device, String s) {

    }

    @Override
    public void searchCompleted() {
        if(Healthy.connected){
            status_text.setText("Status: Device Found");
        } else {
            status_text.setText("Status: Try Again");
        }
        scan_button.setEnabled(true);
    }
}
