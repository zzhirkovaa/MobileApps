package ru.mirea.zhirkova.mireaproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BackgroundTaskWorker extends Worker {

    public static final String KEY_RESULT = "key_result";

    public BackgroundTaskWorker(@NonNull Context context,
                                @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            return Result.failure();
        }

        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date());

        Data outputData = new Data.Builder()
                .putString(KEY_RESULT, "Фоновая задача завершена в " + time)
                .build();

        return Result.success(outputData);
    }
}