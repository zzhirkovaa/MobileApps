package ru.mirea.zhirkovaei.firebaseauth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ru.mirea.zhirkovaei.firebaseauth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    private com.google.firebase.auth.FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        binding.signInButton.setOnClickListener(view -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            signIn(email, password);
        });

        binding.createAccountButton.setOnClickListener(view -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            createAccount(email, password);
        });

        binding.signOutButton.setOnClickListener(view -> signOut());

        binding.verifyEmailButton.setOnClickListener(view -> sendEmailVerification());
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            Toast.makeText(
                                    MainActivity.this,
                                    "Аккаунт создан",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            Toast.makeText(
                                    MainActivity.this,
                                    "Ошибка регистрации: " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            Toast.makeText(
                                    MainActivity.this,
                                    "Вход выполнен",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(
                                    MainActivity.this,
                                    "Ошибка входа: " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            binding.statusTextView.setText(R.string.auth_failed);
                            updateUI(null);
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);

        Toast.makeText(
                MainActivity.this,
                "Вы вышли из аккаунта",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void sendEmailVerification() {
        binding.verifyEmailButton.setEnabled(false);

        final FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            binding.verifyEmailButton.setEnabled(true);
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.verifyEmailButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Письмо подтверждения отправлено на " + user.getEmail(),
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());

                            Toast.makeText(
                                    MainActivity.this,
                                    "Не удалось отправить письмо подтверждения",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = binding.emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Введите email");
            valid = false;
        } else {
            binding.emailEditText.setError(null);
        }

        String password = binding.passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Введите пароль");
            valid = false;
        } else if (password.length() < 6) {
            binding.passwordEditText.setError("Пароль должен быть не менее 6 символов");
            valid = false;
        } else {
            binding.passwordEditText.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            binding.statusTextView.setText(
                    getString(
                            R.string.emailpassword_status_fmt,
                            user.getEmail(),
                            user.isEmailVerified()
                    )
            );

            binding.detailTextView.setText(
                    getString(
                            R.string.firebase_status_fmt,
                            user.getUid()
                    )
            );

            binding.emailPasswordButtons.setVisibility(View.GONE);
            binding.emailPasswordFields.setVisibility(View.GONE);

            binding.signedInButtons.setVisibility(View.VISIBLE);

            binding.verifyEmailButton.setEnabled(!user.isEmailVerified());

        } else {
            binding.statusTextView.setText(R.string.signed_out);
            binding.detailTextView.setText(null);

            binding.emailPasswordButtons.setVisibility(View.VISIBLE);
            binding.emailPasswordFields.setVisibility(View.VISIBLE);

            binding.signedInButtons.setVisibility(View.GONE);
        }
    }
}