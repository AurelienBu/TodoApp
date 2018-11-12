package com.example.lactoriaus.todoapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;


public class TaskSetting  extends AppCompatActivity {
    public static String notificationMsg;
    private int pos;

    private Calendar mCalendar = Calendar.getInstance();
    private Activity mActivity;
    private int notifHour;
    private int notifMinute;
    private String ampm="AM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.task_setting);
        mActivity = this;
        Button btn_OK = findViewById(R.id.btn_OK);
        final CheckBox chb_rep = findViewById(R.id.btn_rep);
        final CheckBox btn_sun = findViewById(R.id.btn_sunday);
        final CheckBox btn_mon = findViewById(R.id.btn_Monday);
        final CheckBox btn_tue = findViewById(R.id.btn_tuesday);
        final CheckBox btn_wed = findViewById(R.id.btn_wes);
        final CheckBox btn_thur = findViewById(R.id.btn_thursday);
        final CheckBox btn_fri = findViewById(R.id.btn_friday);
        final CheckBox btn_sat = findViewById(R.id.btn_satur);
        final Switch sw_notif = findViewById(R.id.sw_notif);

        Intent intent = getIntent();
        notificationMsg = intent.getStringExtra("ITEM");
        pos = intent.getIntExtra("POS", -1);

        final TextView chooseTime = findViewById(R.id.timeTV);
        final EditText et_taskname = findViewById(R.id.et_taskname);
        et_taskname.setText(MainActivity.lvItems.getItemAtPosition(pos).toString());

        chb_rep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (chb_rep.isChecked())
                {

                    btn_sun.setVisibility(View.VISIBLE);
                    btn_mon.setVisibility(View.VISIBLE);
                    btn_tue.setVisibility(View.VISIBLE);
                    btn_wed.setVisibility(View.VISIBLE);
                    btn_thur.setVisibility(View.VISIBLE);
                    btn_fri.setVisibility(View.VISIBLE);
                    btn_sat.setVisibility(View.VISIBLE);

                }
                else
                {
                    btn_sun.setChecked(false);
                    btn_mon.setChecked(false);
                    btn_tue.setChecked(false);
                    btn_wed.setChecked(false);
                    btn_thur.setChecked(false);
                    btn_fri.setChecked(false);
                    btn_sat.setChecked(false);
                    btn_sun.setVisibility(View.INVISIBLE);
                    btn_mon.setVisibility(View.INVISIBLE);
                    btn_tue.setVisibility(View.INVISIBLE);
                    btn_wed.setVisibility(View.INVISIBLE);
                    btn_thur.setVisibility(View.INVISIBLE);
                    btn_fri.setVisibility(View.INVISIBLE);
                    btn_sat.setVisibility(View.INVISIBLE);
                }
            }
        });


        btn_OK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(sw_notif.isChecked()){
                    startAlarm("Task to do: " +
                                notificationMsg, false,
                                notifHour,
                                notifMinute, 0);

                    // Create notification for each days
                    if(chb_rep.isChecked()) {
                        if (btn_mon.isChecked()){
                            startAlarm("Task to do: " + notificationMsg,
                                    true,
                                    notifHour,
                                    notifMinute, Calendar.MONDAY);
                        }
                        if (btn_tue.isChecked()){
                            startAlarm("Task to do: " + notificationMsg,
                                    true,
                                    notifHour,
                                    notifMinute, Calendar.TUESDAY);
                        }
                        if (btn_wed.isChecked()){
                            startAlarm("Task to do: " + notificationMsg,
                                    true,
                                    notifHour,
                                    notifMinute, Calendar.WEDNESDAY);
                        }
                        if (btn_thur.isChecked()){
                            startAlarm("Task to do: " + notificationMsg,
                                    true,
                                    notifHour,
                                    notifMinute, Calendar.THURSDAY);
                        }
                        if (btn_fri.isChecked()){
                            startAlarm("Task to do: " + notificationMsg,
                                    true,
                                    notifHour,
                                    notifMinute, Calendar.FRIDAY);
                        }
                        if (btn_sat.isChecked()){
                            startAlarm("Task to do: " + notificationMsg,
                                    true,
                                    notifHour,
                                    notifMinute, Calendar.SATURDAY);
                        }
                        if (btn_sun.isChecked()){
                            startAlarm("Task to do: " + notificationMsg,
                                    true,
                                    notifHour,
                                    notifMinute, Calendar.SUNDAY);
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("taskname", et_taskname.getText().toString());
                intent.putExtra("pos", pos);

                setResult(RESULT_OK, intent);
                finish(); //returnMain();
            }
        });


        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendar = Calendar.getInstance();


                TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        int hourOfDayToDisplay=hourOfDay;
                        notifHour = hourOfDay;
                        notifMinute = minutes;
                        if(hourOfDay > 11){
                            ampm = "PM";
                            if(hourOfDay >= 13)
                                hourOfDayToDisplay = notifHour - 12;
                        }
                        else ampm = "AM";
                        chooseTime.setText(String.format("Alert time %02d:%02d %S", hourOfDayToDisplay, minutes,ampm));
                    }
                },  mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        });
    }

    private void startAlarm(String msg, boolean isRepeat,int hour, int minute, int dayOfWeek ) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        if(isRepeat)
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);



       // myIntent = new Intent(MainActivity.this,AlarmNotificationReceiver.class);
        myIntent = new Intent(TaskSetting.this,AlarmNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);
        myIntent.putExtra(msg, msg);



        if(!isRepeat)
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        else
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }




}
