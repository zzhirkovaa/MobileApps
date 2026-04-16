package ru.mirea.zhirkovaei.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MyLooper extends Thread {
    public Handler mHandler;
    private Handler mainHandler;

    public MyLooper(Handler mainThreadHandler) {
        mainHandler = mainThreadHandler;
    }

    @Override
    public void run() {
        Log.d("MyLooper", "run");
        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String ageString = msg.getData().getString("AGE");
                String job = msg.getData().getString("JOB");

                int age = Integer.parseInt(ageString);

                Log.d("MyLooper", "Получено: возраст = " + age + ", работа = " + job);

                try {
                    Thread.sleep(age * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("result",
                        "Через " + age + " секунд обработано: возраст = " + age + ", работа = " + job);
                message.setData(bundle);

                mainHandler.sendMessage(message);
            }
        };

        Looper.loop();
    }
}