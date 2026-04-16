package ru.mirea.zhirkovaei.lesson4;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.zhirkovaei.lesson4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textSongTitle.setText("Believer");
        binding.textArtist.setText("Imagine Dragons");
        binding.textCurrentTime.setText("01:15");
        binding.textDuration.setText("03:40");
        binding.seekBarTrack.setProgress(35);

        binding.buttonPlay.setOnClickListener(v -> {
            if (!isPlaying) {
                binding.buttonPlay.setText("Pause");
                Toast.makeText(this, "Воспроизведение", Toast.LENGTH_SHORT).show();
                isPlaying = true;
            } else {
                binding.buttonPlay.setText("Play");
                Toast.makeText(this, "Пауза", Toast.LENGTH_SHORT).show();
                isPlaying = false;
            }
        });

        binding.buttonPrev.setOnClickListener(v ->
                Toast.makeText(this, "Предыдущий трек", Toast.LENGTH_SHORT).show());

        binding.buttonNext.setOnClickListener(v ->
                Toast.makeText(this, "Следующий трек", Toast.LENGTH_SHORT).show());
    }
}