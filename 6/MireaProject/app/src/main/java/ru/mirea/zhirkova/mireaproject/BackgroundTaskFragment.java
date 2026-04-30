package ru.mirea.zhirkova.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class BackgroundTaskFragment extends Fragment {

    private TextView textWorkStatus;
    private TextView textWorkResult;
    private Button buttonStartWork;

    public BackgroundTaskFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_background_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textWorkStatus = view.findViewById(R.id.textWorkStatus);
        textWorkResult = view.findViewById(R.id.textWorkResult);
        buttonStartWork = view.findViewById(R.id.buttonStartWork);

        buttonStartWork.setOnClickListener(v -> {
            OneTimeWorkRequest workRequest =
                    new OneTimeWorkRequest.Builder(BackgroundTaskWorker.class)
                            .build();

            WorkManager workManager = WorkManager.getInstance(requireContext());
            workManager.enqueue(workRequest);

            workManager.getWorkInfoByIdLiveData(workRequest.getId())
                    .observe(getViewLifecycleOwner(), workInfo -> {
                        if (workInfo == null) return;

                        WorkInfo.State state = workInfo.getState();
                        textWorkStatus.setText("Статус: " + state.name());

                        if (state == WorkInfo.State.SUCCEEDED) {
                            String result = workInfo.getOutputData()
                                    .getString(BackgroundTaskWorker.KEY_RESULT);
                            textWorkResult.setText(result);
                        } else if (state == WorkInfo.State.FAILED) {
                            textWorkResult.setText("Ошибка выполнения задачи");
                        }
                    });
        });
    }
}