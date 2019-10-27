package com.example.healthy2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static Activity activity;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    int hour = -1;
    int min = -1;
    int count = 0;
    ArrayList<Button> buttonArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        mPrefs = this.getSharedPreferences("alarmList",0);
        mEditor = mPrefs.edit();

        boolean firstRun = mPrefs.getBoolean("firstRun", true);
        if(firstRun){
            mEditor.putStringSet("quick", new HashSet<String>()).commit();
            mEditor.putStringSet("recurring", new HashSet<String>()).commit();
            mEditor.putBoolean("firstRun", false).commit();
        }

        Set<String> quick = mPrefs.getStringSet("quick", null);
        Set<String> recurring = mPrefs.getStringSet("recurring", null);

        Log.d("added", Integer.toString(quick.size()));
        Log.d("added", Integer.toString(recurring.size()));

        ArrayList<String> quickList = new ArrayList<String>(quick);
        ArrayList<String> recurringList = new ArrayList<String>(recurring);

        for(int i = 0; i < quickList.size(); ++i){
            final Button quickAlarm = new Button(this);
            quickAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DeleteDialogueBox.class);
                    intent.putExtra("count", buttonArrayList.indexOf(quickAlarm));
                    intent.putExtra("code", 0);
                    startActivityForResult(intent, 2);
                }
            });
            quickAlarm.setText(quickList.get(i));
            LinearLayout alarmList = findViewById(R.id.alarmList);
            buttonArrayList.add(quickAlarm);
            alarmList.addView(quickAlarm);
            count++;
        }
        for(int i = 0; i < recurringList.size(); ++i){
            final Button recurringAlarm = new Button(this);
            recurringAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DeleteDialogueBox.class);
                    intent.putExtra("count", buttonArrayList.indexOf(recurringAlarm));
                    intent.putExtra("code", 1);
                    startActivityForResult(intent, 2);
                }
            });
            recurringAlarm.setText(recurringList.get(i));
            LinearLayout alarmList = findViewById(R.id.alarmList);
            buttonArrayList.add(recurringAlarm);
            alarmList.addView(recurringAlarm);
            count++;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quickAlarm:
                Intent intent = new Intent(MainActivity.this, QuickDialogueBox.class);
                intent.putExtra("count", count);
                startActivityForResult(intent, 0);
                break;
            case R.id.recurringAlarm:
                Log.d("recurring", "Should be here");
                Intent intent2 = new Intent(MainActivity.this, RecurringDialogueBox.class);
                intent2.putExtra("count", count);
                startActivityForResult(intent2, 1);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0):
                if (resultCode == Activity.RESULT_OK) {
                    hour = data.getIntExtra("hour", -1);
                    min = data.getIntExtra("min", -1);
                    final Button quickAlarm = new Button(this);
                    quickAlarm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, DeleteDialogueBox.class);
                            intent.putExtra("count", buttonArrayList.indexOf(quickAlarm));
                            intent.putExtra("code", 0);
                            startActivityForResult(intent, 2);
                        }
                    });
                    if(hour == 0){
                        if(min < 10){
                            quickAlarm.setText("One Time Alarm at: " + 12 + ":" + "0" + min +"AM");
                        }
                        else {
                            quickAlarm.setText("One Time Alarm at: " + 12 + ":" + min + "AM");
                        }
                    }
                    else if(hour < 12){
                        if(min < 10){
                            quickAlarm.setText("One Time Alarm at: " + hour + ":" + "0" + min +"AM");
                        }
                        else {
                            quickAlarm.setText("One Time Alarm at: " + hour + ":" + min + "AM");
                        }
                    }
                    else if(hour == 12){
                        if(min < 10){
                            quickAlarm.setText("One Time Alarm at: " + hour + ":" + "0" + min +"PM");
                        }
                        else {
                            quickAlarm.setText("One Time Alarm at: " + hour + ":" + min + "PM");
                        }
                    }
                    else{
                        if(min < 10){
                            quickAlarm.setText("One Time Alarm at: " + hour%12 + ":" + "0" + min +"PM");
                        }
                        else {
                            quickAlarm.setText("One Time Alarm at: " + hour%12 + ":" + min + "PM");
                        }
                    }
                    LinearLayout alarmList = findViewById(R.id.alarmList);
                    buttonArrayList.add(quickAlarm);
                    alarmList.addView(quickAlarm);
                    count++;
                }
                else{
                    hour = -1;
                    min = -1;
                }
                break;
            case(1):
                if (resultCode == Activity.RESULT_OK) {
                    hour = data.getIntExtra("hour", -1);
                    min = data.getIntExtra("min", -1);
                    final Button recurringAlarm = new Button(this);
                    recurringAlarm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, DeleteDialogueBox.class);
                            intent.putExtra("count", buttonArrayList.indexOf(recurringAlarm));
                            intent.putExtra("code", 1);
                            startActivityForResult(intent,2);
                        }
                    });
                    recurringAlarm.setText("Recurring alarm every: " + hour + " hour(s) and " + min + " minute(s)");
                    LinearLayout alarmList = findViewById(R.id.alarmList);
                    buttonArrayList.add(recurringAlarm);
                    alarmList.addView(recurringAlarm);
                    count++;
                }
                else {
                    hour = -1;
                    min = -1;
                }
                break;

            case(2):
                if (resultCode == Activity.RESULT_OK) {
                    int count = data.getIntExtra("count",-1);
                    Log.d("count", Integer.toString(count));
                    ((ViewManager)buttonArrayList.get(count).getParent()).removeView(buttonArrayList.get(count));
                    int code = data.getIntExtra("code", 0);
                    Log.d("code", Integer.toString(code));
                    Set<String> fetch;
                    if(code == 0) {
                        fetch = mPrefs.getStringSet("quick", null);
                        Set<String> fetchCopy = new HashSet<>(fetch);
                        fetchCopy.remove(buttonArrayList.get(count).getText());
                        mEditor.putStringSet("quick", fetchCopy).commit();
                    }
                    else{
                        fetch = mPrefs.getStringSet("recurring", null);
                        Set<String> fetchCopy = new HashSet<String>(fetch);
                        fetchCopy.remove(buttonArrayList.get(count).getText());
                        mEditor.putStringSet("recurring", fetchCopy).commit();
                    }
                    buttonArrayList.set(count, null);
                }
        }
    }
}
