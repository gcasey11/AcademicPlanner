package com.example.todo;

import static com.example.todo.MainActivity.EXTRA_IS_NEW;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;


public class EditToDoItemActivity extends Activity implements
		AdapterView.OnItemSelectedListener
{
	private int position=0;
	EditText etItem;
	Spinner spinner;
	ArrayList<String> type = new ArrayList<>();
	private Button calendarButton;
	private Button timeButton;
	private boolean isNew;
	private LocalDateTime date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		
		// Populate the screen using the layout
		setContentView(R.layout.activity_edit_item);


		// Show original content in the text field
		etItem = findViewById(R.id.etEditItem);

		// Populate type arraylist
		type.add("Class");
		type.add("Assignment");
		type.add("Exam");

		// Find the spinner and set the listener
		spinner = findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(this);

		ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, type);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Setting the ArrayAdapter data on the Spinner
		spinner.setAdapter(aa);

		// Calendar Date Picker
		calendarButton = findViewById(R.id.calendarButton);

		calendarButton.setOnClickListener(v -> openCalendarDialog());

		// Time Picker
		timeButton = findViewById(R.id.timeButton);

		timeButton.setOnClickListener(v -> openTimeDialog());

		// Get the data from the main activity screen
		isNew = getIntent().getBooleanExtra(EXTRA_IS_NEW, false);

		// If the item is not new, it will set up all of the fields as they are in the data model to be edited
		if (!isNew) {
			String editItem = getIntent().getStringExtra("item");
			position = getIntent().getIntExtra("position",-1);
			date = (LocalDateTime) getIntent().getSerializableExtra("date");
			etItem.setText(editItem);
			spinner.setSelection(type.indexOf(getIntent().getStringExtra("type")));
		}
		// If the item is new, the date will be set to today and the time will be set to the current time
		// The rest of the fields will be left blank to be edited.
		else {
			date = LocalDateTime.now();
		}

		calendarButton.setText(date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear());
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
		String formattedTime = dateFormat.format(date);
		timeButton.setText(formattedTime);
	}

	/**
	 * Called when the submit button is clicked.
	 * Passes the data back to the main activity
	 * @param v
	 */
	public void onSubmit(View v) {
		// Prepare data intent for sending it back
		if (etItem.getText().toString().isEmpty()) {
			Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
		}
		else {
			Intent data = new Intent();

			// Pass relevant data back as a result
			data.putExtra("item", etItem.getText().toString());
			data.putExtra(EXTRA_IS_NEW, getIntent().getBooleanExtra(EXTRA_IS_NEW, false));
			data.putExtra("type", spinner.getSelectedItem().toString());
			data.putExtra("position", position);
			data.putExtra("date", date);

			// Activity finishes OK, return the data
			setResult(RESULT_OK, data); // Set result code and bundle data for response
			finish(); // Close the activity, pass data to parent
		}
	}

	/**
	 * Called when the cancel button is clicked.
	 * Does not pass any data back to the main activity.
	 * @param v
	 */
	public void onCancel(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Cancelling")
				.setMessage(R.string.are_you_sure_cancel)
				.setPositiveButton(R.string.delete, (dialogInterface, i) -> {
					setResult(RESULT_CANCELED); // Set result code and bundle data for response
					finish(); // Close the activity, pass data to parent
				})
				.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
					// User cancelled the dialog
					// Nothing happens
				});
		builder.create().show();

	}

	//Performing action onItemSelected and onNothing selected
	//Spinner interface
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	/**
	 * Opens the calendar dialog.
	 * If the item is new, the date will be set to today.
	 * Otherwise, the date will be set to the item's current due date.
	 */
	private void openCalendarDialog() {
		DatePickerDialog calendarDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            month = month + 1;

            date = LocalDateTime.of(year, month, day, date.getHour(), date.getMinute());
            calendarButton.setText(date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear());

        }, !isNew ? date.getYear() : LocalDateTime.now().getYear(),
				!isNew ? date.getMonthValue() - 1 : LocalDateTime.now().getMonthValue() - 1,
				!isNew ? date.getDayOfMonth() : LocalDateTime.now().getDayOfMonth());

		calendarDialog.show();
	}

	/**
	 * Opens the time dialog.
	 * If the item is new, the time will be set to the current time.
	 * Otherwise, the time will be set to the items due date time.
	 */
	private void openTimeDialog() {
		TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hours, minutes) -> {
            date = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hours, minutes);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
            String formattedTime = dateFormat.format(date);
            timeButton.setText(formattedTime);
        }, !isNew ? date.getHour() : LocalDateTime.now().getHour(),
				!isNew ? date.getMinute() : LocalDateTime.now().getMinute(), true);

		timePickerDialog.show();
	}
}
