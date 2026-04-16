package ru.mirea.zhirkovaei.serviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.zhirkovaei.serviceapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔥 Разрешение на уведомления (ОБЯЗАТЕЛЬНО для Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1);
            }
        }

        // ▶️ Старт сервиса
        binding.btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent); // для Android 8+
            } else {
                startService(intent);
            }
        });

        // ⏹ Стоп сервиса
        binding.btnStop.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerService.class);
            stopService(intent);
        });
    }
}