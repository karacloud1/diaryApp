package com.cloud.diaryapp.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cloud.diaryapp.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivityRegisterBinding binding;
    private static final String TAG = "RegistirActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.tvBack.setOnClickListener(view -> switchToLogin());

        binding.btnRegister.setOnClickListener(view -> {
            if (binding.etMail.getText().toString().contentEquals("")) {
                Toast.makeText(RegisterActivity.this, "Email alanını doldurunuz.",
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (binding.etPassword.getText().toString().contentEquals("")) {
                Toast.makeText(RegisterActivity.this, "Parola alanını doldurunuz.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.createUserWithEmailAndPassword(
                    binding.etMail.getText().toString(),
                    binding.etPassword.getText().toString())
                    .addOnCompleteListener(RegisterActivity.this,
                            task -> {
                                if (task.isSuccessful()) {
                                    registerUser();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Doğrulama başarısız.",
                                            Toast.LENGTH_SHORT).show();

                                    if (task.getException() != null) {
                                        Toast.makeText(RegisterActivity.this,
                                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
        });
    }

    private void registerUser() {
        Log.d(TAG, "createUserWithEmail:success");
        FirebaseUser user = mAuth.getCurrentUser();
        try {
            if (user != null)
                user.sendEmailVerification()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email gönderildi.");
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        RegisterActivity.this);
                                alertDialogBuilder.setTitle("Lütfen emailinizi onaylayın.");
                                alertDialogBuilder
                                        .setMessage("Mail kutunuza doğrulama kodu gönderildi, " +
                                                "lütfen linke tıklayın ve tekrar giriş yapın!")
                                        .setCancelable(false)
                                        .setPositiveButton("Giriş yap",
                                                (dialog, id) -> RegisterActivity.this.finish());
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        });
        } catch (Exception e) {
            Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void switchToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}































