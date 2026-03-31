package ru.mirea.zhirkova.intentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TIME = "time_key";
    public static final String EXTRA_SQUARE = "square_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSecondActivity(View view) {
        long dateInMillis = System.currentTimeMillis();
        String format = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String dateString = sdf.format(new Date(dateInMillis));

        int myNumberInGroup = 9;
        int square = myNumberInGroup * myNumberInGroup;

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(EXTRA_TIME, dateString);
        intent.putExtra(EXTRA_SQUARE, square);
        startActivity(intent);
    }
}