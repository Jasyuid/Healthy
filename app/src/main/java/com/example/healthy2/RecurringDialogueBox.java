package com.example.healthy2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

public class RecurringDialogueBox extends Activity {

    Activity mainActivity;
    NumberPicker numberPickerMin;
    NumberPicker numberPickerHr;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recurring_dialogue);
        mainActivity = MainActivity.activity;
        numberPickerMin = findViewById(R.id.increment);
        numberPickerHr = findViewById(R.id.incrementhr);
        Log.d("numberPicker: ", numberPickerHr.toString());
        numberPickerMin.setMaxValue(59);
        numberPickerHr.setMinValue(0);
        numberPickerHr.setMaxValue(99);
        numberPickerMin.setMinValue(0);
        numberPickerMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d("i", Integer.toString(i));
                Log.d("i1", Integer.toString(i1));
                if(i1==1){
                    numberPickerHr.setValue(numberPickerHr.getValue()+1);
                }
            }
        });

        mPrefs = mainActivity.getSharedPreferences("alarmList",0);
        mEditor = mPrefs.edit();
    }

    public void onClick(View v){
        switch(v.getId()){

            case R.id.thirty:
                if(numberPickerMin.getValue()+30 > 60){
                    numberPickerHr.setValue(numberPickerHr.getValue()+1);
                }
                numberPickerMin.setValue(numberPickerMin.getValue()+30);
                break;
            case R.id.hr:
                numberPickerHr.setValue(numberPickerHr.getValue()+1);
                break;
            case R.id.hr24:
                numberPickerHr.setValue(numberPickerHr.getValue()+24);
                break;
            case R.id.ok2:
                long hr = numberPickerHr.getValue()*60*60*1000;
                long min = numberPickerMin.getValue()*60*1000;

                if(hr == 0 && min == 0) {
                    break;
                }

                setAlarmText(numberPickerHr.getValue(), numberPickerMin.getValue());
                createAlarm(min+hr);

                StringBuilder sb = new StringBuilder();
                sb.append("Recurring alarm every: ");
                sb.append(numberPickerHr.getValue());
                sb.append(" hour(s) and ");
                sb.append(numberPickerMin.getValue());
                sb.append(" minute(s)");
                Set<String> fetch = mPrefs.getStringSet("recurring", null);
                Set<String> fetchCopy = new HashSet<>(fetch);
                fetchCopy.add(sb.toString());
                mEditor.putStringSet("recurring", fetchCopy).commit();
                Intent intent = new Intent();
                intent.putExtra("hour", numberPickerHr.getValue());
                intent.putExtra("min", numberPickerMin.getValue());
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.cancel2:
                finish();
                break;
        }
    }

    private void setAlarmText(int hour, int min){
        Toast.makeText(this, "Alarm set for " + hour + " hour(s) and " + min +" minute(s) from now",Toast.LENGTH_LONG).show();
    }

    public void createAlarm(long interval) {
        Log.d("TEST", "Gone in here");

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(mainActivity, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getActivity(mainActivity, getIntent().getIntExtra("count", 0), intent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+interval, interval, alarmIntent);

    }
}
