package com.cloud.diaryapp.repository;


import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;


import androidx.lifecycle.LiveData;

import com.cloud.diaryapp.db.DiaryDatabase;
import com.cloud.diaryapp.db.NoteDao;
import com.cloud.diaryapp.models.Note;
import com.cloud.diaryapp.models.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DiaryRepository {

    public static final String TAG = "DiaryRepository";
    private final NoteDao mNoteDao;
    private final LiveData<List<Note>> mNotes;

    Application mApplication;
    private final DiaryDatabase mDb;

    public DiaryRepository(Application application) {
        mDb = DiaryDatabase.getDatabase(application);
        mNoteDao = mDb.mNoteDao();
        mNotes = mNoteDao.getAllNotes(User.uid);
        mApplication = application;
    }

    public LiveData<List<Note>> getAllNotes() {
        Log.d(TAG, "getAllNotes");
        return mNotes;
    }

    public LiveData<List<Note>> getBackedUpNotes(boolean isBackedUp) {
        Log.d(TAG, "getBackedUpNotes");
        return mNoteDao.getBackedUpNotes(isBackedUp);
    }

    public LiveData<Note> getNote(long id) {
        Log.d(TAG, "getNote " + id);
        return mNoteDao.getNote(id);
    }

    public void deleteNote(Note note) {
        new DeleteNoteAsyncTask(mDb).execute(note);
    }

    public void updateNote(Note note) {
        new UpdateNoteAsyncTask(mDb).execute(note);
        Log.d(TAG, "updateNote");
    }

    public void insertNote(Note note) {
        mNoteDao.insertNote(note);
    }

    public long insertNoteAndReturnId(Note note) {
        Callable<Long> insertCallable = () -> mNoteDao.insertNote(note);
        long rowId = 0;

        Future<Long> future = DiaryDatabase.databaseWriteExecutor.submit(insertCallable);
        try {
            rowId = future.get();
        } catch (InterruptedException | ExecutionException e1) {
            e1.printStackTrace();
        }
        Log.d(TAG, "NOTE INSERTED " + rowId);
        return rowId;
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {

        private final DiaryDatabase mDb;
        DeleteNoteAsyncTask(DiaryDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            Note note = params[0];
            if (note == null) return null;
            mDb.mNoteDao().deleteNote(note);

            Log.d(TAG, "Note Deleted " + note.getId());
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {

        private final DiaryDatabase mDb;

        UpdateNoteAsyncTask(DiaryDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            mDb.mNoteDao().updateNote(params[0]);
            return null;
        }
    }


}
