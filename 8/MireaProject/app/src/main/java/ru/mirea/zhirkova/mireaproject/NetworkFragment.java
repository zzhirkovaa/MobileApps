package ru.mirea.zhirkova.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkFragment extends Fragment {

    private TextView textViewNetworkData;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);

        textViewNetworkData = view.findViewById(R.id.textViewNetworkData);

        loadData();

        return view;
    }

    private void loadData() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        apiService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<Post>> call,
                    @NonNull Response<List<Post>> response
            ) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Post firstPost = response.body().get(0);

                    String result =
                            "Источник: https://jsonplaceholder.typicode.com/posts\n\n"
                                    + "User ID: " + firstPost.getUserId() + "\n"
                                    + "Post ID: " + firstPost.getId() + "\n\n"
                                    + "Title:\n" + firstPost.getTitle() + "\n\n"
                                    + "Body:\n" + firstPost.getBody();

                    textViewNetworkData.setText(result);
                } else {
                    textViewNetworkData.setText("Данные не получены");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                textViewNetworkData.setText("Ошибка загрузки данных");
                Toast.makeText(
                        requireContext(),
                        "Ошибка сети: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}