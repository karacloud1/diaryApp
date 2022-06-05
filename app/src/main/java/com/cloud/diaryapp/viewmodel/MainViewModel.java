package com.cloud.diaryapp.viewmodel;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cloud.diaryapp.models.Note;
import com.cloud.diaryapp.models.User;
import com.cloud.diaryapp.repository.DiaryRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";
    private final DiaryRepository mRepository;
    private final LiveData<List<Note>> mNotes;
    private final FirebaseFirestore db;
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> error = new MutableLiveData<>();
    public MutableLiveData<Boolean> toast = new MutableLiveData<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new DiaryRepository(application);
        mNotes = mRepository.getAllNotes();
        db = FirebaseFirestore.getInstance();
    }

    public void saveRandomNoteToFirestore(Note randomNote) {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences sharedPreferences = getApplication()
                .getSharedPreferences(User.uid + " day", Context.MODE_PRIVATE);
        int lastDay = sharedPreferences.getInt(User.uid + " day", 0);
        if (lastDay != currentDay && getApplication()
                .getSharedPreferences(User.uid + " approval", MODE_PRIVATE)
                .getBoolean(User.uid + " approval", false)) {
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putInt(User.uid + " day", currentDay).apply();
            Map<String, Object> note = new HashMap<>();
            note.put("title", randomNote.getTitle());
            note.put("text", randomNote.getText());
            note.put("accountId", randomNote.getAccountId());
            note.put("date", randomNote.getDay() + "-" + randomNote.getMonth() + "-" + randomNote.getYear());
            db.collection("diaries")
                    .add(note)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        SharedPreferences.Editor myEdit1 = sharedPreferences.edit();
                        myEdit1.putLong(User.uid + " lastSaveTime", Calendar.getInstance().getTimeInMillis()).apply();
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
        }
    }

    public MutableLiveData<Note> getRandomNoteFromFirestore() {
        isLoading.postValue(true);
        List<Note> notes = new ArrayList<>();
        MutableLiveData<Note> newNote = new MutableLiveData<>();
        java.util.Calendar c = java.util.Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy");
        String datetime = dateformat.format(c.getTime());
        db.collection("diaries").whereNotEqualTo("accountId", User.uid).whereEqualTo("date", datetime)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Note note = new Note();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            note.setText(Objects.requireNonNull(document.getString("text")));
                            note.setTitle(Objects.requireNonNull(document.getString("title")));
                            notes.add(note);
                            error.postValue(false);
                            toast.postValue(false);
                        }
                        if (notes.size() <= 0 || notes.get(0).getText() == null) {
                            isLoading.postValue(false);
                            error.postValue(false);
                            toast.postValue(true);
                        } else
                            newNote.postValue(getRandomElement(notes));
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        error.postValue(true);
                        isLoading.postValue(false);
                        toast.postValue(false);
                    }
                });

        return newNote;
    }

    public LiveData<List<Note>> getAllNotes() {
        isLoading.postValue(true);
        return mNotes;
    }

    public Note getRandomElement(List<Note> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public void deleteNote(Note note) {
        mRepository.deleteNote(note);
    }
}
