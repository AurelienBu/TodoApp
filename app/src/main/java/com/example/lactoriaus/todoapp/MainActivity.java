package com.example.lactoriaus.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import org.apache.commons.io.FileUtils;
import android.widget.TextView;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public  static ArrayList<String> items  = new ArrayList<String>();
    public  static ArrayAdapter<String> itemsAdapter ;
    public  static ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ADD HERE
        lvItems = (ListView) findViewById(R.id.lvItems);
       // items = ;
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
                        .setMessage("What task do you want to do?")
                        .setView(textEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(textEditText.getText());
                                itemsAdapter.add(task);
                                writeItems();
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
                                .setTitle("What do you want to do? " + lvItems.getItemAtPosition(pos).toString() + "?")

                                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        launchTaskSettings(pos);
                                    }
                                })
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
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
                                .create();
                        dialogDelete.show();

                        return true;
                    }
                });
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                        launchTaskSettings(pos);
                        writeItems();
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

    private void launchTaskSettings(int pos){
        Intent intent2 = new Intent(this, TaskSetting.class);
        Intent intent = getIntent();
        intent2.putExtra("ITEM", items.get(pos));
        intent2.putExtra("POS",pos);
        startActivityForResult(intent2,1);

    }
    /*--------------------------------------------------
    Method called when get back from the setting task
    --------------------------------------------------*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        int pos = 0;
        int priority = 1;
        int priorityColor = 0;
       // SpannableStringBuilder builder =  new SpannableStringBuilder();


        if(requestCode == 1 && resultCode == RESULT_OK)
        {

            String taskname = intent.getStringExtra("taskname");
            pos = intent.getIntExtra("pos", -1);
            priority = intent.getIntExtra("Priority", 1);

            //HIGH PRIORITY
            if(priority == 1)
            {
                items.remove(pos);
                items.add(pos, taskname);
                priorityColor = Color.RED;
                itemsAdapter.notifyDataSetChanged();

            }
            //MEDIUM PRIORITY
            else if( priority == 2)
            {
                items.remove(pos);
                items.add(pos, taskname);
                priorityColor = Color.rgb(255,165,0);
                itemsAdapter.notifyDataSetChanged();
            }

            //LOW PRIORITY
            else if( priority == 3)
            {
                items.remove(pos);
                items.add(pos, taskname);
                priorityColor = Color.YELLOW;
                itemsAdapter.notifyDataSetChanged();
            }
            lvItems.getChildAt(pos).setBackgroundColor(priorityColor);

        }
    }

}

