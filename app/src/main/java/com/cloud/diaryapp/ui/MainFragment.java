package com.cloud.diaryapp.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cloud.diaryapp.R;
import com.cloud.diaryapp.databinding.FragmentMainBinding;
import com.cloud.diaryapp.models.Note;
import com.cloud.diaryapp.models.User;
import com.cloud.diaryapp.viewmodel.MainViewModel;

import java.util.List;
import java.util.Random;


public class MainFragment extends Fragment implements NotesAdapter.OnNoteClickListener {
    private FragmentMainBinding binding;
    private List<Note> mNotes;
    private NotesAdapter mAdapter;
    private MainViewModel mViewModel;
    private long itemToRemovePos = -1;

    public MainFragment() {
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
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        binding.fab.setOnClickListener(view1 -> {
            NavDirections action =
                    MainFragmentDirections.actionMainFragmentToDiaryFragment(-1);
            if (MainFragment.this.getView() != null) {
                Navigation.findNavController(MainFragment.this.getView()).navigate(action);
            }
        });

        mViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            mAdapter.setNotes(notes, itemToRemovePos);
            mNotes = notes;
            itemToRemovePos = -1;
            if (notes != null && notes.size() > 0) {
                mViewModel.saveRandomNoteToFirestore(getRandomElement(notes));
            }
            mViewModel.isLoading.postValue(false);
        });
        mAdapter.setOnNoteDeleteListener(note -> mViewModel.deleteNote(note));


        mViewModel.isLoading.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.loading.setVisibility(View.VISIBLE);
            } else {
                binding.loading.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            NavDirections action =
                    MainFragmentDirections.actionMainFragmentToSettingsFragment();
            if (this.getView() != null) {
                Navigation.findNavController(this.getView()).navigate(action);
            }
            return true;
        }
        else if (id == R.id.action_explore) {
            SharedPreferences sharedPreferences = requireActivity()
                    .getSharedPreferences(User.uid + " approval", MODE_PRIVATE);
            if (sharedPreferences.getBoolean(User.uid + " approval", false)){
                NavDirections action =
                        MainFragmentDirections.actionMainFragmentToExploreDiaryFragment();
                if (this.getView() != null) {
                    Navigation.findNavController(this.getView()).navigate(action);
                }
                return true;    
            }
            Toast.makeText(requireContext(),
                    "Ayarlardan diğer insanların günlüklerini okuyu seçeneğini onaylayın.",
                    Toast.LENGTH_LONG).show();
            
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void init() {
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mAdapter = new NotesAdapter(requireContext(), requireActivity(), this);
        binding.rvDiaries.setAdapter(mAdapter);
        binding.rvDiaries.setLayoutManager(new LinearLayoutManager(requireContext()));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new NotesAdapter.SwipeToDeleteCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(binding.rvDiaries);
    }

    @Override
    public void onNoteClick(long noteId) {
        NavDirections action =
                MainFragmentDirections.actionMainFragmentToDiaryFragment(noteId);
        if (MainFragment.this.getView() != null) {
            Navigation.findNavController(MainFragment.this.getView()).navigate(action);
        }
    }

    public Note getRandomElement(List<Note> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }
}