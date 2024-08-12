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

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // Define variables
    ListView listView;
    ArrayList<String> items;
    CustomAdapter adapter;
    EditText addItemEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);
// Reference the "listView" variable to the id "lstView" in the layout
        listView = findViewById(R.id.lstView);

// Create an adapter for the list view using Android's built-in item layout
        ArrayList<DataModel> dataModels = new ArrayList<>();
        dataModels.add(new DataModel("item one", false));
        dataModels.add(new DataModel("item two", false));
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
                    int position = result.getData().getIntExtra("position", -1);
                    items.set(position, editedItem);
                    Log.i("Updated item in list ", editedItem + ", position: " + position);
// Make a standard toast that just contains text
                    Toast.makeText(getApplicationContext(), "Updated: " + editedItem,
                            Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            }
    );

    private void setupListViewListener() {
        listView.setOnItemLongClickListener((parent, view, position, rowId) -> {
            Log.i("MainActivity", "Long Clicked item " + position);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.dialog_delete_title)
                    .setMessage(R.string.dialog_delete_msg)
                    .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                        items.remove(position); // Remove item from the ArrayList
                        adapter.notifyDataSetChanged(); // Notify listView adapter to update the list
                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                        // User cancelled the dialog
                        // Nothing happens
                    });
            builder.create().show();
            return true;
        });
        // Set a click listener on the checkbox within each list item
        adapter.setOnCheckboxClickListener(position -> {
            DataModel dataModel = adapter.getItem(position);
            assert dataModel != null;
            dataModel.setChecked(!dataModel.isChecked());
            adapter.notifyDataSetChanged();});
    }

    public void onAddItemClick(View view) {
        String toAddString = addItemEditText.getText().toString();
        if (!toAddString.isEmpty()) {
//            adapter.add(toAddString); // Add text to list view adapter
            adapter.add(new DataModel(toAddString, false));
            addItemEditText.setText("");
        }
    }
}

