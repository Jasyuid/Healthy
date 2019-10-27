package com.example.healthy2;

import android.app.Application;

import java.util.ArrayList;

import a5.com.a5bluetoothlibrary.A5Device;
import a5.com.a5bluetoothlibrary.A5DeviceManager;

public class Healthy extends Application {

    private static Healthy instance;

    public static A5Device device = null;
    public static boolean connected = false;

    public static int calibration = 0;

    public static ArrayList<Integer> data;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        A5DeviceManager.INSTANCE.initializeDeviceManager(this );

        data = new ArrayList<Integer>();

    }

    public static Healthy getInstance() {
        return instance;
    }

}
