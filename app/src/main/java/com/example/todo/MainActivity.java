package com.example.todo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // Define variables
    ListView listView;
    ArrayList<String> items;
    CustomAdapter adapter;
    EditText addItemEditText;
    ArrayList<DataModel> dataModels = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);
// Reference the "listView" variable to the id "lstView" in the layout
        listView = findViewById(R.id.lstView);

// Create an adapter for the list view using Android's built-in item layout

        dataModels.add(new DataModel("item one",  false, "Assignment", LocalDateTime.now()));
        dataModels.add(new DataModel("item two", false, "Exam", LocalDateTime.now()));
        adapter = new CustomAdapter(dataModels, this);

        listView.setAdapter(adapter);
        // Setup listView listeners
        setupListViewListener();
    }

    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Extract name value from result extras
                    assert result.getData() != null;
                    String editedItem = Objects.requireNonNull(result.getData().getExtras()).getString("item");
                    String type = Objects.requireNonNull(result.getData().getExtras()).getString("type");
                    LocalDateTime dateTime = (LocalDateTime) Objects.requireNonNull(result.getData().getExtras()).get("date");
                    boolean isNew = result.getData().getBooleanExtra("isNew", false);
                    if (!isNew) {
                        int position = result.getData().getIntExtra("position", -1);
                        dataModels.set(position, new DataModel(editedItem, false, type, dateTime));
                        Log.i("Updated item in list ", editedItem + ", position: " + position);
                        // Make a standard toast that just contains text
                        Toast.makeText(getApplicationContext(), "Updated: " + editedItem,
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        dataModels.add(new DataModel(editedItem, false, type, dateTime));
                        Log.i("Added item to list ", editedItem);
                        // Make a standard toast that just contains text
                        Toast.makeText(getApplicationContext(), "Added: " + editedItem,
                                Toast.LENGTH_SHORT).show();

                    }


                    adapter.notifyDataSetChanged();
                }
                else if (result.getResultCode() == RESULT_CANCELED) {
                    // User cancelled the activity
                }
            }
    );

    private void setupListViewListener() {
        adapter.setOnItemLongClickListener(position -> {
            Log.i("MainActivity", "Long Clicked item " + position);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.dialog_delete_title)
                    .setMessage(R.string.dialog_delete_msg)
                    .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                        dataModels.remove(position); // Remove item from the ArrayList
                        adapter.notifyDataSetChanged(); // Notify listView adapter to update the list
                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                        // User cancelled the dialog
                        // Nothing happens
                    });
            builder.create().show();
        });
        // Set a click listener on the checkbox within each list item
        adapter.setOnCheckboxClickListener(position -> {
            DataModel dataModel = adapter.getItem(position);
            assert dataModel != null;
            dataModel.setChecked(!dataModel.isChecked());
            adapter.notifyDataSetChanged();});

        // Set a click listener on the list item
        adapter.setOnItemClickListener(position -> {
            // Takes you to the edit item activity
            Intent intent = new Intent(this, EditToDoItemActivity.class);
            // Sets that the item is new and being added rather than edited
            intent.putExtra("isNew", false);
            intent.putExtra("item", dataModels.get(position).getName());
            intent.putExtra("position", position);
            intent.putExtra("type", dataModels.get(position).getType());
            intent.putExtra("date", dataModels.get(position).getDate());
            mLauncher.launch(intent);
        });
    }

    public void onAddItemClick(View view) {
        // Takes you to the edit item activity
        Intent intent = new Intent(this, EditToDoItemActivity.class);
        // Sets that the item is new and being added rather than edited
        intent.putExtra("isNew", true);
        mLauncher.launch(intent);
    }
}

