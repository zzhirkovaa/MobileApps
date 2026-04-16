package ru.mirea.zhirkovaei.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.zhirkovaei.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("result");
                Log.d(MainActivity.class.getSimpleName(),
                        "Task execute. This is result: " + result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            }
        };

        myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        binding.buttonMirea.setOnClickListener(v -> {
            if (myLooper.mHandler == null) {
                Toast.makeText(this, "Подождите, поток ещё создаётся", Toast.LENGTH_SHORT).show();
                return;
            }

            String age = binding.editTextAge.getText().toString();
            String job = binding.editTextJob.getText().toString();

            if (age.isEmpty() || job.isEmpty()) {
                Toast.makeText(this, "Введите возраст и профессию", Toast.LENGTH_SHORT).show();
                return;
            }

            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("AGE", age);
            bundle.putString("JOB", job);
            msg.setData(bundle);

            myLooper.mHandler.sendMessage(msg);
        });
    }
}