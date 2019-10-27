package com.example.healthy2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class ActivActivity extends AppCompatActivity {

    private int calibration = 0;
    private TextView calibration_text = null;

    private boolean test_run = false;
    private int startTime = 10;
    private int activeStartTime = 60;

    private int target = 0;
    private ArrayList<Boolean> zoneTracker = new ArrayList<Boolean>();

    private TextView current_text = null;
    private TextView average_text = null;
    private TextView time_text = null;

    private TextView current_force_text = null;
    private TextView target_force_text = null;
    private TextView zone_text = null;
    private TextView timer_text = null;
    private TextView accuracy_text = null;

    private Button calibration_button = null;
    private Button active_button = null;
    private Button exit_button = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activ);

        calibration_text = findViewById(R.id.calibration);
        calibration_text.setText("Not Calibrated");

        current_force_text = findViewById(R.id.current_force);
        target_force_text = findViewById(R.id.target_force);
        zone_text = findViewById(R.id.in_zone);
        timer_text = findViewById(R.id.timer);

        setCurrentForce(0);
        setTargetForce(0);
        setZone(false);
        setTimer(0);

        accuracy_text = findViewById(R.id.accuracy);
        accuracy_text.setVisibility(View.INVISIBLE);

        calibration_button = findViewById(R.id.calibrate_button);
        calibration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrationPopup(v);
            }
        });

        final Toast no_calibration = Toast.makeText(this, "Device not calibrated!", Toast.LENGTH_SHORT);
        active_button = findViewById(R.id.start_button);
        active_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calibration != 0) {
                    active_button.setEnabled(false);
                    activity();
                } else {
                    no_calibration.show();
                }
            }
        });

        exit_button = findViewById(R.id.exit_button);
        exit_button.setEnabled(false);
        exit_button.setVisibility(View.INVISIBLE);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

    private void calibrationPopup(View view) {

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.calibration_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, false);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        average_text = popupWindow.getContentView().findViewById(R.id.average_force);
        time_text = popupWindow.getContentView().findViewById(R.id.time_remaining);
        average_text.setText("Current Force: 0");
        time_text.setText("Seconds remaining: " + startTime);

        calibration_button.setEnabled(false);
        active_button.setEnabled(false);

        test_run = false;
        final Button toggle_button = popupWindow.getContentView().findViewById(R.id.toggle_button);
        toggle_button.setText("Start");
        toggle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(test_run){
                    calibration_button.setEnabled(true);
                    active_button.setEnabled(true);
                    popupWindow.dismiss();

                } else {
                    Healthy.device.startIsometric();
                    toggle_button.setEnabled(false);

                    new CountDownTimer(startTime*1000, 10) {

                        public void onTick(long millisUntilFinished) {
                            time_text.setText("Seconds remaining: " + millisUntilFinished / 1000);
                            if(!Healthy.data.isEmpty()) {
                                average_text.setText("Current Force: " + Healthy.data.get(Healthy.data.size()-1));
                            }
                        }

                        public void onFinish() {
                            time_text.setText("Done");
                            toggle_button.setEnabled(true);
                            toggle_button.setText("Finish");
                            test_run = true;
                            for(int i = 0; i < Healthy.data.size(); i++) {
                                calibration += Healthy.data.get(i);
                            }
                            calibration /= Healthy.data.size();
                            Healthy.device.stop();
                            Healthy.data.clear();
                            average_text.setText("Average Force: " + calibration);
                            if(calibration != 0) {
                                calibration_text.setText("Calibrated Force: " + calibration);
                            }
                        }

                    }.start();
                }
            }
        });
    }

    private void activity(){

        Healthy.device.startIsometric();
        target = generateZone();
        setTargetForce(target);
        current_force_text.setText("Target Force: " + target);
        accuracy_text.setVisibility(View.INVISIBLE);
        calibration_button.setEnabled(false);

        new CountDownTimer(activeStartTime*1000, 5) {

            boolean changed = true;

            public void onTick(long millisUntilFinished) {
                setTimer((int) millisUntilFinished / 1000);
                if((millisUntilFinished/1000) % 10 == 0) {
                    if(!changed) {
                        target = generateZone();
                        setTargetForce(target);
                    }
                    changed = true;
                }else {
                    changed = false;
                }
                if(!Healthy.data.isEmpty()) {
                    int current_force = Healthy.data.get(Healthy.data.size()-1);
                    setCurrentForce(current_force);
                    current_force_text.setText("Current Force: " + current_force);
                    if( current_force > target - 10 && current_force < target + 10){
                        setZone(true);
                        zoneTracker.add(true);
                    }else{
                        setZone(false);
                        zoneTracker.add(false);
                    }
                }
            }

            public void onFinish() {
                Healthy.device.stop();
                Healthy.data.clear();
                int accuracy = 0;
                for( int i = 0; i < zoneTracker.size(); i++){
                    if(zoneTracker.get(i)){
                        accuracy++;
                    }
                }
                accuracy = (accuracy*100)/zoneTracker.size();
                zoneTracker.clear();
                accuracy_text.setVisibility(View.VISIBLE);
                if(accuracy < 70) {
                    accuracy_text.setText("Accuracy: " + accuracy + "% | Try Again");
                    accuracy_text.setTextColor(0xFFAA0000);
                } else {
                    accuracy_text.setText("Accuracy: " + accuracy + "% | Good Job");
                    accuracy_text.setTextColor(0xFF00AA00);
                    exit_button.setEnabled(true);
                    exit_button.setVisibility(View.VISIBLE);
                }

                active_button.setEnabled(true);
                calibration_button.setEnabled(true);
            }

        }.start();

    }

    private int generateZone() {
        Random r = new Random();
        return r.nextInt(calibration/2) + 3*calibration/4;
    }

    private void setCurrentForce(int i){
        current_force_text.setText("Current Force: " + i);
    }

    private void setTargetForce(int i){
        target_force_text.setText("Target Force: " + i);
    }

    private void setZone(boolean b){
        if(b) {
            zone_text.setText("Your in the zone!");
            zone_text.setTextColor(0xff00aa00);
        } else {
            zone_text.setText("Your not in the zone!");
            zone_text.setTextColor(0xffaa0000);
        }
    }

    private void setTimer(int i){
        timer_text.setText("Time Remaining: " + i);
    }
}
