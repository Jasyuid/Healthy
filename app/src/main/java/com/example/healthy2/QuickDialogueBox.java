package com.example.healthy2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class QuickDialogueBox extends Activity {
    Activity mainActivity;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_dialogue);
        Log.d("TEST", "DIALOGUE");
        mainActivity = MainActivity.activity;
        mPrefs = mainActivity.getSharedPreferences("alarmList",0);
        mEditor = mPrefs.edit();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                Calendar calendar = Calendar.getInstance();
                TimePicker clock = findViewById(R.id.clock);
                calendar.set(Calendar.HOUR_OF_DAY, clock.getHour());
                calendar.set(Calendar.MINUTE, clock.getMinute());
                setAlarmText(clock.getHour(), clock.getMinute());
                createAlarm(calendar.getTimeInMillis());

                StringBuilder sb = new StringBuilder();
                sb.append("One Time Alarm at: ");
                if(clock.getHour() == 0){
                    sb.append(12);
                    sb.append(":");
                    if(clock.getMinute() >= 10)
                        sb.append(clock.getMinute());
                    else{
                        sb.append("0");
                        sb.append(clock.getMinute());
                    }
                    sb.append("AM");
                }
                else if(clock.getHour() < 12) {
                    sb.append(clock.getHour());
                    sb.append(":");
                    if(clock.getMinute() >= 10)
                        sb.append(clock.getMinute());
                    else{
                        sb.append("0");
                        sb.append(clock.getMinute());
                    }
                    sb.append("AM");
                }
                else if(clock.getHour() == 12) {
                    sb.append(clock.getHour());
                    sb.append(":");
                    if(clock.getMinute() >= 10)
                        sb.append(clock.getMinute());
                    else{
                        sb.append("0");
                        sb.append(clock.getMinute());
                    }
                    sb.append("PM");
                }
                else{
                    sb.append(clock.getHour()%12);
                    sb.append(":");
                    if(clock.getMinute() >= 10)
                        sb.append(clock.getMinute());
                    else{
                        sb.append("0");
                        sb.append(clock.getMinute());
                    }
                    sb.append("PM");
                }

                Set<String> fetch = mPrefs.getStringSet("quick", null);
                Set<String> fetchCopy = new HashSet<>(fetch);
                fetchCopy.add(sb.toString());
                Log.d("added", sb.toString());
                mEditor.putStringSet("quick", fetchCopy).commit();
                Log.d("added", Integer.toString(mPrefs.getStringSet("quick", null).size()));

                Intent intent = new Intent();
                intent.putExtra("hour", clock.getHour());
                intent.putExtra("min", clock.getMinute());
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;

            case R.id.cancel:
                finish();
                break;
        }
    }

    private void setAlarmText(int hour, int min){
        StringBuilder sb = new StringBuilder();
        sb.append("Alarm set for");
        if(hour == 0){
            sb.append(12);
            sb.append(":");
            if(min >= 10)
                sb.append(min);
            else{
                sb.append("0");
                sb.append(min);
            }
            sb.append("AM");
        }
        else if(hour < 12) {
            sb.append(hour);
            sb.append(":");
            if(min >= 10)
                sb.append(min);
            else{
                sb.append("0");
                sb.append(min);
            }
            sb.append("AM");
        }
        else if(hour == 12) {
            sb.append(hour);
            sb.append(":");
            if(min >= 10)
                sb.append(min);
            else{
                sb.append("0");
                sb.append(min);
            }
            sb.append("PM");
        }
        else{
            sb.append(hour%12);
            sb.append(":");
            if(min >= 10)
                sb.append(min);
            else{
                sb.append("0");
                sb.append(min);
            }
            sb.append("PM");
        }
        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
    }

    public void createAlarm(long ms) {
        Log.d("TEST", "Gone in here");
        Intent myIntent = new Intent(mainActivity, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, getIntent().getIntExtra("count", 0), myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, ms, pendingIntent);
    }
}
