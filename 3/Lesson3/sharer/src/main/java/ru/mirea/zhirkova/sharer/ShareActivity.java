package ru.mirea.zhirkova.sharer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {

    private TextView textReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        textReceived = findViewById(R.id.textReceived);

        Intent intent = getIntent();

        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction())) {
            String type = intent.getType();

            if (type != null && "text/plain".equals(type)) {
                String receivedText = intent.getStringExtra(Intent.EXTRA_TEXT);

                if (receivedText != null) {
                    textReceived.setText("Полученный текст: " + receivedText);
                }
            }
        }
    }
}