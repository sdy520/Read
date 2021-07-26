/*package com.example.read.Room.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.read.Room.Entity.Book;
import com.example.read.Room.Dao.BookDao;
import com.example.read.Room.Database.BookDatabase;

import java.util.List;

public class BookRepository {
    private LiveData<List<Book>> allbookslive;
    private BookDao bookDao;

    public BookRepository(Context context) {
        BookDatabase bookDatabase=BookDatabase.getDatabase(context.getApplicationContext());
        bookDao=bookDatabase.getBookDao();
        allbookslive=bookDao.getAllBookLive();
    }
    public LiveData<List<Book>> getAllBookLive(){
        return allbookslive;
    }
}*/
