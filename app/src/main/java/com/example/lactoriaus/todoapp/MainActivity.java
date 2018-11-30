package com.example.lactoriaus.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public  static ArrayList<String> items  = new ArrayList<String>();
    public  static ArrayAdapter<String> itemsAdapter ;
    public  static ListView lvItems;
    public Progress progr = new Progress();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        // Restore the saved list
        readItems();

        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);

        setupListViewListener();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /** Called when the button "+" is pressed **/
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

    /**
     * Creation of the Menu option
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Method called when the menu button is pressed
     * @param item Name of the sub-menu wanted
     * @return
     */
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
        if (id == R.id.action_progress){
            launchProgress();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to iteract with the List View
     */
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    /* Called When long press on a item */
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, final int pos, long id) {

                        AlertDialog dialogDelete = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("What do you want to do? " + lvItems.getItemAtPosition(pos).toString() + "?")
                                .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        items.remove(pos);
                                        // Increase progress
                                        progr.increaseProgress();
                                        // Refresh the adapter
                                        itemsAdapter.notifyDataSetChanged();
                                        // Return true consumes the long click event (marks it handled)
                                    }
                                })
                                .setNegativeButton("Drop it!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Remove the item within array at position
                                        items.remove(pos);
                                        //Decrease progress
                                        progr.decreaseProgress();
                                        // Refresh the adapter
                                        itemsAdapter.notifyDataSetChanged();
                                    }
                                })
                                .create();
                        dialogDelete.show();
                        return true;
                    }
                });
        /** Called when a item is pressed **/
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                        // Open settings layout
                        launchTaskSettings(pos);
                        writeItems();
                    }
                }
        );
    }
    /**
     * Method to read the task list in a text file
     */
    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "TodoListSave.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    /**
     * Method to write the task list in a text file
     */
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

    /**
     * Start the task setting activity
     * @param pos position of the selectem task
     */
    private void launchTaskSettings(int pos){
        Intent intent2 = new Intent(this, TaskSetting.class);
        Intent intent = getIntent();
        // Gives data to the other class
        intent2.putExtra("ITEM", items.get(pos));
        intent2.putExtra("POS",pos);
        startActivityForResult(intent2,1);

    }

    /**
     * Start the progress layout
     */
    private void launchProgress(){
        Intent intent2 = new Intent(this, Progress.class);
        startActivityForResult(intent2,1);

    }

    /**
     * Method called when get back from the setting task
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        int pos = 0;
        int priority = 1;
        int priorityColor = 0;

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

