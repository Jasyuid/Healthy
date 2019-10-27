package com.example.healthy2;

import android.util.Log;

import a5.com.a5bluetoothlibrary.A5BluetoothCallback;
import a5.com.a5bluetoothlibrary.A5Device;
import a5.com.a5bluetoothlibrary.A5DeviceManager;

public class A5Callback implements A5BluetoothCallback {

    public A5Callback() {
        Log.d("tag", "yes");

    }

    @Override
    public void bluetoothIsSwitchedOff() {
        Log.d("tag", "Bluetooth is off!");
    }

    @Override
    public void deviceConnected(A5Device a5Device) {

    }

    @Override
    public void deviceDisconnected(A5Device a5Device) {

    }

    @Override
    public void deviceFound(A5Device a5Device) {

        Log.d("tag", "Device: " + a5Device.getDevice().getName());
        Healthy.device = a5Device;
    }

    @Override
    public void didReceiveIsometric(A5Device a5Device, int i) {
        Healthy.data.add(i);
        Log.d("tag", "" + Healthy.data.size());
    }

    @Override
    public void on133Error() {

    }

    @Override
    public void onWriteCompleted(A5Device a5Device, String s) {

    }

    @Override
    public void searchCompleted() {

        Log.d("tag", "coolio");
        if(Healthy.device != null) {
            A5DeviceManager.INSTANCE.connect(Healthy.getInstance(), Healthy.device);
        }
    }
}
