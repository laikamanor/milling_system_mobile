package com.example.atlanticgrains;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
//        TimePicker t = findViewById(R.id.time_picker);
//        t.setIs24HourView(true);
//        Button btn = findViewById(R.id.btnn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final View dialogView = View.inflate(getBaseContext(), R.layout.test, null);
//                final AlertDialog alertDialog = new AlertDialog.Builder(getBaseContext()).create();
//                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
//                        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
//
//                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
//                                datePicker.getMonth(),
//                                datePicker.getDayOfMonth(),
//                                timePicker.getCurrentHour(),
//                                timePicker.getCurrentMinute());
//                        alertDialog.dismiss();
//                    }});
//                alertDialog.setView(dialogView);
//                alertDialog.show();
//            }
//        });
//
//        TextView tv1 = findViewById(R.id.tv1);
//        TextView tv2 = findViewById(R.id.tv2);
//        tv1.setTypeface(null, Typeface.ITALIC);
//        tv2.setTypeface(null, Typeface.ITALIC);
        Button btnn = findViewById(R.id.date_time_set);
        TimePicker tp1 = findViewById(R.id.tp1);
        tp1.setIs24HourView(true);
        btnn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
//                TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
//                Toast.makeText(getBaseContext(), datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth() + " " + timePicker.getHour() + ":" + timePicker.getMinute(), Toast.LENGTH_SHORT).show();

                final AlertDialog.Builder builder = new AlertDialog.Builder(test.this);
                builder.setCancelable(false);
                builder.setView(R.layout.test);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }
}