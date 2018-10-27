package com.example.lactoriaus.todoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import java.util.Calendar;


public class TaskSetting  extends AppCompatActivity {
    public static String notificationMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.task_setting);
        Button btn_OK = findViewById(R.id.btn_OK);
        final TimePicker timePicker = findViewById(R.id.timePicker);
        Intent intent = getIntent();
        notificationMsg = intent.getStringExtra(MainActivity.ITEM);


        btn_OK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                startAlarm("Task to do: " +
                                notificationMsg, false,
                        timePicker.getHour(),
                        timePicker.getMinute());
                returnMain();
            }


        });



    }
    private void returnMain(){
        Intent returnToMain = new Intent(this, MainActivity.class);
        startActivity(returnToMain);
        finish();

    }




    private void startAlarm(String msg, boolean isRepeat,int hour, int minute ) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);


       // myIntent = new Intent(MainActivity.this,AlarmNotificationReceiver.class);
        myIntent = new Intent(TaskSetting.this,AlarmNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);
        myIntent.putExtra(msg, msg);



        if(!isRepeat)
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        else
            manager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,pendingIntent);
    }

}
