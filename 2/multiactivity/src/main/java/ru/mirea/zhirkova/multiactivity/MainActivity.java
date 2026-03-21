package ru.mirea.zhirkova.multiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText editTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = findViewById(R.id.editTextInput);

        Log.i(TAG, "onCreate()");
    }

    // Метод для кнопки "Start new activity!"
    public void onClickNewActivity(View view) {
        Log.i(TAG, "onClickNewActivity() - запуск SecondActivity без данных");
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }

    // Метод для кнопки "Отправить"
    public void onClickSend(View view) {
        String inputText = editTextInput.getText().toString();

        if (inputText.isEmpty()) {
            inputText = "Текст не введён";
        }

        Log.i(TAG, "onClickSend() - передача текста: " + inputText);

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("USER_TEXT", inputText);
        startActivity(intent);
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