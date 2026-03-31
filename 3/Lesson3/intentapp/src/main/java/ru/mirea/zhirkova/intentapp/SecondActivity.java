package ru.mirea.zhirkova.intentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textViewResult = findViewById(R.id.textViewResult);

        String time = getIntent().getStringExtra(MainActivity.EXTRA_TIME);
        int square = getIntent().getIntExtra(MainActivity.EXTRA_SQUARE, 0);

        String message = "КВАДРАТ ЗНАЧЕНИЯ МОЕГО НОМЕРА ПО СПИСКУ В ГРУППЕ СОСТАВЛЯЕТ ЧИСЛО "
                + square + ", а текущее время " + time;

        textViewResult.setText(message);
    }
}