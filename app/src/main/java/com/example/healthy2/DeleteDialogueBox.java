package com.example.healthy2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class DeleteDialogueBox  extends Activity {

    Activity mainActivity;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_dialogue);
        mainActivity = MainActivity.activity;
        mPrefs = mainActivity.getSharedPreferences("alarmList",0);
        mEditor = mPrefs.edit();
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.cancel3:
                finish();
                break;
            case R.id.delete:
                Intent myIntent = new Intent(mainActivity, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, getIntent().getIntExtra("count", 0), myIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                Intent intent = new Intent();
                int code = getIntent().getIntExtra("code", 0);
                intent.putExtra("count", getIntent().getIntExtra("count", 0));
                Log.d("count", Integer.toString(code));
                intent.putExtra("code", code);
                setResult(Activity.RESULT_OK, intent);
                finish();
        }
    }
}
