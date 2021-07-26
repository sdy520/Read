package com.example.read.Room.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import com.example.read.Room.Dao.SearchHistoryDao;
import com.example.read.Room.Entity.SearchHistory;

@Database(entities = {SearchHistory.class}, version = 1,exportSchema = false)
public abstract class SearchHistoryDatabase extends RoomDatabase {
    private static SearchHistoryDatabase INSTANCE;
    public static synchronized SearchHistoryDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),SearchHistoryDatabase.class,"searchhistory_database")
                    //.fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    public abstract SearchHistoryDao getSearchHistoryDao();
}
