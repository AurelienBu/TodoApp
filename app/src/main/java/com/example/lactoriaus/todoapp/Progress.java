package com.example.lactoriaus.todoapp;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class Progress extends AppCompatActivity {
    public static ProgressBar xp;
    private int exp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        Intent intent = getIntent();

        exp = intent.getIntExtra("Prog", -1);

        xp = (ProgressBar)findViewById(R.id.xp);
        xp.incrementProgressBy(exp);

    }
    public void increaseProgress(){


        xp.setProgress(exp);
    }
    public void decreaseProgress(){
        exp-=5;

        xp.setProgress(exp);
    }

}
