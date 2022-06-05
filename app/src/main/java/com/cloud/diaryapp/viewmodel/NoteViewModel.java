package com.cloud.diaryapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cloud.diaryapp.models.Note;
import com.cloud.diaryapp.repository.DiaryRepository;

public class NoteViewModel extends AndroidViewModel {

    private final DiaryRepository mRepository;
    private boolean isNewNote = false;
    private long currentNoteId = -1;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new DiaryRepository(application);
    }

    public LiveData<Note> getNote(long id) {
        return mRepository.getNote(id);
    }

    public long insertNote(Note note) {
        return mRepository.insertNoteAndReturnId(note);
    }

    public void updateNote(Note note) {
        mRepository.updateNote(note);
    }

    public void deleteNote(Note note) {
        mRepository.deleteNote(note);
    }

    public boolean currentNoteIsNew() {
        return isNewNote;
    }

    public void setCurrentNoteIsNew(boolean newNote) {
        isNewNote = newNote;
        Log.d("HRD", String.valueOf(newNote));
    }

    public long getCurrentNoteId() {
        return currentNoteId;
    }

    public void setCurrentNoteId(long id) {
        currentNoteId = id;
    }
}
