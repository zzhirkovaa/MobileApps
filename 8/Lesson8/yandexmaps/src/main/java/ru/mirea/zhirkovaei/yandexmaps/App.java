package ru.mirea.zhirkovaei.yandexmaps;

import android.app.Application;
import android.util.Log;

import com.yandex.mapkit.MapKitFactory;

public class App extends Application {

    private final String MAPKIT_API_KEY = "e2a6c7c7-100b-44d4-9edf-e4ca2b084abc";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("YANDEX_KEY_TEST", "Используется ключ: " + MAPKIT_API_KEY);

        MapKitFactory.setApiKey(MAPKIT_API_KEY);
    }
}