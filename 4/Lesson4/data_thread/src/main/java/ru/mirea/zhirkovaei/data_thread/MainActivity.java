package ru.mirea.zhirkovaei.data_thread;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import ru.mirea.zhirkovaei.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "DataThread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Runnable runn1 = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Выполнился runn1 через runOnUiThread");
                binding.tvInfo.setText(
                        "1) runOnUiThread(Runnable)\n" +
                                "- выполняет код в главном UI-потоке.\n" +
                                "- вызывается из Activity.\n" +
                                "- здесь выполнился первым: runn1"
                );
            }
        };

        final Runnable runn2 = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Выполнился runn2 через post");
                binding.tvInfo.append(
                        "\n\n2) View.post(Runnable)\n" +
                                "- ставит задачу в очередь UI-потока у конкретного View.\n" +
                                "- выполняется почти сразу.\n" +
                                "- здесь выполнился вторым: runn2"
                );
            }
        };

        final Runnable runn3 = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Выполнился runn3 через postDelayed");
                binding.tvInfo.append(
                        "\n\n3) View.postDelayed(Runnable, long)\n" +
                                "- тоже ставит задачу в UI-поток,\n" +
                                "  но с задержкой.\n" +
                                "- здесь выполнился третьим: runn3"
                );
            }
        };

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Фоновый поток стартовал");
                    TimeUnit.SECONDS.sleep(2);

                    Log.d(TAG, "Отправляем runn1");
                    runOnUiThread(runn1);

                    TimeUnit.SECONDS.sleep(1);

                    Log.d(TAG, "Отправляем runn3 с задержкой 2000 мс");
                    binding.tvInfo.postDelayed(runn3, 2000);

                    Log.d(TAG, "Отправляем runn2");
                    binding.tvInfo.post(runn2);

                } catch (InterruptedException e) {
                    Log.d(TAG, "Ошибка потока: " + e.getMessage());
                }
            }
        });
        t.start();
    }
}