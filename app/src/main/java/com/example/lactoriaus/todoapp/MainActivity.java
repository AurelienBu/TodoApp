package com.example.lactoriaus.todoapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    public static String notificationMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ADD HERE
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText textEditText = new EditText(MainActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add New Task")
                        .setMessage("What task you want to do?")
                        .setView(textEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(textEditText.getText());
                                itemsAdapter.add(task);
                                writeItems();

                                //createNotification("Task to do: " +
                                //         task, MainActivity.this);
                                startAlarm("Task to do: " +
                                                 task, false);

                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, final int pos, long id) {

                        AlertDialog dialogDelete = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Are you sure you want to delete this " + lvItems.getItemAtPosition(pos).toString() + "?")

                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Remove the item within array at position
                                        items.remove(pos);
                                        // Refresh the adapter
                                        itemsAdapter.notifyDataSetChanged();
                                        // Return true consumes the long click event (marks it handled)
                                        //writeItems();
                                    }
                                })
                                .setNegativeButton("Cancel",null)
                                .create();
                        dialogDelete.show();

                        return true;
                    }


                });
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                        final EditText textEditText = new EditText(MainActivity.this);
                        AlertDialog dialogModify = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Modification of: " + lvItems.getItemAtPosition(pos).toString() + "?")
                                .setView(textEditText)
                                .setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String task = String.valueOf(textEditText.getText());
                                        // Remove the item within array at position

                                        items.remove(pos);
                                        items.add(pos, task);
                                        // Refresh the adapter
                                        itemsAdapter.notifyDataSetChanged();
                                        // Return true consumes the long click event (marks it handled)
                                        //writeItems();
                                    }
                                })
                                .setNegativeButton("Cancel",null)
                                .create();
                        dialogModify.show();
                    }
                }
        );
    }
    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "TodoListSave.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }

    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "TodoListSave.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        writeItems();
        getDelegate().onDestroy();

    }

    private void startAlarm(String msg, boolean isRepeat) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,11);
        calendar.set(Calendar.MINUTE,38);
        calendar.set(Calendar.SECOND,0);


        notificationMsg = msg;


        myIntent = new Intent(MainActivity.this,AlarmNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);


        if(!isRepeat)
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        else
            manager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                                 AlarmManager.INTERVAL_DAY,pendingIntent);
    }




}
