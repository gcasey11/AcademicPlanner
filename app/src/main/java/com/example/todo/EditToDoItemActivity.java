package com.example.todo;

import android.app.Activity;
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
import java.util.Locale;


public class EditToDoItemActivity extends Activity implements
		AdapterView.OnItemSelectedListener
{
	public int position=0;
	EditText etItem;
	Spinner spinner;
	String[] type;
	private TextView showText;
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
		etItem = (EditText)findViewById(R.id.etEditItem);
		type = getResources().getStringArray(R.array.type);
		Spinner spin = (Spinner) findViewById(R.id.spinner);
		spin.setOnItemSelectedListener(this);

		//Creating the ArrayAdapter instance having the country list
		ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,type);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Setting the ArrayAdapter data on the Spinner
		spin.setAdapter(aa);

		// Calendar Date Picker
		calendarButton = findViewById(R.id.calendarButton);

		calendarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openCalendarDialog();
			}
		});

		// Time Picker
		timeButton = findViewById(R.id.timeButton);

		timeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openTimeDialog();
			}
		});

		// Get the data from the main activity screen
		isNew = getIntent().getBooleanExtra("isNew", false);

		if (!isNew) {
			String editItem = getIntent().getStringExtra("item");
			position = getIntent().getIntExtra("position",-1);
			date = (LocalDateTime) getIntent().getSerializableExtra("date");
			etItem.setText(editItem);
		}
		else {
			date = LocalDateTime.now();
		}

		calendarButton.setText(date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear());
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
		String formattedTime = dateFormat.format(date);
		timeButton.setText(formattedTime);
	}

	public void onSubmit(View v) {
		// Prepare data intent for sending it back
		Intent data = new Intent();

		// Pass relevant data back as a result
		data.putExtra("item", etItem.getText().toString());
		data.putExtra("isNew", getIntent().getBooleanExtra("isNew", false));
		data.putExtra("type", spinner.getSelectedItem().toString());
		data.putExtra("position", position);

		// Activity finishes OK, return the data
		setResult(RESULT_OK, data); // Set result code and bundle data for response
		finish(); // Close the activity, pass data to parent
	}

	public void onCancel(View v) {
		setResult(RESULT_CANCELED); // Set result code and bundle data for response
		finish();// Close the activity, pass data to parent
	}

	//Performing action onItemSelected and onNothing selected
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
		Toast.makeText(getApplicationContext(), type[position] , Toast.LENGTH_LONG).show();
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		if (getIntent().getBooleanExtra("isNew", false)) {
			Toast.makeText(getApplicationContext(), "Please select a type", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(getApplicationContext(), getIntent().getStringExtra("type") , Toast.LENGTH_LONG).show();
		}
	}

	private void openCalendarDialog() {
		DatePickerDialog calendarDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int day) {
				month = month + 1;

				date = LocalDateTime.of(year, month, day, date.getHour(), date.getMinute());
				calendarButton.setText(date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear());

			}
		}, !isNew ? date.getYear() : LocalDateTime.now().getYear(),
				!isNew ? date.getMonthValue() - 1 : LocalDateTime.now().getMonthValue() - 1,
				!isNew ? date.getDayOfMonth() : LocalDateTime.now().getDayOfMonth());

		calendarDialog.show();
	}

	private void openTimeDialog() {
		TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
				date = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hours, minutes);
				DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
				String formattedTime = dateFormat.format(date);
				timeButton.setText(formattedTime);
			}
		}, !isNew ? date.getHour() : LocalDateTime.now().getHour(), !isNew ? date.getMinute() : LocalDateTime.now().getMinute(), true);

		timePickerDialog.show();
	}
}
