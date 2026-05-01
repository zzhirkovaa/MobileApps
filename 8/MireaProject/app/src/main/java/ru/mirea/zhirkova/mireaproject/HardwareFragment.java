package ru.mirea.zhirkova.mireaproject;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HardwareFragment extends Fragment implements SensorEventListener {

    private TextView textLightValue;
    private TextView textLightConclusion;

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private Button buttonTakePhoto;
    private ImageView imageCameraResult;

    private Uri imageUri;

    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;

    private TextView textAudioStatus;
    private Button buttonRecordAudio;
    private Button buttonPlayAudio;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private String recordFilePath = null;

    private boolean isStartRecording = true;
    private boolean isStartPlaying = true;
    private boolean isAudioRecorded = false;

    private ActivityResultLauncher<String> audioPermissionLauncher;

    private static final String TAG = "HardwareFragment";

    public HardwareFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_hardware, container, false);

        textLightValue = view.findViewById(R.id.textLightValue);
        textLightConclusion = view.findViewById(R.id.textLightConclusion);

        buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto);
        imageCameraResult = view.findViewById(R.id.imageCameraResult);

        textAudioStatus = view.findViewById(R.id.textAudioStatus);
        buttonRecordAudio = view.findViewById(R.id.buttonRecordAudio);
        buttonPlayAudio = view.findViewById(R.id.buttonPlayAudio);

        buttonPlayAudio.setEnabled(false);

        File musicDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        if (musicDirectory != null) {
            recordFilePath = new File(musicDirectory, "hardware_voice_note.3gp").getAbsolutePath();
        } else {
            textAudioStatus.setText("Статус: папка для аудио недоступна");
            buttonRecordAudio.setEnabled(false);
        }

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(requireContext(), "Разрешение на камеру не выдано", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        audioPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startRecordingWithUi();
                    } else {
                        Toast.makeText(requireContext(), "Разрешение на микрофон не выдано", Toast.LENGTH_SHORT).show();
                        textAudioStatus.setText("Статус: нет разрешения на микрофон");
                    }
                }
        );

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageCameraResult.setImageURI(imageUri);
                    }
                }
        );

        buttonTakePhoto.setOnClickListener(viewButton -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        buttonRecordAudio.setOnClickListener(viewButton -> {
            if (recordFilePath == null) {
                Toast.makeText(requireContext(), "Файл для записи недоступен", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isStartRecording) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED) {
                    startRecordingWithUi();
                } else {
                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
                }
            } else {
                stopRecordingWithUi();
            }
        });

        buttonPlayAudio.setOnClickListener(viewButton -> {
            if (!isAudioRecorded) {
                Toast.makeText(requireContext(), "Сначала запишите голосовую заметку", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isStartPlaying) {
                startPlayingWithUi();
            } else {
                stopPlayingWithUi();
            }
        });

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            textLightValue.setText("Освещённость: датчик недоступен");
            textLightConclusion.setText("Вывод: невозможно определить уровень освещения");
            Toast.makeText(requireContext(), "Датчик освещённости недоступен", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) == null) {
            Toast.makeText(requireContext(), "Приложение камеры не найдено", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File photoFile = createImageFile();

            String authorities = requireContext().getPackageName() + ".fileprovider";

            imageUri = FileProvider.getUriForFile(
                    requireContext(),
                    authorities,
                    photoFile
            );

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            cameraActivityResultLauncher.launch(cameraIntent);

        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка создания файла фото", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.ENGLISH
        ).format(new Date());

        String imageFileName = "IMAGE_" + timeStamp + "_";

        File storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDirectory == null) {
            throw new IOException("Storage directory is null");
        }

        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDirectory
        );
    }

    private void startRecordingWithUi() {
        startRecording();

        buttonRecordAudio.setText("Остановить запись");
        buttonPlayAudio.setEnabled(false);
        textAudioStatus.setText("Статус: идёт запись голосовой заметки");

        isStartRecording = false;
    }

    private void stopRecordingWithUi() {
        stopRecording();

        buttonRecordAudio.setText("Начать запись голосовой заметки");
        buttonPlayAudio.setEnabled(true);
        textAudioStatus.setText("Статус: запись сохранена");

        isStartRecording = true;
        isAudioRecorded = true;

        File audioFile = new File(recordFilePath);
        Log.d(TAG, "Файл существует: " + audioFile.exists() + ", размер: " + audioFile.length() + " байт");
    }

    private void startPlayingWithUi() {
        startPlaying();

        buttonPlayAudio.setText("Остановить воспроизведение");
        buttonRecordAudio.setEnabled(false);
        textAudioStatus.setText("Статус: воспроизведение голосовой заметки");

        isStartPlaying = false;
    }

    private void stopPlayingWithUi() {
        stopPlaying();

        buttonPlayAudio.setText("Воспроизвести голосовую заметку");
        buttonRecordAudio.setEnabled(true);
        textAudioStatus.setText("Статус: воспроизведение остановлено");

        isStartPlaying = true;
    }

    private void startRecording() {
        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
            Log.d(TAG, "Запись началась. Файл: " + recordFilePath);
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed", e);
            Toast.makeText(requireContext(), "Ошибка подготовки записи", Toast.LENGTH_SHORT).show();
            releaseRecorder();
        } catch (RuntimeException e) {
            Log.e(TAG, "start recording failed", e);
            Toast.makeText(requireContext(), "Ошибка запуска записи", Toast.LENGTH_SHORT).show();
            releaseRecorder();
        }
    }

    private void stopRecording() {
        if (recorder == null) {
            return;
        }

        try {
            recorder.stop();
            Log.d(TAG, "Запись остановлена");
        } catch (RuntimeException e) {
            Log.e(TAG, "stop recording failed", e);
            Toast.makeText(requireContext(), "Ошибка остановки записи", Toast.LENGTH_SHORT).show();
        } finally {
            releaseRecorder();
        }
    }

    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        player.setVolume(1.0f, 1.0f);

        try {
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();
            Log.d(TAG, "Воспроизведение началось");

            player.setOnCompletionListener(mediaPlayer -> {
                stopPlayingWithUi();
            });

        } catch (IOException e) {
            Log.e(TAG, "prepare() failed", e);
            Toast.makeText(requireContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
            releasePlayer();
        }
    }

    private void stopPlaying() {
        releasePlayer();
        Log.d(TAG, "Воспроизведение остановлено");
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (lightSensor != null) {
            sensorManager.registerListener(
                    this,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        };

        if (recorder != null) {
            stopRecordingWithUi();
        }

        if (player != null) {
            stopPlayingWithUi();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        releaseRecorder();
        releasePlayer();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightValue = event.values[0];

            textLightValue.setText("Освещённость: " + lightValue + " lux");

            if (lightValue < 10) {
                textLightConclusion.setText("Вывод: темно");
            } else if (lightValue <= 1000) {
                textLightConclusion.setText("Вывод: нормальное освещение");
            } else {
                textLightConclusion.setText("Вывод: ярко");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}