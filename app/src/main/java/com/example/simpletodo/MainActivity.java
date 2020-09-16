package com.example.simpletodo;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final  String KEY_ITEM_TEXT = "item_text";
    public static final  String KEY_ITEM_POSITION = "item_position";
    public static final  int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button buttonADD;
    EditText EDTItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonADD = findViewById(R.id.buttonADD);
        EDTItem = findViewById(R.id.EDTItem);
        rvItems = findViewById(R.id.rvItems);


        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was Removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int postion) {
                Log.d("MainActivity", "Single click at position" + postion);
                //Create the new activity
                Intent i = new Intent (MainActivity.this, EditActivity.class);
                //pass the data being  edited
                i.putExtra(KEY_ITEM_TEXT, items.get(postion));
                i.putExtra(KEY_ITEM_POSITION, postion);
                //display activity
                startActivityForResult(i, EDIT_TEXT_CODE);

            }
        };
        itemsAdapter = new ItemsAdapter(items,onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        buttonADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = EDTItem.getText().toString();
                //Add item to the model
                items.add(todoItem);
                // Notify adapter that an item is intrested
                itemsAdapter.notifyItemInserted(items.size()-1);
                EDTItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();


            }
        });
    }

    //handle result of edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         if (resultCode ==RESULT_OK && requestCode == EDIT_TEXT_CODE){
             //Retreive updated text value
             String itemText = data.getStringExtra(KEY_ITEM_TEXT);
             //extract the original position of the edited item from the position key
             int position = data.getExtras().getInt(KEY_ITEM_POSITION);
             //UPDATE model at right position with the new item text
             items.set(position,itemText);
             //notify the adapter
             itemsAdapter.notifyItemChanged(position);
             //presist the changes
             saveItems();
             Toast.makeText(getApplicationContext(), "Item was updated Successfully!", Toast.LENGTH_SHORT).show();
         } else {
             Log.w("MainActivity", "Unknown call to onActivityResult");
         }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    //function will load item by reading every line of data file
    private  void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }
    // functions saves items by writing them into the data file
    private  void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
            items = new ArrayList<>();
        }
    }
}