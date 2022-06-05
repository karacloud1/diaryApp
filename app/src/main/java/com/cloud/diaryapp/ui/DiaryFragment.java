package com.cloud.diaryapp.ui;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.cloud.diaryapp.R;
import com.cloud.diaryapp.databinding.FragmentDiaryBinding;
import com.cloud.diaryapp.models.Note;
import com.cloud.diaryapp.models.User;
import com.cloud.diaryapp.viewmodel.NoteViewModel;

import java.util.Calendar;
import java.util.Objects;

public class DiaryFragment extends Fragment {

    private FragmentDiaryBinding binding;
    private NoteViewModel mViewModel;
    private Note mNote = new Note();

    public DiaryFragment() {
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
        setHasOptionsMenu(true);
        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(User.uid + " textColor", MODE_PRIVATE);
        String color = sharedPreferences.getString(User.uid + " textColor","#000000");
        binding.editTextNoteText.setTextColor(Color.parseColor(color));
        binding.editTextNoteTitle.setTextColor(Color.parseColor(color));
        mViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        long selectedNoteId = DiaryFragmentArgs.fromBundle(getArguments()).getId();
        if (selectedNoteId == -1) {
            mNote = new Note("", Calendar.getInstance().getTimeInMillis(), "");
            mViewModel.setCurrentNoteIsNew(true);
        } else {
            mViewModel.setCurrentNoteId(selectedNoteId);
            mViewModel.getNote(mViewModel.getCurrentNoteId()).observe(getViewLifecycleOwner(), this::displayViews);
        }

    }

    private void displayViews(Note note) {
        if (note == null) return;
        mNote = note;
        binding.editTextNoteText.setText(note.getText());
        binding.editTextNoteTitle.setText(note.getTitle());
    }

    private void deleteNote() {
        mViewModel.deleteNote(mNote);
        Toast.makeText(requireContext(), "Günlük silindi.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void saveNote() {
        String title = Objects.requireNonNull(binding.editTextNoteTitle.getText().toString());
        String text = Objects.requireNonNull(binding.editTextNoteText.getText().toString());
        if (title.isEmpty() || text.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen başlık ve içerik giriniz.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mNote.setTitle(title);
        mNote.setText(text);
        mNote.setAccountId(User.uid);
        mNote.setBackedUp(false);
        if (mViewModel.currentNoteIsNew()) {
            long id = mViewModel.insertNote(mNote);
            mNote.setId(id);
            mViewModel.setCurrentNoteIsNew(false);
            mViewModel.setCurrentNoteId(id);
        } else {
            mViewModel.updateNote(mNote);
        }
        Toast.makeText(requireContext(), "Günlük kaydedildi.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_save) {
            saveNote();
            navigateMainFragment();
        } else if (id == R.id.menu_delete) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = requireActivity().getCurrentFocus();
            if (view == null) {
                view = new View(requireContext());
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteNote();
                        navigateMainFragment();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Günlük silinsin mi?").setPositiveButton("Sil", dialogClickListener)
                    .setNegativeButton("İptal", dialogClickListener).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void navigateMainFragment() {
        if (DiaryFragment.this.getView() != null) {
            Navigation.findNavController(DiaryFragment.this.getView()).popBackStack();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.diary_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}