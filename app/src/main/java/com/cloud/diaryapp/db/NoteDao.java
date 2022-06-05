package com.cloud.diaryapp.db;

import static com.cloud.diaryapp.models.Note.NOTE_TABLE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cloud.diaryapp.models.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNote(Note note);

    @Query("DELETE FROM " + NOTE_TABLE)
    void deleteAll();

    @Delete
    void deleteNote(Note note);

    @Update
    void updateNote(Note note);

    @Query("SELECT * from " + NOTE_TABLE + " WHERE account_id = :accountId ORDER BY id ASC")
    LiveData<List<Note>> getAllNotes(String accountId);

    @Query("SELECT * from " + NOTE_TABLE + " WHERE is_backed_up = :isBackedUp " + " ORDER BY id ASC")
    LiveData<List<Note>> getBackedUpNotes(boolean isBackedUp);

    @Query("SELECT * FROM " + NOTE_TABLE + " WHERE id = :id")
    LiveData<Note> getNote(long id);

}




