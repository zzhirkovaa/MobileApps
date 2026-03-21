package ru.mirea.zhirkova.multiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";
    private TextView textViewReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textViewReceived = findViewById(R.id.textViewReceived);

        Log.i(TAG, "onCreate()");

        // Получение данных из Intent
        Intent intent = getIntent();
        if (intent.hasExtra("USER_TEXT")) {
            String receivedText = intent.getStringExtra("USER_TEXT");
            textViewReceived.setText(receivedText);
            Log.i(TAG, "Получен текст: " + receivedText);
        } else {
            textViewReceived.setText("Нет переданных данных");
            Log.i(TAG, "Данные не переданы");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
}