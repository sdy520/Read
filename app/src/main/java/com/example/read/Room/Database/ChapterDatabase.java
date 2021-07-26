package com.example.read.Room.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.read.Room.Dao.ChapterDao;
import com.example.read.Room.Entity.Chapter;


@Database(entities = {Chapter.class}, version = 1,exportSchema = false)
public abstract class ChapterDatabase extends RoomDatabase {
    private static ChapterDatabase INSTANCE;
    public static synchronized ChapterDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),ChapterDatabase.class,"chapter_database")
                    //.fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    public abstract ChapterDao getChapterDao();
}
