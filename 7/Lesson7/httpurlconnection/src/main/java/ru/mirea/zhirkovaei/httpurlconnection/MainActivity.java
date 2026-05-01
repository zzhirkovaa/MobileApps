package ru.mirea.zhirkovaei.httpurlconnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ru.mirea.zhirkovaei.httpurlconnection.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    private static final String IP_INFO_URL = "https://ipinfo.io/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonGetInfo.setOnClickListener(view -> checkInternetAndLoadData());
    }

    private void checkInternetAndLoadData() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;

        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadPageTask().execute(IP_INFO_URL);
        } else {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();
            binding.textViewStatus.setText("Нет подключения к интернету");
        }
    }

    private class DownloadPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            binding.textViewStatus.setText("Загружаем данные об IP...");
            clearFields();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "IP info response: " + result);

            if (result.startsWith("error")) {
                binding.textViewStatus.setText("Ошибка загрузки данных");
                return;
            }

            parseIpInfo(result);
        }
    }

    private String downloadInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";

        HttpsURLConnection connection = null;

        try {
            URL url = new URL(address);

            connection = (HttpsURLConnection) url.openConnection();

            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                int read;
                while ((read = inputStream.read()) != -1) {
                    byteArrayOutputStream.write(read);
                }

                byteArrayOutputStream.close();

                data = byteArrayOutputStream.toString();

            } else {
                data = "error: " + connection.getResponseMessage()
                        + ". Error Code: " + responseCode;
            }

        } catch (IOException e) {
            e.printStackTrace();
            data = "error: " + e.getMessage();

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return data;
    }

    private void parseIpInfo(String result) {
        try {
            JSONObject responseJson = new JSONObject(result);

            String ip = responseJson.optString("ip", "нет данных");
            String city = responseJson.optString("city", "нет данных");
            String region = responseJson.optString("region", "нет данных");
            String country = responseJson.optString("country", "нет данных");
            String loc = responseJson.optString("loc", "");
            String org = responseJson.optString("org", "нет данных");
            String postal = responseJson.optString("postal", "нет данных");
            String timezone = responseJson.optString("timezone", "нет данных");

            binding.textViewStatus.setText("Данные об IP успешно загружены");

            binding.textViewIp.setText("IP: " + ip);
            binding.textViewCity.setText("Город: " + city);
            binding.textViewRegion.setText("Регион: " + region);
            binding.textViewCountry.setText("Страна: " + country);
            binding.textViewLocation.setText("Координаты: " + loc);
            binding.textViewOrg.setText("Организация: " + org);
            binding.textViewPostal.setText("Почтовый индекс: " + postal);
            binding.textViewTimezone.setText("Часовой пояс: " + timezone);

            if (!loc.isEmpty() && loc.contains(",")) {
                String[] coordinates = loc.split(",");

                String latitude = coordinates[0];
                String longitude = coordinates[1];

                String weatherUrl =
                        "https://api.open-meteo.com/v1/forecast?latitude="
                                + latitude
                                + "&longitude="
                                + longitude
                                + "&current_weather=true";

                new DownloadWeatherTask().execute(weatherUrl);

            } else {
                binding.textViewStatus.setText("Координаты не найдены");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            binding.textViewStatus.setText("Ошибка разбора JSON с IP");
        }
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            binding.textViewStatus.setText("Загружаем данные о погоде...");
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "Weather response: " + result);

            if (result.startsWith("error")) {
                binding.textViewStatus.setText("Ошибка загрузки погоды");
                return;
            }

            parseWeatherInfo(result);
        }
    }

    private void parseWeatherInfo(String result) {
        try {
            JSONObject responseJson = new JSONObject(result);

            JSONObject currentWeather = responseJson.getJSONObject("current_weather");

            double temperature = currentWeather.getDouble("temperature");
            double windSpeed = currentWeather.getDouble("windspeed");
            int weatherCode = currentWeather.getInt("weathercode");

            binding.textViewTemperature.setText("Температура: " + temperature + " °C");
            binding.textViewWindSpeed.setText("Скорость ветра: " + windSpeed + " км/ч");
            binding.textViewWeatherCode.setText("Код погоды: " + weatherCode);

            binding.textViewStatus.setText("Данные успешно загружены");

        } catch (JSONException e) {
            e.printStackTrace();
            binding.textViewStatus.setText("Ошибка разбора JSON с погодой");
        }
    }

    private void clearFields() {
        binding.textViewIp.setText("IP: —");
        binding.textViewCity.setText("Город: —");
        binding.textViewRegion.setText("Регион: —");
        binding.textViewCountry.setText("Страна: —");
        binding.textViewLocation.setText("Координаты: —");
        binding.textViewOrg.setText("Организация: —");
        binding.textViewPostal.setText("Почтовый индекс: —");
        binding.textViewTimezone.setText("Часовой пояс: —");

        binding.textViewTemperature.setText("Температура: —");
        binding.textViewWindSpeed.setText("Скорость ветра: —");
        binding.textViewWeatherCode.setText("Код погоды: —");
    }
}