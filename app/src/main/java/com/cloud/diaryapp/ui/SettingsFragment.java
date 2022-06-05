package com.cloud.diaryapp.ui;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloud.diaryapp.R;
import com.cloud.diaryapp.databinding.FragmentSettingsBinding;
import com.cloud.diaryapp.models.User;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(User.uid + " approval", MODE_PRIVATE);
        binding.cbRead.setChecked(sharedPreferences.getBoolean(User.uid + " approval", false));

        binding.cbRead.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                Toast.makeText(requireContext(), "Bu ayarı açarsanız başkaları da sizin günlüklerinizi okuyabilir.", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putBoolean(User.uid + " approval", b).apply();
        });

        binding.btnLogout.setOnClickListener(view1 -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("isLogout", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences1.edit();
                        myEdit.putBoolean("isLogout", true).apply();
                        navigateLoginActivity();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Çıkış yapılsın mı?").setPositiveButton("Evet", dialogClickListener)
                    .setNegativeButton("İptal", dialogClickListener).show();
        });

        binding.blue.setOnClickListener(view12 -> {
            changeTextColor("#00BCD4");
        });

        binding.red.setOnClickListener(view12 -> {
            changeTextColor("#E91E63");
        });

        binding.dark.setOnClickListener(view12 -> {
            changeTextColor("#000000");
        });

        binding.purple.setOnClickListener(view12 -> {
            changeTextColor("#9C27B0");
        });
    }

    private void changeTextColor(String color){
        SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences(User.uid + " textColor", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences1.edit();
        myEdit.putString(User.uid + " textColor", color).apply();
        Toast.makeText(requireContext(), "Yazı rengi değiştirildi.", Toast.LENGTH_SHORT).show();
    }

    private void navigateLoginActivity() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}