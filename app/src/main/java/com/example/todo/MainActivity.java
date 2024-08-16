package com.example.todo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {
    // Define variables
    ListView listView;
    CustomAdapter adapter;
    ArrayList<DataModel> dataModels = new ArrayList<>();
    ToDoItemDao toDoItemDao;
    ToDoItemDB db;
    public static final String EXTRA_IS_NEW = "isNew";

    /**
     * mLauncher is used to hand off an intent to another activity and
     * handle the result of the activity when it returns.
     */
    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Extract name value from result extras
                    assert result.getData() != null;
                    String editedItem = Objects.requireNonNull(result.getData().getExtras()).getString("item");
                    String type = Objects.requireNonNull(result.getData().getExtras()).getString("type");
                    LocalDateTime dateTime = (LocalDateTime) Objects.requireNonNull(result.getData().getExtras()).get("date");
                    boolean isNew = result.getData().getBooleanExtra(EXTRA_IS_NEW, false);
                    if (!isNew) {
                        int position = result.getData().getIntExtra("position", -1);
                        dataModels.set(position, new DataModel(editedItem, false, type, dateTime));
                        Log.i("Updated item in list ", editedItem + ", position: " + position);
                        // Make a standard toast that just contains text
                        Toast.makeText(getApplicationContext(), "Updated: " + editedItem,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        dataModels.add(0, new DataModel(editedItem, false, type, dateTime));
                        assert editedItem != null;
                        Log.i("Added item to list ", editedItem);
                        // Make a standard toast that just contains text
                        Toast.makeText(getApplicationContext(), "Added: " + editedItem,
                                Toast.LENGTH_SHORT).show();

                    }


                    adapter.notifyDataSetChanged();
                    saveItemsToDatabase();
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    // User cancelled the activity
                }
            }
    );

    /**
     * Called when the activity is first created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = ToDoItemDB.getDatabase(this.getApplication().getApplicationContext());
        toDoItemDao = db.toDoItemDao();
        readItemsFromDatabase();
// Use "activity_main.xml" as the layout
        setContentView(R.layout.activity_main);
// Reference the "listView" variable to the id "lstView" in the layout
        listView = findViewById(R.id.lstView);

// Create an adapter for the list view using Android's built-in item layout
        adapter = new CustomAdapter(dataModels, this);
        listView.setAdapter(adapter);
        // Setup listView listeners
        setupListViewListener();
    }

    /**
     * Set up the listeners for the list view.
     */
    private void setupListViewListener() {
        adapter.setOnItemLongClickListener(position -> {
            Log.i("MainActivity", "Long Clicked item " + position);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.dialog_delete_title)
                    .setMessage(R.string.dialog_delete_msg)
                    .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                        dataModels.remove(position); // Remove item from the ArrayList
                        adapter.notifyDataSetChanged(); // Notify listView adapter to update the list
                        saveItemsToDatabase();
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
            adapter.notifyDataSetChanged();
            saveItemsToDatabase();
        });

        // Set a click listener on the list item
        adapter.setOnItemClickListener(position -> {
            // Takes you to the edit item activity
            Intent intent = new Intent(this, EditToDoItemActivity.class);
            // Sets that the item is new and being added rather than edited
            intent.putExtra(EXTRA_IS_NEW, false);
            intent.putExtra("item", dataModels.get(position).getName());
            intent.putExtra("position", position);
            intent.putExtra("type", dataModels.get(position).getType());
            intent.putExtra("date", dataModels.get(position).getDate());
            mLauncher.launch(intent);
        });
    }

    /**
     * Called when the add button is clicked.
     * @param view
     */
    public void onAddItemClick(View view) {
        // Takes you to the edit item activity
        Intent intent = new Intent(this, EditToDoItemActivity.class);
        // Sets that the item is new and being added rather than edited
        intent.putExtra(EXTRA_IS_NEW, true);
        mLauncher.launch(intent);
    }


    //DB Methods

    /**
     * Reads the items from ToDoItemDB and stores them in dataModels.
     */
    private void readItemsFromDatabase() {
//Use asynchronous task to run query on the background and wait for result
        try {
// Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                //read items from database
                List<ToDoItem> itemsFromDB = toDoItemDao.listAllSorted();
                dataModels = new ArrayList<>();
                if (itemsFromDB != null && !itemsFromDB.isEmpty()) {
                    for (ToDoItem item : itemsFromDB) {
                        dataModels.add(new DataModel(
                                item.getToDoItemName(),
                                item.isChecked(),
                                item.getType(),
                                item.getDate()));
                    }
                }
            });
// Block and wait for the future to complete
            future.get();
        } catch (Exception ex) {
            Log.e("readItemsFromDatabase", ex.getStackTrace().toString());
        }
    }

    /**
     * Saves the items in dataModels to ToDoItemDB.
     */
    private void saveItemsToDatabase() {
//Use asynchronous task to run query on the background to avoid locking UI
        try {
// Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                //delete all items and re-insert
                toDoItemDao.deleteAll();
                for (DataModel dataModel : dataModels) {
                    ToDoItem item = new ToDoItem(dataModel.getName());
                    item.setChecked(dataModel.isChecked());
                    item.setType(dataModel.getType());
                    item.setDate(dataModel.getDate());
                    toDoItemDao.insert(item);
                    Log.i("SQLite saved item", item.getToDoItemName());
                }
            });
// Block and wait for the future to complete
            future.get();
        } catch (Exception ex) {
            Log.e("saveItemsToDatabase", ex.getStackTrace().toString());
        }
    }
}

