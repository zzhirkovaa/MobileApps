package ru.mirea.zhirkova.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MyProgressDialogFragment extends DialogFragment {

    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private int progressStatus = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Создаём ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Загрузка данных");
        progressDialog.setMessage("Пожалуйста, подождите...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);

        // Запускаем имитацию загрузки
        startProgress();

        return progressDialog;
    }

    private void startProgress() {
        progressStatus = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressStatus < 100) {
                    progressStatus += 10;
                    progressDialog.setProgress(progressStatus);
                    handler.postDelayed(this, 300); // Обновляем каждые 300 мс
                } else {
                    // Загрузка завершена
                    dismiss();
                    ((MainActivity) getActivity()).onProgressCompleted();
                }
            }
        }, 300);
    }
}