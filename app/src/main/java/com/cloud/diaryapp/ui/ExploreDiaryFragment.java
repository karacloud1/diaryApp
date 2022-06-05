package com.cloud.diaryapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cloud.diaryapp.databinding.FragmentExploreDiaryBinding;
import com.cloud.diaryapp.viewmodel.MainViewModel;

public class ExploreDiaryFragment extends Fragment {
    private static final String TAG = "ExploreDiaryFragment";

    private FragmentExploreDiaryBinding binding;
    private MainViewModel mViewModel;

    public ExploreDiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExploreDiaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.editTextNoteTitle.setFocusable(false);
        binding.editTextNoteText.setFocusable(false);
        mViewModel.getRandomNoteFromFirestore().observe(getViewLifecycleOwner(), note -> {
            binding.editTextNoteText.setText(note.getText());
            binding.editTextNoteTitle.setText(note.getTitle());
            mViewModel.isLoading.postValue(false);
        });
        mViewModel.isLoading.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.loading.setVisibility(View.VISIBLE);
            } else {
                binding.loading.setVisibility(View.GONE);

            }
        });

        mViewModel.error.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.errorMessage.setVisibility(View.VISIBLE);
            } else {
                binding.errorMessage.setVisibility(View.GONE);

            }
        });

        mViewModel.toast.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Toast.makeText(requireContext(), "Bugün hiç günlük girilmemiş. İlk giren sen ol.", Toast.LENGTH_LONG).show();
            }
        });

    }
}

