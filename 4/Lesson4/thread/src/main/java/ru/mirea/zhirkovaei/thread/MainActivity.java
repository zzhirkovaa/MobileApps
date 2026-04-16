package ru.mirea.zhirkovaei.thread;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import ru.mirea.zhirkovaei.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread mainThread = Thread.currentThread();

        binding.textView.setText("Имя текущего потока: " + mainThread.getName());

        mainThread.setName("ГРУППА: БСБО-08-23, НОМЕР ПО СПИСКУ: 9, ЛЮБИМЫЙ ФИЛЬМ: Огонь");
        binding.textView.append("\nНовое имя потока: " + mainThread.getName());

        Log.d(MainActivity.class.getSimpleName(),
                "Stack: " + Arrays.toString(mainThread.getStackTrace()));
        Log.d(MainActivity.class.getSimpleName(),
                "Group: " + mainThread.getThreadGroup());

        binding.buttonMirea.setOnClickListener(v -> {
            String lessonsText = binding.editTextLessons.getText().toString();
            String daysText = binding.editTextDays.getText().toString();

            if (lessonsText.isEmpty() || daysText.isEmpty()) {
                binding.textView.setText("Введите количество пар и учебных дней");
                return;
            }

            int lessons = Integer.parseInt(lessonsText);
            int days = Integer.parseInt(daysText);

            if (days == 0) {
                binding.textView.setText("Количество учебных дней не может быть 0");
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    int numberThread = counter++;

                    Log.d("ThreadProject",
                            String.format("Запущен поток № %d", numberThread));

                    long endTime = System.currentTimeMillis() + 3 * 1000;

                    while (System.currentTimeMillis() < endTime) {
                        synchronized (this) {
                            try {
                                wait(endTime - System.currentTimeMillis());
                                Log.d(MainActivity.class.getSimpleName(),
                                        "Endtime: " + endTime);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    double average = (double) lessons / days;

                    Log.d("ThreadProject", "Выполнен поток № " + numberThread);

                    runOnUiThread(() -> binding.textView.setText(
                            "Имя текущего потока: " + Thread.currentThread().getName()
                                    + "\nСреднее количество пар в день: " + average
                    ));
                }
            }).start();
        });
    }
}