package com.example.read.Room.Database;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.read.Room.Dao.BookDao;
import com.example.read.Room.Entity.Book;

@Database(entities = {Book.class}, version = 1,exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    private static BookDatabase INSTANCE;
    public static synchronized BookDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),BookDatabase.class,"book_database")
                    //.fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    public abstract BookDao getBookDao();
}
