package com.example.carrentapp;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTime_Selection extends AppCompatActivity {
    final Calendar calendar = Calendar.getInstance();
    TextInputEditText FromDate__1, ToDate__1, FromTime__1, ToTime__1;
    int year1, month1, day1;
    String DatabaseReferenceKey = "";
    private TextInputLayout FromDate, FromTime, ToTime, ToDate;
    private TimePicker timePicker;
    String from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time__selection);

        FromTime = findViewById(R.id.FromTime);
        FromDate = findViewById(R.id.FromDate);
        ToDate = findViewById(R.id.ToDate);
        ToTime = findViewById(R.id.ToTime);
        FromDate__1 = findViewById(R.id.FromDate__1);
        ToDate__1 = findViewById(R.id.ToDate__1);
        FromTime__1 = findViewById(R.id.FromTime__1);
        ToTime__1 = findViewById(R.id.ToTime__1);

        findViewById(R.id.btn1).setOnClickListener(view -> next());


        FromDate__1.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(FromDate__1);
            };
            new DatePickerDialog(DateTime_Selection.this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();


        });

        ToDate__1.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(ToDate__1);
            };
            new DatePickerDialog(DateTime_Selection.this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();


        });

        FromTime__1.setOnClickListener(v -> {
            Calendar mCurrentTime = Calendar.getInstance();
            int hr = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            int min = mCurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(DateTime_Selection.this, (view, hourOfDay, minute) -> FromTime__1.setText(hourOfDay + ":" + minute), hr, min, false);
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        });

        ToTime__1.setOnClickListener(v -> {
            Calendar mCurrentTime = Calendar.getInstance();
            int hr = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            int min = mCurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(DateTime_Selection.this, (view, hourOfDay, minute) -> ToTime__1.setText(hourOfDay + ":" + minute), hr, min, false);
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();

        });
        DatabaseReferenceKey = getIntent().getStringExtra("DatabaseReferenceKey");
        from = getIntent().getExtras().getString("from");
        to = getIntent().getExtras().getString("to");
    }

    private void next() {
          if    (FromTime__1.getText().toString().trim().equals("")
                || ToTime__1.getText().toString().trim().equals("")
                || FromDate__1.getText().toString().trim().equals("")
                || ToDate__1.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, DriverLicence_User.class)
                .putExtra("FromDate", FromDate__1.getText().toString())
                .putExtra("ToDate", ToDate__1.getText().toString())
                .putExtra("FromTime", FromDate__1.getText().toString())
                .putExtra("ToTime", ToTime__1.getText().toString())
                .putExtra("from", from)
                .putExtra("to", to)
                .putExtra("DatabaseReferenceKey", DatabaseReferenceKey)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }


    private void updateLabel(TextInputEditText setTextField) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        setTextField.setText(sdf.format(calendar.getTime()));
    }


}