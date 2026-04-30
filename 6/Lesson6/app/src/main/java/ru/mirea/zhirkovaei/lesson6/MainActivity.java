package ru.mirea.zhirkovaei.lesson6;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextGroup;
    private EditText editTextNumber;
    private EditText editTextMovie;
    private Button buttonSave;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "mirea_settings";

    private static final String KEY_GROUP = "GROUP";
    private static final String KEY_NUMBER = "NUMBER";
    private static final String KEY_MOVIE = "MOVIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextGroup = findViewById(R.id.editTextGroup);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextMovie = findViewById(R.id.editTextMovie);
        buttonSave = findViewById(R.id.buttonSave);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        loadData();

        buttonSave.setOnClickListener(view -> saveData());
    }

    private void saveData() {
        String group = editTextGroup.getText().toString();
        String numberText = editTextNumber.getText().toString();
        String movie = editTextMovie.getText().toString();

        if (group.isEmpty() || numberText.isEmpty() || movie.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        int number = Integer.parseInt(numberText);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_GROUP, group);
        editor.putInt(KEY_NUMBER, number);
        editor.putString(KEY_MOVIE, movie);
        editor.apply();

        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        String group = sharedPreferences.getString(KEY_GROUP, "");
        int number = sharedPreferences.getInt(KEY_NUMBER, 0);
        String movie = sharedPreferences.getString(KEY_MOVIE, "");

        editTextGroup.setText(group);

        if (number != 0) {
            editTextNumber.setText(String.valueOf(number));
        }

        editTextMovie.setText(movie);
    }
}