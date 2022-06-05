package com.cloud.diaryapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cloud.diaryapp.models.Note;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1)
public abstract class DiaryDatabase extends RoomDatabase {

    public static final String MY_DIARY_DATABASE = "my_diary_database";

    public abstract NoteDao mNoteDao();

    private static volatile DiaryDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DiaryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DiaryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DiaryDatabase.class, MY_DIARY_DATABASE)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
