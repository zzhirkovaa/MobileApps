package ru.mirea.zhirkova.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textViewResult);
    }

    // Вызов диалога выбора времени
    public void onClickShowTimeDialog(View view) {
        MyTimeDialogFragment timeDialog = new MyTimeDialogFragment();
        timeDialog.show(getSupportFragmentManager(), "time_dialog");
    }

    // Вызов диалога выбора даты
    public void onClickShowDateDialog(View view) {
        MyDateDialogFragment dateDialog = new MyDateDialogFragment();
        dateDialog.show(getSupportFragmentManager(), "date_dialog");
    }

    // Вызов диалога загрузки
    public void onClickShowProgressDialog(View view) {
        MyProgressDialogFragment progressDialog = new MyProgressDialogFragment();
        progressDialog.show(getSupportFragmentManager(), "progress_dialog");
    }

    // Обработчик выбора времени
    public void onTimeSelected(int hour, int minute) {
        String timeString = String.format("Выбрано время: %02d:%02d", hour, minute);
        textViewResult.setText(timeString);
        Toast.makeText(this, timeString, Toast.LENGTH_SHORT).show();
    }

    // Обработчик выбора даты
    public void onDateSelected(int year, int month, int day) {
        String dateString = String.format("Выбрана дата: %02d.%02d.%d", day, month, year);
        textViewResult.setText(dateString);
        Toast.makeText(this, dateString, Toast.LENGTH_SHORT).show();
    }

    // Обработчик завершения загрузки
    public void onProgressCompleted() {
        textViewResult.setText("Загрузка завершена!");
        Toast.makeText(this, "Загрузка завершена!", Toast.LENGTH_SHORT).show();
    }
}