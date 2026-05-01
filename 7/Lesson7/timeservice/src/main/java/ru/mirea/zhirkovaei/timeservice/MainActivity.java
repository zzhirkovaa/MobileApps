package ru.mirea.zhirkovaei.timeservice;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import ru.mirea.zhirkovaei.timeservice.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    private final String host = "time.nist.gov";
    private final int port = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonGetTime.setOnClickListener(view -> {
            GetTimeTask timeTask = new GetTimeTask();
            timeTask.execute();
        });
    }

    private class GetTimeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            binding.textViewRaw.setText("Загрузка данных с сервера...");
            binding.textViewDate.setText("Дата: —");
            binding.textViewTime.setText("Время: —");
        }

        @Override
        protected String doInBackground(Void... voids) {
            String timeResult = "";

            try {
                Socket socket = new Socket(host, port);

                BufferedReader reader = SocketUtils.getReader(socket);

                reader.readLine();

                timeResult = reader.readLine();

                Log.d(TAG, "Ответ сервера: " + timeResult);

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                timeResult = "Ошибка подключения: " + e.getMessage();
            }

            return timeResult;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            binding.textViewRaw.setText("Ответ сервера: " + result);

            parseAndShowDateTime(result);
        }
    }

    private void parseAndShowDateTime(String result) {
        try {
            String[] parts = result.trim().split("\\s+");

            String date = parts[1];
            String time = parts[2];

            binding.textViewDate.setText("Дата: " + date);
            binding.textViewTime.setText("Время: " + time);

        } catch (Exception e) {
            binding.textViewDate.setText("Дата: не удалось определить");
            binding.textViewTime.setText("Время: не удалось определить");
        }
    }
}