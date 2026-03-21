package ru.mirea.zhirkova.activitylifecycle;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Тег для логирования
    private static final String TAG = "LifecycleActivity";

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получаем ссылку на EditText
        editText = findViewById(R.id.editTextInput);

        Log.i(TAG, "onCreate() - активность создана");

        // Проверяем, есть ли сохраненное состояние
        if (savedInstanceState == null) {
            Log.i(TAG, "onCreate(): приложение запущено впервые");
        } else {
            Log.i(TAG, "onCreate(): приложение восстановлено из памяти");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart() - активность становится видимой");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState() - восстановление состояния");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume() - активность в фокусе, пользователь может взаимодействовать");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause() - активность теряет фокус");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState() - сохранение состояния");

        // Сохраняем текст из EditText
        if (editText != null) {
            String currentText = editText.getText().toString();
            outState.putString("edit_text_content", currentText);
            Log.i(TAG, "onSaveInstanceState(): сохранен текст = " + currentText);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop() - активность остановлена, не видна");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart() - активность перезапускается");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy() - активность уничтожена");
    }
}