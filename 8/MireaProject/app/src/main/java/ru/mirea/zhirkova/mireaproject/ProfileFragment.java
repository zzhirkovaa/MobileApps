package ru.mirea.zhirkova.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText editTextName;
    private EditText editTextGroup;
    private EditText editTextNumber;
    private EditText editTextLanguage;
    private EditText editTextStatus;
    private Button buttonSaveProfile;

    private static final String PREFS_NAME = "profile_preferences";

    private static final String KEY_NAME = "profile_name";
    private static final String KEY_GROUP = "profile_group";
    private static final String KEY_NUMBER = "profile_number";
    private static final String KEY_LANGUAGE = "profile_language";
    private static final String KEY_STATUS = "profile_status";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextName = view.findViewById(R.id.editTextProfileName);
        editTextGroup = view.findViewById(R.id.editTextProfileGroup);
        editTextNumber = view.findViewById(R.id.editTextProfileNumber);
        editTextLanguage = view.findViewById(R.id.editTextProfileLanguage);
        editTextStatus = view.findViewById(R.id.editTextProfileStatus);
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile);

        loadProfile();

        buttonSaveProfile.setOnClickListener(v -> saveProfile());

        return view;
    }

    private void saveProfile() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(KEY_NAME, editTextName.getText().toString());
        editor.putString(KEY_GROUP, editTextGroup.getText().toString());
        editor.putString(KEY_NUMBER, editTextNumber.getText().toString());
        editor.putString(KEY_LANGUAGE, editTextLanguage.getText().toString());
        editor.putString(KEY_STATUS, editTextStatus.getText().toString());

        editor.apply();

        Toast.makeText(requireContext(), "Профиль сохранён", Toast.LENGTH_SHORT).show();
    }

    private void loadProfile() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

        editTextName.setText(preferences.getString(KEY_NAME, ""));
        editTextGroup.setText(preferences.getString(KEY_GROUP, ""));
        editTextNumber.setText(preferences.getString(KEY_NUMBER, ""));
        editTextLanguage.setText(preferences.getString(KEY_LANGUAGE, ""));
        editTextStatus.setText(preferences.getString(KEY_STATUS, ""));
    }
}