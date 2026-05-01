package ru.mirea.zhirkova.mireaproject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FilesFragment extends Fragment {

    private EditText editTextLoadFileName;
    private Button buttonLoadFile;
    private TextView textViewEncryptedResult;
    private TextView textViewDecryptedResult;
    private FloatingActionButton fabCreateFileRecord;

    private static final int CAESAR_SHIFT = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_files, container, false);

        editTextLoadFileName = view.findViewById(R.id.editTextLoadFileName);
        buttonLoadFile = view.findViewById(R.id.buttonLoadFile);
        textViewEncryptedResult = view.findViewById(R.id.textViewEncryptedResult);
        textViewDecryptedResult = view.findViewById(R.id.textViewDecryptedResult);
        fabCreateFileRecord = view.findViewById(R.id.fabCreateFileRecord);

        fabCreateFileRecord.setOnClickListener(v -> showCreateRecordDialog());

        buttonLoadFile.setOnClickListener(v -> loadEncryptedFile());

        return view;
    }

    private void showCreateRecordDialog() {
        Context context = requireContext();

        LinearLayout dialogLayout = new LinearLayout(context);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(48, 24, 48, 0);

        EditText editTextFileName = new EditText(context);
        editTextFileName.setHint(getString(R.string.files_dialog_file_name_hint));
        editTextFileName.setSingleLine(true);

        EditText editTextRecordText = new EditText(context);
        editTextRecordText.setHint(getString(R.string.files_dialog_text_hint));
        editTextRecordText.setMinLines(4);
        editTextRecordText.setGravity(android.view.Gravity.TOP);

        dialogLayout.addView(editTextFileName);
        dialogLayout.addView(editTextRecordText);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.files_dialog_title)
                .setView(dialogLayout)
                .setPositiveButton(R.string.files_dialog_save, null)
                .setNegativeButton(R.string.files_dialog_cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String fileName = editTextFileName.getText().toString().trim();
                String recordText = editTextRecordText.getText().toString();

                if (fileName.isEmpty() || recordText.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.files_error_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                saveEncryptedFile(fileName, recordText);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void saveEncryptedFile(String fileName, String text) {
        String correctFileName = getCorrectFileName(fileName);
        String encryptedText = encryptCaesar(text);

        try {
            FileOutputStream outputStream = requireContext().openFileOutput(
                    correctFileName,
                    Context.MODE_PRIVATE
            );

            outputStream.write(encryptedText.getBytes());
            outputStream.close();

            editTextLoadFileName.setText(correctFileName);
            textViewEncryptedResult.setText(encryptedText);
            textViewDecryptedResult.setText(text);

            Toast.makeText(requireContext(), R.string.files_saved, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ошибка сохранения файла", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadEncryptedFile() {
        String fileName = editTextLoadFileName.getText().toString().trim();

        if (fileName.isEmpty()) {
            Toast.makeText(requireContext(), R.string.files_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        String correctFileName = getCorrectFileName(fileName);

        try {
            FileInputStream inputStream = requireContext().openFileInput(correctFileName);

            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            String encryptedText = new String(bytes);
            String decryptedText = decryptCaesar(encryptedText);

            textViewEncryptedResult.setText(encryptedText);
            textViewDecryptedResult.setText(decryptedText);

        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.files_error_not_found, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getCorrectFileName(String fileName) {
        if (!fileName.endsWith(".txt")) {
            return fileName + ".txt";
        }

        return fileName;
    }

    private String encryptCaesar(String text) {
        StringBuilder result = new StringBuilder();

        for (char symbol : text.toCharArray()) {
            result.append((char) (symbol + CAESAR_SHIFT));
        }

        return result.toString();
    }

    private String decryptCaesar(String text) {
        StringBuilder result = new StringBuilder();

        for (char symbol : text.toCharArray()) {
            result.append((char) (symbol - CAESAR_SHIFT));
        }

        return result.toString();
    }
}