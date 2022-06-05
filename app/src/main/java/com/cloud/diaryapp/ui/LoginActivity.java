package com.cloud.diaryapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cloud.diaryapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(view -> authenticateUser());

        binding.tvRegister.setOnClickListener(view -> switchToRegister());

        binding.btnLogin.setOnClickListener(view -> {
            if (binding.etMail.getText().toString().contentEquals("")) {
                Toast.makeText(LoginActivity.this,
                        "Email alanını doldurunuz.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.etPassword.getText().toString().contentEquals("")) {
                Toast.makeText(LoginActivity.this,
                        "Parola alanını doldurunuz.", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(binding.etMail.getText().toString(), binding.etPassword.getText().toString())
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    System.out.println("Email doğrulandı : " + user.isEmailVerified());
                                    Intent HomeActivity = new Intent(LoginActivity.this, MainActivity.class);
                                    setResult(RESULT_OK, null);
                                    startActivity(HomeActivity);
                                    LoginActivity.this.finish();

                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "Lütfen emailinzi doğrulayın.", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Doğrulama başarısız.",
                                    Toast.LENGTH_SHORT).show();
                            if (task.getException() != null) {
                                Toast.makeText(LoginActivity.this,
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }

                    });
        });
    }

    private void authenticateUser() {
        String email = binding.etMail.getText().toString();
        String password = binding.etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Lütfen boş alan bırakmayın", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Kimlik doğrulama başarısız.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void switchToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}









































